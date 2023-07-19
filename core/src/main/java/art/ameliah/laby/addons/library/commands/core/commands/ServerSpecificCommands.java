package art.ameliah.laby.addons.library.commands.core.commands;

import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Command;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class ServerSpecificCommands extends CommandClass<MyCustomCommandContext> {

  private final String ip;

  public ServerSpecificCommands(String ip) {
    this.ip = ip;
  }

  @Override
  public boolean shouldRegister(MyCustomCommandContext ctx) {
    return ctx.getServerData() != null && ctx.getServerData().address().getHost().equals(this.ip);
  }

  @Command
  public boolean hidden(MyCustomCommandContext ctx) {
    ctx.displayClientMessage("Wow! This only works on: " + this.ip);
    return true;
  }

  @Override
  public Component noPermissionComponent() {
    return Component.text("You do not have the required permissions to use this command.",
        NamedTextColor.RED);
  }

  @Override
  public @NotNull Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }
}
