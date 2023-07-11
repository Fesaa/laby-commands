package art.ameliah.brigadier.core.models.custumTypes;

import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.exceptions.SyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public interface CustomArgumentType<T, S extends CommandContext> {

  T parse(String string) throws SyntaxException;

  Class<S> getCommandContextClass();

  boolean stringCollector(char c);

  default CompletableFuture<CustomSuggestions> listSuggestions(final S ctx) {
    return CustomSuggestions.empty();
  }

  default Collection<String> getExamples() {
    return Collections.emptyList();
  }

}
