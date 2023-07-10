package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.annotations.Command;
import art.ameliah.brigadier.core.models.annotations.Greedy;
import art.ameliah.brigadier.core.models.annotations.NoCallback;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;

public class ColourCommands extends CommandClass {

  public ColourCommands() {
  }

  @Command
  @NoCallback
  public boolean colour(CommandContext ctx) {
    return true;
  }

  @Command(parent = "colour")
  public boolean code(CommandContext ctx, @Greedy String text) {
    ctx.displayClientMessage(text.replace("&", "§"));
    return true;
  }

  @Command(parent = "colour")
  public boolean rainbow(CommandContext ctx, @Greedy String text) {
    ctx.displayClientMessage("§4 TEST");
    return true;
  }

  @Override
  public Component noPermissionComponent() {
    return Component.text("You do not have the required permissions to use this command.",
        NamedTextColor.RED);
  }
}
