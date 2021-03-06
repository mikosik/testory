package org.testory.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.mock.Typing.typing;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class Describe_Typing {
  private Class<?> superclass;
  private Set<? extends Class<?>> interfaces;
  private Typing typing;

  @Before
  public void before() {
    superclass = Object.class;
    interfaces = classes(InterfaceA.class, InterfaceB.class);
  }

  @Test
  public void should_get_concrete_superclass() {
    class ConcreteClass {}
    superclass = ConcreteClass.class;
    typing = typing(superclass, interfaces);
    assertEquals(superclass, typing.superclass);
  }

  @Test
  public void should_get_abstract_superclass() {
    abstract class AbstractClass {}
    superclass = AbstractClass.class;
    typing = typing(superclass, interfaces);
    assertEquals(superclass, typing.superclass);
  }

  @Test
  public void should_get_interfaces() {
    typing = typing(superclass, interfaces);
    assertEquals(interfaces, typing.interfaces);
  }

  @Test
  public void should_get_no_interfaces() {
    interfaces = Collections.unmodifiableSet(new HashSet<Class<?>>());
    typing = typing(superclass, interfaces);
    assertEquals(interfaces, typing.interfaces);
  }

  @Test
  public void should_fail_for_interface_as_superclass() {
    superclass = Interface.class;
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_annotation_as_superclass() {
    superclass = AnnotationClass.class;
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_primitive_type_as_superclass() {
    superclass = int.class;
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_array_as_superclass() {
    superclass = Object[].class;
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_concrete_class_as_interface() {
    class ConcreteClass {}
    interfaces = classes(ConcreteClass.class);
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_abstract_class_as_interface() {
    abstract class AbstractClass {}
    interfaces = classes(AbstractClass.class);
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_annotation_as_interface() {
    interfaces = classes(AnnotationClass.class);
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_primitive_type_as_interface() {
    interfaces = classes(int.class);
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_array_as_interface() {
    interfaces = classes(Object[].class);
    try {
      typing(superclass, interfaces);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_null_superclass() {
    try {
      typing(null, interfaces);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void should_fail_for_null_interfaces() {
    try {
      typing(superclass, null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static Set<Class<?>> classes(Class<?>... classes) {
    return new HashSet<Class<?>>(Arrays.asList(classes));
  }

  private interface Interface {}

  private interface InterfaceA {}

  private interface InterfaceB {}

  private @interface AnnotationClass {}
}
