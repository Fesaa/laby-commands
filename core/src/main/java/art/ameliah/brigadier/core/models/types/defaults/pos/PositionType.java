package art.ameliah.brigadier.core.models.types.defaults.pos;

import art.ameliah.brigadier.core.models.types.CustomType;
import net.labymod.api.util.math.vector.FloatVector3;

public class PositionType extends CustomType<FloatVector3> {

  public PositionType(FloatVector3 value) {
    super(value);
  }

  public static Class<FloatVector3> getClassType() {
    return FloatVector3.class;
  }

}
