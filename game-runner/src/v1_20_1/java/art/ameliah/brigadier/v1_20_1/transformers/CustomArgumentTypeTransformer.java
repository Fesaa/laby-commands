package art.ameliah.brigadier.v1_20_1.transformers;

import art.ameliah.brigadier.core.models.custumTypes.CustomArgumentType;
import art.ameliah.brigadier.core.models.custumTypes.CustomSuggestion;
import art.ameliah.brigadier.core.models.custumTypes.CustomSuggestions;
import art.ameliah.brigadier.core.models.exceptions.SyntaxException;
import art.ameliah.brigadier.core.utils.Utils;
import art.ameliah.brigadier.v1_20_1.VersionedCommandContext;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.commands.SharedSuggestionProvider;

public class CustomArgumentTypeTransformer {


  public static <T extends CustomArgumentType<S>, S> ArgumentType<S> transform(
      T customArgumentType) {
    return new ArgumentType<S>() {

      @Override
      public Collection<String> getExamples() {
        return customArgumentType.getExamples();
      }

      @Override
      public <U> CompletableFuture<Suggestions> listSuggestions(final CommandContext<U> context,
          final SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof SharedSuggestionProvider)) {
          return Suggestions.empty();
        }

        CommandContext<SharedSuggestionProvider> ctx = (CommandContext<SharedSuggestionProvider>) context;
        CompletableFuture<CustomSuggestions> customSuggestionsCompletableFuture = customArgumentType.listSuggestions(
            new VersionedCommandContext(ctx));

        CustomSuggestions suggestions;
        try {
          suggestions = customSuggestionsCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
          System.out.println("Suggestions couldn't complete. Returning empty;" + Utils.stackTraceToString(e.getStackTrace()));
          return Suggestions.empty();
        }

        for (CustomSuggestion suggestion : suggestions.getSuggestions()) {
          builder.suggest(suggestion.getText());
        }

        return builder.buildFuture();
      }

      @Override
      public S parse(StringReader reader) throws CommandSyntaxException {
        try {
          int start = reader.getCursor();
          while (reader.canRead() && customArgumentType.stringCollector(reader.peek())) {
            reader.skip();
          }
          return customArgumentType.parse(reader.getString().substring(start, reader.getCursor()));
        } catch (SyntaxException e) {
          throw new SimpleCommandExceptionType(
              new LiteralMessage(e.getMessage())).createWithContext(reader);
        }
      }
    };
  }

}
