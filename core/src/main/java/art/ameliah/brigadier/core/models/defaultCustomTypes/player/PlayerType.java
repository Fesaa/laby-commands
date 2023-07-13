package art.ameliah.brigadier.core.models.defaultCustomTypes.player;

import art.ameliah.brigadier.core.models.custumTypes.CustomType;
import net.labymod.api.client.entity.player.Player;

public class PlayerType extends CustomType<Player> {

  public PlayerType(Player value) {
    super(value);
  }

  public static Class<Player> getClassType() {
    return Player.class;
  }
}
