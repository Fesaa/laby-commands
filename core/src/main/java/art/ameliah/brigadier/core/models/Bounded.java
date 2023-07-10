package art.ameliah.brigadier.core.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Bounded {

  int min_int() default Integer.MIN_VALUE;

  int max_int() default Integer.MAX_VALUE;

  float min_float() default Float.MIN_VALUE;

  float max_float() default Float.MAX_VALUE;

  double min_double() default Double.MIN_VALUE;

  double max_double() default Double.MAX_VALUE;

}
