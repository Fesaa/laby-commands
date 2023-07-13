package art.ameliah.brigadier.core.models.types;

import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.exceptions.CommandException;
import art.ameliah.brigadier.core.models.exceptions.SyntaxException;
import art.ameliah.brigadier.core.service.CommandService;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper around the Mojang ArgumentType used to implement custom argument Register with
 * {@link CommandService#registerCustomArgumentType(Class, CustomArgumentType)}
 *
 * @param <T> The wrapped type
 * @param <S> Your used CommandContext
 */
public interface CustomArgumentType<T, S extends CommandContext> {

  T parse(StringReaderWrapper reader) throws SyntaxException, CommandException;

  Class<S> getCommandContextClass();

  boolean stringCollector(char c);

  default CompletableFuture<CustomSuggestions> listSuggestions(final S ctx) {
    return CustomSuggestions.empty();
  }

  default Collection<String> getExamples() {
    return Collections.emptyList();
  }

}
