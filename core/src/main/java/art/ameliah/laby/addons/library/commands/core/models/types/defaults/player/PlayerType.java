package art.ameliah.laby.addons.library.commands.core.models.types.defaults.player;

import art.ameliah.laby.addons.library.commands.core.models.types.CustomType;
import net.labymod.api.client.entity.player.Player;

public class PlayerType extends CustomType<Player> {

  public PlayerType(Player value) {
    super(value);
  }

  public static Class<Player> getClassType() {
    return Player.class;
  }
}
