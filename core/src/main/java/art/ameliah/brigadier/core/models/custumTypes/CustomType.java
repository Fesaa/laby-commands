package art.ameliah.brigadier.core.models.custumTypes;

import art.ameliah.brigadier.core.models.annotations.Command;
import org.jetbrains.annotations.Nullable;

/**
 * The typed used for your argument in a {@link Command}
 *
 * @param <T> The wrapped type
 */
public interface CustomType<T> {

  static Class<?> getClassType() {
    return null;
  }

  @Nullable T get();


}
