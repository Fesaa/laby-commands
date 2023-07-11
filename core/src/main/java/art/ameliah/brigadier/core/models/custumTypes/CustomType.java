package art.ameliah.brigadier.core.models.custumTypes;

import org.jetbrains.annotations.Nullable;

public interface CustomType<T> {

  static Class<?> getClassType() {
    return null;
  }

  @Nullable T get();


}
