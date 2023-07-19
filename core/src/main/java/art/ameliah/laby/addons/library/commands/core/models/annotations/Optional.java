package art.ameliah.laby.addons.library.commands.core.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates the argument as optional. All following arguments must be optional ass well.
 * <pre>
 * {@code
 * @Command
 * public boolean myCommand(CommandContext ctx, String name, @Optional @Bound(min_int = 18) int age) {
 *    if (age == null) {
 *      <code>
 *    } else {
 *      <code>
 *    }
 *  }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {

}