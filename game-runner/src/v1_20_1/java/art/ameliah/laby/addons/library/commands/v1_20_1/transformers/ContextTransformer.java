package art.ameliah.laby.addons.library.commands.v1_20_1.transformers;

import art.ameliah.laby.addons.library.commands.core.models.CommandSource;
import art.ameliah.laby.addons.library.commands.v1_20_1.VersionedCommandSource;
import com.mojang.brigadier.context.CommandContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;

public class ContextTransformer {

  public static <S extends art.ameliah.laby.addons.library.commands.core.models.CommandContext> @Nullable S createCorrectCtx(
      CommandContext<SharedSuggestionProvider> ctx, Parameter parameter, Class<S> commandContext) {
    VersionedCommandSource versionedCtxSource = new VersionedCommandSource(ctx, parameter);
    return create(commandContext, versionedCtxSource);
  }

  public static <S extends art.ameliah.laby.addons.library.commands.core.models.CommandContext> @Nullable S createCorrectCtx(
      CommandContext<SharedSuggestionProvider> ctx, Parameter parameter, Class<S> commandContext, String remaining) {
    VersionedCommandSource versionedCtxSource = new VersionedCommandSource(ctx, parameter, remaining);
    return create(commandContext, versionedCtxSource);
  }

  private static <S extends art.ameliah.laby.addons.library.commands.core.models.CommandContext> @Nullable S create(
      Class<S> commandContext, VersionedCommandSource versionedCtxSource
  ) {
    Constructor<?> ctxConstructor;
    try {
      ctxConstructor = commandContext.getConstructor(CommandSource.class);
    } catch (NoSuchMethodException ignored) {
      return null;
    }
    Object newCtx;
    try {
      newCtx = ctxConstructor.newInstance(versionedCtxSource);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
      return null;
    }
    return (S) newCtx;
  }

}
