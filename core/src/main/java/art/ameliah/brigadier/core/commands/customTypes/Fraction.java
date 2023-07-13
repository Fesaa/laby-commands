package art.ameliah.brigadier.core.commands.customTypes;

import art.ameliah.brigadier.core.models.custumTypes.CustomType;

public class Fraction extends CustomType<Float> {

  public Fraction(Float value) {
    super(value);
  }

  public static Class<Float> getClassType() {
    return Float.class;
  }

}
