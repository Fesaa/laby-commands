package art.ameliah.laby.addons.library.commands.core.commands.customTypes;

import art.ameliah.laby.addons.library.commands.core.models.types.CustomType;

public class Fraction extends CustomType<Float> {

  public Fraction(Float value) {
    super(value);
  }

  public static Class<Float> getClassType() {
    return Float.class;
  }

}
