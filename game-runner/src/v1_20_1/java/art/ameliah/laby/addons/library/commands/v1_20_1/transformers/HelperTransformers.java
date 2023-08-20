package art.ameliah.laby.addons.library.commands.v1_20_1.transformers;

import art.ameliah.laby.addons.library.commands.v1_20_1.VersionedCommandService;
import com.mojang.brigadier.context.CommandContext;
import net.labymod.api.util.I18n;
import net.minecraft.commands.SharedSuggestionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class HelperTransformers {

  private static final Logger logger = LoggerFactory.getLogger(HelperTransformers.class);

  static <T> T getArgument(Class<T> clazz, CommandContext<SharedSuggestionProvider> ctx, String name) {
    try {
      return ctx.getArgument(name, clazz);
    } catch (IllegalArgumentException e) {
      logger.warn(I18n.translate("brigadier.exceptions.commands.cannotGetArgument", name, e.getClass()), e);
      return null;
    }
  }

  static Function<Object, Object> arrayMapper(Class<?> clazz) {
    return el -> {
      if (VersionedCommandService.get().isCustomArgument(clazz)) {
        return constructCustomArgument(clazz, el);
      } else {
        return clazz.cast(el);
      }
    };
  }

  static Object constructCustomArgumentFromContext(Class<?> clazz, CommandContext<SharedSuggestionProvider> ctx, String name) {
    try {
      return constructCustomArgument(clazz, ctx.getArgument(name, getReturnClass(clazz)));
    } catch (NoSuchMethodException | InvocationTargetException |
             IllegalAccessException invalidException) {
      logger.error(I18n.translate("brigadier.exceptions.commands.impossibleError", invalidException.getClass()));
      return null;
    }
  }

  static Object constructCustomArgument(Class<?> clazz, Object input) {
    try {
      Class<?> returnClass = getReturnClass(clazz);
      Constructor<?> constructor = clazz.getConstructor(returnClass);
      return constructor.newInstance(input);
    } catch (InstantiationException e) {
      logger.warn(I18n.translate("brigadier.exceptions.commands.cannotInitialiseCustomArgument", e.getClass()), e);
      return null;
    } catch (NoSuchMethodException | InvocationTargetException |
             IllegalAccessException invalidException) {
      logger.error(I18n.translate("brigadier.exceptions.commands.impossibleError", invalidException.getClass()));
      return null;
    }
  }

  static Class<?> getReturnClass(Class<?> clazz)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method classGetter = clazz.getMethod("getClassType");
    Object returnValue = classGetter.invoke(clazz);
    return (Class<?>) returnValue;
  }
}
