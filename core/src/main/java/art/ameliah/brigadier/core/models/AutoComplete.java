package art.ameliah.brigadier.core.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Connect a method to a parameter for command suggestions. The return type must be an array that
 * can be cast to Strings Arguments names are `argN`
 * <pre>
 * {@code
 * @Command
 * @AutoComplete(method = "myAutoCompleteMethod", parameterName = "arg1")
 * public boolean myCommand(CommandContext ctx, String names) {
 *  <code>
 *    }
 *
 * public String[] myAutoCompleteMethod(CommandContext ctx) {
 *   return new String[]{"Amelia", "Eva", "Milan", "Nico"};
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(AutoCompleteContainer.class)
public @interface AutoComplete {

  String method();

  String parameterName();
}

