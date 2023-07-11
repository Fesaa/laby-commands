package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.annotations.Command;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;

public class ServerSpecificCommands extends CommandClass {

  private final String ip;

  public ServerSpecificCommands(String ip) {
    this.ip = ip;
  }

  @Override
  public boolean classCheck(CommandContext ctx) {
    return ctx.getServerData().address().getHost().equals(this.ip);
  }

  @Command
  public boolean hidden(CommandContext ctx) {
    ctx.displayClientMessage("Wow! This only works on: " + this.ip);
    return true;
  }

  @Override
  public Component noPermissionComponent() {
    return Component.text("You do not have the required permissions to use this command.",
        NamedTextColor.RED);
  }
}
