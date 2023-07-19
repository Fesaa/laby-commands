package art.ameliah.laby.addons.library.commands.core.models.annotations;

import art.ameliah.laby.addons.library.commands.core.models.CommandContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add a method as check called before the command is run. Takes one argument,
 * {@link CommandContext}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CheckContainer.class)
public @interface Check {

  String method();

  /**
   * Method called to obtain Component to display on fail
   */
  String failedMethod() default "noPermissionComponent";

}
