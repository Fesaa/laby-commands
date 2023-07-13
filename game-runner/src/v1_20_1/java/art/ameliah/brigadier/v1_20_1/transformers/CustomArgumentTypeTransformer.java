package art.ameliah.brigadier.v1_20_1.transformers;

import art.ameliah.brigadier.core.models.types.CustomArgumentType;
import art.ameliah.brigadier.core.models.types.CustomSuggestion;
import art.ameliah.brigadier.core.models.types.CustomSuggestions;
import art.ameliah.brigadier.core.models.types.StringReaderWrapper;
import art.ameliah.brigadier.core.models.exceptions.CommandException;
import art.ameliah.brigadier.core.models.exceptions.SyntaxException;
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
import javax.inject.Singleton;
import net.labymod.api.util.I18n;
import net.minecraft.commands.SharedSuggestionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CustomArgumentTypeTransformer {

  private static final Logger logger = LoggerFactory.getLogger(CustomArgumentTypeTransformer.class);


  public static <T extends CustomArgumentType<S, U>, S, U extends art.ameliah.brigadier.core.models.CommandContext> ArgumentType<S> transform(
      T customArgumentType) {
    return new ArgumentType<>() {

      @Override
      public Collection<String> getExamples() {
        return customArgumentType.getExamples();
      }

      @Override
      public <V> CompletableFuture<Suggestions> listSuggestions(final CommandContext<V> context,
          final SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof SharedSuggestionProvider)) {
          return Suggestions.empty();
        }

        CommandContext<SharedSuggestionProvider> ctx = (CommandContext<SharedSuggestionProvider>) context;
        U versionedCtx = ContextTransformer.createCorrectCtx(ctx, null,
            customArgumentType.getCommandContextClass(), builder.getRemaining());
        CompletableFuture<CustomSuggestions> customSuggestionsCompletableFuture = customArgumentType.listSuggestions(
            versionedCtx);

        CustomSuggestions suggestions;
        try {
          suggestions = customSuggestionsCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
          logger.warn(I18n.translate("brigadier.exceptions.commands.suggestionCannotComplete"), e);
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
          StringReaderWrapper readerWrapper = new StringReaderWrapper(reader.getString(),
              reader.getCursor());
          S parse = customArgumentType.parse(readerWrapper);
          reader.setCursor(readerWrapper.getCursor());
          return parse;
        } catch (SyntaxException | CommandException e) {
          throw new SimpleCommandExceptionType(
              new LiteralMessage(e.getMessage())).createWithContext(reader);
        }
      }
    };
  }

}
