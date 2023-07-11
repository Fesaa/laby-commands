package art.ameliah.brigadier.core.commands.customTypes;

import art.ameliah.brigadier.core.models.custumTypes.CustomType;
import org.jetbrains.annotations.Nullable;

public class Fraction implements CustomType<Float> {

  private final Float value;

  public Fraction() {
    this.value = null;
  }

  public Fraction(Float value) {
    this.value = value;
  }

  public static Class<Float> getClassType() {
    return Float.class;
  }

  @Override
  @Nullable
  public Float get() {
    return value;
  }

}
