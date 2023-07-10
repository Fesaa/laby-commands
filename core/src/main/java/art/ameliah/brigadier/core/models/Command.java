package art.ameliah.brigadier.core.models;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

/**
 * Annotates a method as a command. Use the parent attribute to link it further Note: A parent with
 * arguments cannot have sub-commands
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

  @NotNull String parent() default "";

}