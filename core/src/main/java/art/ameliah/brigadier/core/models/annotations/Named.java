package art.ameliah.brigadier.core.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate your method to name arguments in game.
 * <pre>
 * {@code
 * @Command
 * @Named(argument = "arg1", name = "age")
 * public boolean myCommand(CommandContext ctx, int age) {
 *  <code>
 *    }
 *
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(NamedContainer.class)
public @interface Named {

  /**
   * The argument you're changing the name for {@code argN}
   */
  String argument();

  String name();

}
