package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.annotations.Check;
import art.ameliah.brigadier.core.models.annotations.Command;
import art.ameliah.brigadier.core.models.annotations.Greedy;
import art.ameliah.brigadier.core.models.annotations.NoCallback;
import art.ameliah.brigadier.core.utils.CyclicCollection;
import art.ameliah.brigadier.core.utils.Utils;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;

public class ColourCommands extends CommandClass {

  private final CyclicCollection<String> rainbowColour =
      CyclicCollection.of("§4", "§c", "§6", "§E", "§2", "§a", "§b", "§3", "§1", "§9", "§d", "§5");

  private final CyclicCollection<String> transColour = CyclicCollection.of("§b", "§d", "§f", "§d",
      "§b");

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
    ctx.displayClientMessage(Utils.mixer(text, this.rainbowColour));
    return true;
  }

  @Command(parent = "colour")
  @Check(method = "highEnough", errorMethod = "notHighEnough")
  public boolean trans(CommandContext ctx, @Greedy String text) {
    ctx.displayClientMessage(Utils.mixer(text, this.transColour));
    return true;
  }

  public boolean highEnough(CommandContext ctx) {
    return ctx.getPosition().getY() > 100;
  }

  public Component notHighEnough() {
    return Component.text("You have to be at least 100 blocks high!", NamedTextColor.BLUE);
  }

  @Override
  public Component noPermissionComponent() {
    return Component.text("You do not have the required permissions to use this command.",
        NamedTextColor.RED);
  }
}
