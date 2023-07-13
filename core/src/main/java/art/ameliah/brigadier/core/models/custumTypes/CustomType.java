package art.ameliah.brigadier.core.models.custumTypes;

import art.ameliah.brigadier.core.models.annotations.Command;
import org.jetbrains.annotations.Nullable;

/**
 * The typed used for your argument in a {@link Command}
 *
 * @param <T> The wrapped type
 */
public abstract class CustomType<T> {

  protected final T value;

  public CustomType() {
    this.value = null;
  }

  public CustomType(T value) {
    this.value = value;
  }

  public static Class<?> getClassType() {
    return null;
  }

  public @Nullable T get() {
    return value;
  }

  ;


}
