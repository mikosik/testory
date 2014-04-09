package org.testory.util;

import static java.util.Collections.nCopies;
import static org.testory.common.CharSequences.join;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.tryWrap;
import static org.testory.common.Collections.last;
import static org.testory.common.Matchers.arrayOf;
import static org.testory.common.Matchers.equalDeep;
import static org.testory.common.Matchers.listOf;
import static org.testory.common.Matchers.same;
import static org.testory.common.Objects.print;
import static org.testory.proxy.Invocations.invocationOf;
import static org.testory.util.Uniques.unique;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.testory.TestoryException;
import org.testory.common.Matcher;
import org.testory.common.Matchers;
import org.testory.common.Matchers.ProxyMatcher;
import org.testory.proxy.Invocation;

public class Any {
  private final Class<?> type;
  private final Matcher matcher;
  private final Object token;

  private Any(Class<?> type, Matcher matcher, Object token) {
    this.type = type;
    this.matcher = matcher;
    this.token = token;
  }

  public static Any any(Class<?> type, Matcher matcher) {
    checkNotNull(type);
    checkNotNull(matcher);
    return new Any(type, matcher, unique(tryWrap(type)));
  }

  public boolean mustBe(Object argument) {
    return token == argument;
  }

  public boolean couldBe(Object argument) {
    return true;
  }

  public Object token() {
    return token;
  }

  public static Matcher solveInvocationMatcher(List<Any> anys, Invocation invocation) {
    boolean repacking = invocation.method.isVarArgs()
        && !last(anys).mustBe(last(invocation.arguments));
    List<Object> arguments = repacking
        ? unpackVarargs(invocation.arguments)
        : invocation.arguments;
    List<Boolean> solution = solve(anys, arguments);
    List<Matcher> matchers = matcherize(solution, anys, arguments);
    List<Matcher> argumentsMatchers = repacking
        ? packVarargs(invocation.method.getParameterTypes().length, matchers)
        : matchers;
    return invocationMatcher(invocation.method, invocation.instance, argumentsMatchers);
  }

  private static Matcher invocationMatcher(final Method method, final Object instance,
      final List<Matcher> arguments) {
    Matcher target = invocationOf(equalDeep(method), same(instance), listOf(arguments));
    return new ProxyMatcher(target) {
      public String toString() {
        return instance + "." + method.getName() + "(" + join(", ", arguments) + ")";
      }
    };
  }

  private static List<Boolean> solve(List<Any> anys, List<Object> arguments) {
    int firstAvailableArg = 0;
    int firstAvailableAny = 0;
    List<Boolean> solution = new ArrayList<Boolean>(arguments.size());

    for (int i = 0; i < anys.size(); i++) {
      Any any = anys.get(i);
      int foundIndex = find(any, arguments);
      if (foundIndex != -1) {
        List<Any> anysSublist = anys.subList(firstAvailableAny, i);
        List<Object> argumentsSublist = arguments.subList(firstAvailableArg, foundIndex);
        solution.addAll(solveHidden(anysSublist, argumentsSublist));
        solution.add(true);
        firstAvailableArg = foundIndex + 1;
        firstAvailableAny = i + 1;
      }
    }
    List<Any> anysSublist = anys.subList(firstAvailableAny, anys.size());
    List<Object> argumentsSublist = arguments.subList(firstAvailableArg, arguments.size());
    solution.addAll(solveHidden(anysSublist, argumentsSublist));
    return solution;
  }

  private static int find(Any any, List<Object> arguments) {
    for (int i = 0; i < arguments.size(); i++) {
      if (any.mustBe(arguments.get(i))) {
        return i;
      }
    }
    return -1;
  }

  private static Collection<? extends Boolean> solveHidden(List<Any> anys, List<Object> arguments) {
    if (anys.size() == 0) {
      return nCopies(arguments.size(), false);
    }
    if (anys.size() == arguments.size()) {
      return nCopies(arguments.size(), true);
    }
    throw new TestoryException();
  }

  private static List<Object> unpackVarargs(List<?> packed) {
    ArrayList<Object> unpacked = new ArrayList<Object>();
    unpacked.addAll(packed.subList(0, packed.size() - 1));
    unpacked.addAll(Arrays.asList((Object[]) packed.get(packed.size() - 1)));
    return unpacked;
  }

  private static List<Matcher> packVarargs(int length, List<Matcher> unpacked) {
    List<Matcher> packed = new ArrayList<Matcher>();
    packed.addAll(unpacked.subList(0, length - 1));
    packed.add(arrayOf(unpacked.subList(length - 1, unpacked.size())));
    return packed;
  }

  private static List<Matcher> matcherize(List<Boolean> solution, List<Any> anys,
      List<Object> arguments) {
    List<Any> anysQueue = new ArrayList<Any>(anys);
    List<Matcher> matchers = new ArrayList<Matcher>();
    for (int i = 0; i < arguments.size(); i++) {
      Matcher matcher = solution.get(i)
          ? asMatcher(anysQueue.remove(0))
          : asMatcher(arguments.get(i));
      matchers.add(matcher);
    }
    return matchers;
  }

  private static Matcher asMatcher(final Object argument) {
    return new ProxyMatcher(equalDeep(argument)) {
      public String toString() {
        return print(argument);
      }
    };
  }

  private static Matcher asMatcher(final Any any) {
    return new ProxyMatcher(any.matcher) {
      public String toString() {
        return any.matcher == Matchers.anything
            ? "any(" + any.type.getName() + ")"
            : "any(" + any.type.getName() + ", " + any.matcher + ")";
      }
    };
  }
}
