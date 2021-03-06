package org.testory;

import static org.testory.Dummies.dummy;
import static org.testory.Formats.formatSection;
import static org.testory.WhenEffect.whenEffect;
import static org.testory.common.Closures.invoked;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Matchers.match;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Throwables.gently;
import static org.testory.mock.Invocations.invoke;
import static org.testory.mock.Invocations.on;
import static org.testory.mock.Mocks.mock;
import static org.testory.mock.Typing.typing;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;

import org.testory.common.Closure;
import org.testory.common.Nullable;
import org.testory.doc.TestoryDemo;
import org.testory.doc.TestoryTutorial;
import org.testory.mock.Handler;
import org.testory.mock.Invocation;
import org.testory.mock.Invocations;

/**
 * @see TestoryTutorial
 * @see TestoryDemo
 */
public class Testory {
  public static void givenTest(Object test) {
    for (final Field field : test.getClass().getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers())) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            field.setAccessible(true);
            return null;
          }
        });
        try {
          if (field.get(test) == null) {
            field.set(test, dummy(field.getType(), field.getName()));
          }
        } catch (RuntimeException e) {
          throw new TestoryException(e);
        } catch (IllegalAccessException e) {
          throw new Error(e);
        }
      }
    }
  }

  /**
   * Reserved for future use.
   */
  @Deprecated
  public static Closure given(Closure closure) {
    throw new TestoryException("\n\tgiven(Closure) is confusing, do not use it\n");
  }

  public static <T> T given(T object) {
    return object;
  }

  public static void given(boolean primitive) {}

  public static void given(double primitive) {}

  public static <T> T givenTry(final T object) {
    checkUsage(object != null);
    return (T) mock(typing(object.getClass(), new HashSet<Class<?>>()), new Handler() {
      public Object handle(Invocation invocation) {
        Invocation onObjectInvocation = on(object, invocation);
        try {
          return invoke(onObjectInvocation);
        } catch (Throwable e) {
          return null;
        }
      }
    });
  }

  public static void givenTimes(int number, Closure closure) {
    checkUsage(number >= 0);
    checkUsage(closure != null);
    for (int i = 0; i < number; i++) {
      try {
        closure.invoke();
      } catch (Throwable throwable) {
        throw gently(throwable);
      }
    }
  }

  public static <T> T givenTimes(final int number, final T object) {
    checkUsage(number >= 0);
    checkUsage(object != null);
    return (T) mock(typing(object.getClass(), new HashSet<Class<?>>()), new Handler() {
      public Object handle(final Invocation invocation) throws Throwable {
        final Invocation onObjectInvocation = on(object, invocation);
        for (int i = 0; i < number; i++) {
          invoke(onObjectInvocation);
        }
        return null;
      }
    });
  }

  public static <T> T when(final T object) {
    whenEffect.set(new Closure() {
      public Object invoke() {
        return object;
      }
    });
    try {
      return (T) mock(typing(object.getClass(), new HashSet<Class<?>>()), new Handler() {
        public Object handle(final Invocation invocation) {
          final Invocation onObjectInvocation = on(object, invocation);
          Closure effect = invoked(new Closure() {
            public Object invoke() throws Throwable {
              return Invocations.invoke(onObjectInvocation);
            }
          });
          whenEffect.set(effect);
          return null;
        }
      });
    } catch (RuntimeException e) {
      return null;
    }
  }

  public static void when(Closure closure) {
    checkUsage(closure != null);
    whenEffect.set(invoked(closure));
  }

  public static void when(boolean value) {
    when((Object) value);
  }

  public static void when(char value) {
    when((Object) value);
  }

  public static void when(byte value) {
    when((Object) value);
  }

  public static void when(short value) {
    when((Object) value);
  }

  public static void when(int value) {
    when((Object) value);
  }

  public static void when(long value) {
    when((Object) value);
  }

  public static void when(float value) {
    when((Object) value);
  }

  public static void when(double value) {
    when((Object) value);
  }

  public static void thenReturned(@Nullable Object objectOrMatcher) {
    Closure effect = getWhenEffect();
    Object object;
    try {
      object = effect.invoke();
    } catch (Throwable throwable) {
      throw assertionError("\n" //
          + formatSection("expected returned", objectOrMatcher) //
          + formatSection("but thrown", throwable));
    }
    if (!areEqualDeep(objectOrMatcher, object)
        && !(objectOrMatcher != null && isMatcher(objectOrMatcher) && match(objectOrMatcher, object))) {
      throw assertionError("\n" //
          + formatSection("expected returned", objectOrMatcher) //
          + formatSection("but returned", object));
    }
  }

  public static void thenReturned(boolean value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(char value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(byte value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(short value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(int value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(long value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(float value) {
    thenReturned((Object) value);
  }

  public static void thenReturned(double value) {
    thenReturned((Object) value);
  }

  public static void thenReturned() {
    Closure effect = getWhenEffect();
    try {
      effect.invoke();
    } catch (Throwable throwable) {
      throw assertionError("\n" //
          + formatSection("expected returned", "") //
          + formatSection("but thrown", throwable));
    }
  }

  public static void thenThrown(Object matcher) {
    checkUsage(matcher != null);
    checkUsage(isMatcher(matcher));
    Closure effect = getWhenEffect();
    Object object;
    try {
      object = effect.invoke();
    } catch (Throwable throwable) {
      if (!match(matcher, throwable)) {
        throw assertionError("\n" //
            + formatSection("expected thrown throwable matching", matcher) //
            + formatSection("but thrown", throwable));
      }
      return;
    }
    throw assertionError("\n" //
        + formatSection("expected thrown throwable matching", matcher) //
        + formatSection("but returned", object));
  }

  public static void thenThrown(Throwable expectedThrowable) {
    checkUsage(expectedThrowable != null);
    Closure effect = getWhenEffect();
    Object object;
    try {
      object = effect.invoke();
    } catch (Throwable throwable) {
      if (!areEqualDeep(expectedThrowable, throwable)) {
        throw assertionError("\n" //
            + formatSection("expected thrown", expectedThrowable) //
            + formatSection("but thrown", throwable));
      }
      return;
    }
    throw assertionError("\n" //
        + formatSection("expected thrown", expectedThrowable) //
        + formatSection("but returned", object));
  }

  public static void thenThrown(Class<? extends Throwable> type) {
    checkUsage(type != null);
    Closure effect = getWhenEffect();
    Object object;
    try {
      object = effect.invoke();
    } catch (Throwable throwable) {
      if (!type.isInstance(throwable)) {
        throw assertionError("\n" //
            + formatSection("expected thrown instance of", type.getName()) //
            + formatSection("but thrown instance of", throwable.getClass().getName()));
      }
      return;
    }
    throw assertionError("\n" //
        + formatSection("expected thrown instance of", type.getName()) //
        + formatSection("but returned", object));
  }

  public static void then(boolean condition) {
    if (!condition) {
      throw assertionError("\n" //
          + formatSection("expected", "true") //
          + formatSection("but was", "false"));
    }
  }

  public static void thenThrown() {
    Closure effect = getWhenEffect();
    Object object;
    try {
      object = effect.invoke();
    } catch (Throwable throwable) {
      return;
    }
    throw assertionError("\n" //
        + formatSection("expected thrown", "") //
        + formatSection("but returned", object));
  }

  public static <T> void then(@Nullable T object, Object matcher) {
    checkUsage(matcher != null);
    checkUsage(isMatcher(matcher));
    if (!match(matcher, object)) {
      throw assertionError("\n" //
          + formatSection("expected object matching", matcher) //
          + formatSection("but was", object));
    }
  }

  public static <T> void thenEqual(@Nullable Object object, @Nullable Object expected) {
    if (!areEqualDeep(object, expected)) {
      throw assertionError("\n" //
          + formatSection("expected", expected) //
          + formatSection("but was", object));
    }
  }

  private static void checkUsage(boolean condition) {
    if (!condition) {
      throw new TestoryException();
    }
  }

  private static TestoryAssertionError assertionError(String message) {
    TestoryAssertionError error = new TestoryAssertionError(message);
    cloakStackTrace(error);
    return error;
  }

  private static void cloakStackTrace(Throwable throwable) {
    StackTraceElement[] stackTrace = throwable.getStackTrace();

    int index = -1;
    for (int i = stackTrace.length - 1; i >= 0; i--) {
      if (stackTrace[i].getClassName().equals(Testory.class.getName())) {
        index = i;
        break;
      }
    }
    if (index == -1 || index == stackTrace.length - 1) {
      throw new Error();
    }
    throwable.setStackTrace(new StackTraceElement[] { stackTrace[index + 1] });
  }

  private static Closure getWhenEffect() {
    Closure effect = whenEffect.get();
    checkUsage(effect != null);
    return effect;
  }
}
