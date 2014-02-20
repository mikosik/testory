package org.testory.common;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Classes {
  // TODO move tests from Invocation to Classes
  public static boolean isAssignableTo(Class<?> type, @Nullable Object instance) {
    checkNotNull(type);
    return type.isPrimitive()
        ? isConvertibleTo(type, instance)
        : instance == null || type.isAssignableFrom(instance.getClass());
  }

  private static boolean isConvertibleTo(Class<?> type, Object instance) {
    checkArgument(type.isPrimitive());
    if (type == void.class) {
      return false;
    }
    try {
      Method method = PrimitiveMethods.class.getDeclaredMethod("method", type);
      method.setAccessible(true);
      method.invoke(null, instance);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    } catch (NoSuchMethodException e) {
      throw new Error(e);
    } catch (IllegalAccessException e) {
      throw new Error(e);
    } catch (InvocationTargetException e) {
      throw new Error(e);
    }
  }

  @SuppressWarnings("unused")
  private static class PrimitiveMethods {
    private static void method(byte argument) {}

    private static void method(short argument) {}

    private static void method(int argument) {}

    private static void method(long argument) {}

    private static void method(float argument) {}

    private static void method(double argument) {}

    private static void method(boolean argument) {}

    private static void method(char argument) {}
  }

  // TODO write tests
  public static boolean couldReturn(@Nullable Object object, Method method) {
    checkNotNull(method);
    return canReturn(object, method) || method.getReturnType() == void.class && object == null;
  }

  // TODO write tests
  public static boolean canReturn(@Nullable Object object, Method method) {
    checkNotNull(method);
    return isAssignableTo(method.getReturnType(), object);
  }

  // TODO write tests
  public static boolean canThrow(Throwable throwable, Method method) {
    checkNotNull(throwable);
    checkNotNull(method);
    for (Class<?> exceptionType : method.getExceptionTypes()) {
      if (exceptionType.isInstance(throwable)) {
        return true;
      }
    }
    return throwable instanceof RuntimeException || throwable instanceof Error;
  }

  // TODO write tests
  public static <T> T zeroOrNull(Class<T> type) {
    checkNotNull(type);
    return (T) zeroes.get(type);
  }

  private static final Map<Class<?>, Object> zeroes = zeroes();

  private static Map<Class<?>, Object> zeroes() {
    Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
    map.put(boolean.class, Boolean.valueOf(false));
    map.put(char.class, Character.valueOf((char) 0));
    map.put(byte.class, Byte.valueOf((byte) 0));
    map.put(short.class, Short.valueOf((short) 0));
    map.put(int.class, Integer.valueOf(0));
    map.put(long.class, Long.valueOf(0));
    map.put(float.class, Float.valueOf(0));
    map.put(double.class, Double.valueOf(0));
    map.put(Boolean.class, Boolean.valueOf(false));
    map.put(Character.class, Character.valueOf((char) 0));
    map.put(Byte.class, Byte.valueOf((byte) 0));
    map.put(Short.class, Short.valueOf((short) 0));
    map.put(Integer.class, Integer.valueOf(0));
    map.put(Long.class, Long.valueOf(0));
    map.put(Float.class, Float.valueOf(0));
    map.put(Double.class, Double.valueOf(0));
    return Collections.unmodifiableMap(map);
  }
}