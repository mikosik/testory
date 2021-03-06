package org.testory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.givenTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;
import org.testory.Dummies.DummyClass;

public class Describe_Testory_givenTest {
  @Test
  public void should_mock_concrete_class() {
    class ConcreteClass {}
    class TestClass {
      ConcreteClass field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof ConcreteClass);
  }

  @Test
  public void should_mock_interface() {
    class TestClass {
      Interface field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof Interface);
  }

  @Test
  public void should_mock_abstract_class() {
    abstract class AbstractClass {}
    class TestClass {
      AbstractClass field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof AbstractClass);
  }

  @Test
  public void should_mock_object_class() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field instanceof Object);
  }

  @Test
  public void should_stub_to_string_to_return_field_name() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals("field", test.field.toString());
  }

  @Test
  public void should_stub_equals_to_match_same_mock() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.field.equals(test.field));
  }

  @Test
  public void should_stub_equals_to_not_match_not_same_mock() {
    class TestClass {
      Object field;
      Object otherField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertFalse(test.field.equals(test.otherField));
    assertFalse(test.otherField.equals(test.field));
  }

  @Test
  public void should_stub_equals_to_not_match_null() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertFalse(test.field.equals(null));
  }

  @Test
  public void should_stub_hashcode_to_obey_contract() {
    class TestClass {
      Object field, otherField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(test.field.hashCode(), test.field.hashCode());
    assertTrue(test.field.hashCode() != test.otherField.hashCode());
  }

  @Test
  public void should_skip_not_null() {
    class TestClass {
      Object field;
      String stringField = "value";
      Boolean booleanField = Boolean.TRUE;
      Character characterField = Character.valueOf((char) 1);
      Byte byteField = Byte.valueOf((byte) 1);
      Short shortField = Short.valueOf((short) 1);
      Integer integerField = Integer.valueOf(1);
      Long longField = Long.valueOf(1);
      Float floatField = Float.valueOf(1);
      Double doubleField = Double.valueOf(1);
    }
    TestClass test = new TestClass();
    Object object = new Object();
    test.field = object;
    givenTest(test);
    assertSame(object, test.field);
    assertEquals("value", test.stringField);
    assertEquals(Boolean.TRUE, test.booleanField);
    assertEquals(Character.valueOf((char) 1), test.characterField);
    assertEquals(Byte.valueOf((byte) 1), test.byteField);
    assertEquals(Short.valueOf((short) 1), test.shortField);
    assertEquals(Integer.valueOf(1), test.integerField);
    assertEquals(Long.valueOf(1), test.longField);
    assertEquals(Float.valueOf(1), test.floatField);
    assertEquals(Double.valueOf(1), test.doubleField);
  }

  @Test
  public void should_skip_primitive_equal_to_binary_zero() {
    class TestClass {
      boolean booleanField;
      char charField;
      byte byteField;
      short shortField;
      int intField;
      long longField;
      float floatField;
      double doubleField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.booleanField == false);
    assertTrue(test.charField == (char) 0);
    assertTrue(test.byteField == 0);
    assertTrue(test.shortField == 0);
    assertTrue(test.intField == 0);
    assertTrue(test.longField == 0);
    assertTrue(test.floatField == 0);
    assertTrue(test.doubleField == 0);
  }

  @Test
  public void should_skip_primitive_not_equal_to_binary_zero() {
    class TestClass {
      boolean booleanField = true;
      char charField = 'a';
      byte byteField = 1;
      short shortField = 1;
      int intField = 1;
      long longField = 1;
      float floatField = 1;
      double doubleField = 1;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertTrue(test.booleanField == true);
    assertTrue(test.charField == 'a');
    assertTrue(test.byteField == 1);
    assertTrue(test.shortField == 1);
    assertTrue(test.intField == 1);
    assertTrue(test.longField == 1);
    assertTrue(test.floatField == 1);
    assertTrue(test.doubleField == 1);
  }

  @Test
  public void should_fail_for_final_class() {
    final class FinalClass {}
    class TestClass {
      @SuppressWarnings("unused")
      FinalClass field;
    }
    TestClass test = new TestClass();
    try {
      givenTest(test);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_inject_string() {
    class TestClass {
      String field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals("field", test.field);
  }

  @Test
  public void should_inject_wrappers() {
    class TestClass {
      Void voidField;
      Boolean booleanField;
      Character characterField;
      Byte byteField;
      Short shortField;
      Integer integerField;
      Long longField;
      Float floatField;
      Double doubleField;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(null, test.voidField);
    assertEquals(Boolean.FALSE, test.booleanField);
    assertEquals(Character.valueOf((char) 0), test.characterField);
    assertEquals(Byte.valueOf((byte) 0), test.byteField);
    assertEquals(Short.valueOf((short) 0), test.shortField);
    assertEquals(Integer.valueOf(0), test.integerField);
    assertEquals(Long.valueOf(0), test.longField);
    assertEquals(Float.valueOf(0), test.floatField);
    assertEquals(Double.valueOf(0), test.doubleField);
  }

  @Test
  public void should_inject_arrays() {
    class TestClass {
      Object[] objects;
      String[] strings;
      Integer[] wrapperIntegers;
      boolean[] booleans;
      char[] characters;
      byte[] bytes;
      short[] shorts;
      int[] integers;
      long[] longs;
      float[] floats;
      double[] doubles;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(1, test.objects.length);
    assertTrue(test.objects[0] instanceof Object);
    assertArrayEquals(new Integer[] { Integer.valueOf(0) }, test.wrapperIntegers);
    assertArrayEquals(new String[] { "strings" }, test.strings);
    assertEquals(1, test.booleans.length);
    assertEquals((new boolean[1])[0], test.booleans[0]);
    assertArrayEquals(new char[1], test.characters);
    assertArrayEquals(new byte[1], test.bytes);
    assertArrayEquals(new short[1], test.shorts);
    assertArrayEquals(new int[1], test.integers);
    assertEquals(1, test.longs.length);
    assertEquals((new long[1])[0], test.longs[0]);
    assertEquals(1, test.floats.length);
    assertEquals((new float[1])[0], test.floats[0], 0.0f);
    assertEquals(1, test.doubles.length);
    assertEquals((new double[1])[0], test.doubles[0], 0.0);
  }

  @Test
  public void should_inject_class() {
    class TestClass {
      Class<?> field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(DummyClass.class, test.field);
  }

  @Test
  public void should_inject_method() throws NoSuchMethodException {
    class TestClass {
      Method field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(DummyClass.class.getDeclaredMethod("dummyMethod"), test.field);
  }

  @Test
  public void should_inject_field() throws NoSuchFieldException {
    class TestClass {
      Field field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    assertEquals(DummyClass.class.getDeclaredField("dummyField"), test.field);
  }

  public static interface Interface {}
}
