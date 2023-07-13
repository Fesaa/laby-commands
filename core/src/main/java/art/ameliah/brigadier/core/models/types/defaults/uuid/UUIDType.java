package art.ameliah.brigadier.core.models.types.defaults.uuid;

import art.ameliah.brigadier.core.models.types.CustomType;
import java.util.UUID;

public class UUIDType extends CustomType<UUID> {

  public UUIDType(UUID value) {
    super(value);
  }

  public static Class<UUID> getClassType() {
    return UUID.class;
  }

}
