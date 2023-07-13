package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.annotations.Check;
import art.ameliah.brigadier.core.models.annotations.Command;
import art.ameliah.brigadier.core.models.annotations.Greedy;
import art.ameliah.brigadier.core.models.annotations.NoCallback;
import art.ameliah.brigadier.core.utils.CyclicCollection;
import art.ameliah.brigadier.core.utils.Utils;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class ColourCommands extends CommandClass<MyCustomCommandContext> {

  private final CyclicCollection<String> rainbowColour =
      CyclicCollection.of("§4", "§c", "§6", "§E", "§2", "§a", "§b", "§3", "§1", "§9", "§d", "§5");

  private final CyclicCollection<String> transColour = CyclicCollection.of("§b", "§d", "§f", "§d",
      "§b");

  public ColourCommands() {
  }

  @Command
  @NoCallback
  public boolean colour(MyCustomCommandContext ctx) {
    return true;
  }

  @Command(parent = "colour")
  public boolean code(MyCustomCommandContext ctx, @Greedy String text) {
    ctx.displayClientMessage(text.replace("&", "§"));
    return true;
  }

  @Command(parent = "colour")
  public boolean rainbow(MyCustomCommandContext ctx, @Greedy String text) {
    ctx.displayClientMessage(Utils.mixer(text, this.rainbowColour));
    return true;
  }

  @Command(parent = "colour")
  @Check(method = "highEnough", failedMethod = "notHighEnough")
  public boolean trans(MyCustomCommandContext ctx, @Greedy String text) {
    ctx.displayClientMessage(Utils.mixer(text, this.transColour));
    return true;
  }

  public boolean highEnough(MyCustomCommandContext ctx) {
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

  @Override
  public @NotNull Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }
}
