package app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// BEGIN (write your solution here)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Inspect {
    String level() default "debug";
}
// END