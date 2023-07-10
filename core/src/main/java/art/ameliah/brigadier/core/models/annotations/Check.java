package art.ameliah.brigadier.core.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add a method as check called before the command is run. Takes one argument, CommandContext
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CheckContainer.class)
public @interface Check {

  String method();

  String errorMethod() default "noPermissionComponent";

}
