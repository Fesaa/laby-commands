package art.ameliah.brigadier.core.models;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.entity.player.GameMode;

/**
 * Classes containing commands should extend this class.
 */
public abstract class CommandClass {

  public abstract Component noPermissionComponent();

  public Component errorComponent() {
    return Component.text(
        "An error has occured during the executing of the command. Check your game logs for more information.",
        NamedTextColor.RED);
  }

  public boolean classCheck(CommandContext ctx) {
    return true;
  }

  public boolean isCreative(CommandContext ctx) {
    ClientPlayer player = ctx.getClientPlayer();
    return player != null && player.gameMode().equals(GameMode.CREATIVE);
  }

}
