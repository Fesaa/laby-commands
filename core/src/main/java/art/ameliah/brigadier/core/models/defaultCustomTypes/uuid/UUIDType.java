package art.ameliah.brigadier.core.models.defaultCustomTypes.uuid;

import art.ameliah.brigadier.core.models.custumTypes.CustomType;
import java.util.UUID;

public class UUIDType extends CustomType<UUID> {

  public UUIDType(UUID value) {
    super(value);
  }

  public static Class<UUID> getClassType() {
    return UUID.class;
  }

}
