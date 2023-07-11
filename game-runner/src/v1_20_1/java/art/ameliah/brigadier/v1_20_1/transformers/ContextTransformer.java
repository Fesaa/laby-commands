package art.ameliah.brigadier.v1_20_1.transformers;

import art.ameliah.brigadier.core.models.CommandSource;
import art.ameliah.brigadier.v1_20_1.VersionedCommandSource;
import com.mojang.brigadier.context.CommandContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;

public class ContextTransformer {

  public static <S extends art.ameliah.brigadier.core.models.CommandContext> @Nullable S createCorrectCtx(
      CommandContext<SharedSuggestionProvider> ctx, Parameter parameter, Class<S> commandClass) {
    VersionedCommandSource versionedCtxSource = new VersionedCommandSource(ctx, parameter);
    Constructor<?> ctxConstructor;
    try {
      ctxConstructor = commandClass.getConstructor(CommandSource.class);
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
