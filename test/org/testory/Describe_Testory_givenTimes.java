package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.givenTimes;

import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;

public class Describe_Testory_givenTimes {
  private int times, counter, failTime;
  private Exception exception;

  @Before
  public void before() {
    times = 5;
    counter = 0;
    exception = new Exception("exception");
  }

  @Test
  public void should_call_closure_many_times() {
    givenTimes(times, new Closure() {
      public Void invoke() {
        counter++;
        return null;
      }
    });
    assertEquals(times, counter);
  }

  @Test
  public void should_call_closure_zero_times() {
    givenTimes(0, new Closure() {
      public Void invoke() {
        counter++;
        return null;
      }
    });
    assertEquals(0, counter);
  }

  @Test
  public void should_fail_gently_if_closure_throws() {
    times = 5;
    failTime = 3;
    try {
      givenTimes(times, new Closure() {
        public Void invoke() throws Throwable {
          counter++;
          if (counter == failTime) {
            throw exception;
          }
          return null;
        }
      });
      fail();
    } catch (Throwable throwable) {
      assertTrue(throwable instanceof RuntimeException);
      assertSame(exception, throwable.getCause());
      assertEquals(failTime, counter);
    }
  }

  @Test
  public void should_call_method_many_times() {
    givenTimes(times, new Runnable() {
      public void run() {
        counter++;
      }
    }).run();
    assertEquals(times, counter);
  }

  @Test
  public void should_call_method_zero_times() {
    times = 0;
    givenTimes(0, new Runnable() {
      public void run() {
        counter++;
      }
    }).run();
    assertEquals(times, counter);
  }

  @Test
  public void should_fail_if_method_throws() {
    times = 5;
    failTime = 3;
    try {
      givenTimes(times, new Callable<Object>() {
        public Object call() throws Exception {
          counter++;
          if (counter == failTime) {
            throw exception;
          }
          return null;
        }
      }).call();
      fail();
    } catch (Throwable throwable) {
      assertSame(exception, throwable);
      assertEquals(failTime, counter);
    }
  }

  @Test
  public void should_fail_calling_closure_negative_number_of_times() {
    try {
      givenTimes(-1, new Closure() {
        public Object invoke() {
          return null;
        }
      });
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_fail_calling_method_negative_number_of_times() {
    try {
      givenTimes(-1, new Runnable() {
        public void run() {}
      });
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_fail_for_null_closure() {
    try {
      givenTimes(times, null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void should_fail_for_null_object() {
    try {
      givenTimes(times, (Object) null);
      fail();
    } catch (TestoryException e) {}
  }
}
