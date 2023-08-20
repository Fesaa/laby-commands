package art.ameliah.laby.addons.library.commands.core.models.types.defaults.NetworkPlayer;

import art.ameliah.laby.addons.library.commands.core.models.types.CustomType;
import net.labymod.api.client.network.NetworkPlayerInfo;

public class NetworkPlayerType extends CustomType<NetworkPlayerInfo> {

  public NetworkPlayerType(NetworkPlayerInfo value) {
    super(value);
  }

  public static Class<NetworkPlayerInfo> getClassType() {
    return NetworkPlayerInfo.class;
  }

}
