package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.commands.customTypes.Fraction;
import art.ameliah.brigadier.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.annotations.AutoComplete;
import art.ameliah.brigadier.core.models.annotations.Bounded;
import art.ameliah.brigadier.core.models.annotations.Check;
import art.ameliah.brigadier.core.models.annotations.Command;
import art.ameliah.brigadier.core.models.annotations.Greedy;
import art.ameliah.brigadier.core.models.annotations.Optional;
import java.util.List;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.entity.player.GameMode;

public class TestCommands extends CommandClass<MyCustomCommandContext> {

  @Override
  public Component noPermissionComponent() {
    return Component.text("You do not have the required permissions to use this command.",
        NamedTextColor.RED);
  }

  @Override
  public Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }

  @Command
  public boolean foo(MyCustomCommandContext ctx) {
    ctx.displayClientMessage("Executed foo");
    return true;
  }

  @Command(parent = "foo")
  @Check(method = "isCreative")
  public boolean bar(MyCustomCommandContext ctx) {
    ctx.displayClientMessage("Executed bar");
    return true;
  }

  public boolean isCreative(MyCustomCommandContext ctx) {
    ClientPlayer player = ctx.getClientPlayer();
    return player != null && player.gameMode().equals(GameMode.CREATIVE);
  }

  @Command
  @AutoComplete(method = "autocompleteName", parameterName = "arg2")
  @AutoComplete(method = "autocompleteOther", parameterName = "arg3")
  public boolean numbers(MyCustomCommandContext ctx, @Bounded(max_int = 10) Integer i, String name,
      String other, Boolean b, @Optional @Greedy String theRest) {
    ctx.displayClientMessage(
        "Number: " + i + " Name: " + name + " Other: " + other + " Bool: " + b + " Rest?: "
            + theRest);
    return true;
  }

  public String[] autocompleteName(MyCustomCommandContext ctx) {
    return new String[]{"Fesa", "Amelia"};
  }

  public String[] autocompleteOther(MyCustomCommandContext ctx) {
    List<String> others = List.of("Eva", "Cathie", "Hoshi", "Mivke", "Casper");
    return others.stream().filter(el -> el.startsWith(ctx.getSource().currentArgInput()))
        .toArray(String[]::new);
  }

  @Command
  public boolean optionals(MyCustomCommandContext ctx, String name, String other,
      @Optional String third) {
    ctx.displayClientMessage(name + " " + other + (third == null ? "" : " " + third));
    return true;
  }

  @Command
  public boolean fractionCommand(MyCustomCommandContext ctx, Fraction fractionArgument,
      Integer rest) {
    ctx.displayClientMessage(
        "Your fraction has a value of: " + fractionArgument.get() + " and rest: " + rest);
    return true;
  }
}
