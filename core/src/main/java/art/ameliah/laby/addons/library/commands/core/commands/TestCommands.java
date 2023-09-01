package art.ameliah.laby.addons.library.commands.core.commands;

import art.ameliah.laby.addons.library.commands.core.commands.customTypes.Fraction;
import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.CommandSource;
import art.ameliah.laby.addons.library.commands.core.models.annotations.AutoComplete;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Bounded;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Check;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Command;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Greedy;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import art.ameliah.laby.addons.library.commands.core.models.types.defaults.NetworkPlayer.NetworkPlayerType;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.entity.player.GameMode;
import net.labymod.api.client.network.NetworkPlayerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestCommands extends CommandClass<MyCustomCommandContext> {

  @Override
  public Component noPermissionComponent() {
    return Component.text("You do not have the required permissions to use this command.",
        NamedTextColor.RED);
  }

  @Override
  public @NotNull Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }

  @Command(injectCommand = true)
  @AutoComplete(method = "autocompleteTimes", parameterName = "arg1")
  public boolean time(MyCustomCommandContext ctx, String time) {
    ctx.displayClientMessage("LOL: " + time);
    return true;
  }

  public String[] autocompleteTimes(MyCustomCommandContext ctx) {
    return new String[]{"what"};
  }



  @Command
  public boolean playerList(MyCustomCommandContext ctx, NetworkPlayerType[] players) {
    ctx.displayClientMessage("Players: " + Arrays.stream(players)
        .map(NetworkPlayerType::get)
        .filter(Objects::nonNull)
        .map(el -> String.format("[Name: %s, Health: %s]", el.profile().getUsername(), el.getHealth()))
        .collect(Collectors.joining(",\n")));
    return true;
  }

  @Command
  public boolean test(MyCustomCommandContext ctx, Fraction[] numbers, String s) {
    ctx.displayClientMessage("Executed test with: " + Arrays.stream(numbers)
        .map(Fraction::get)
        .map(el -> " " + el)
        .collect(Collectors.joining())
        + " and " + s);
    return true;
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
  @Named(argument = "arg1", name = "age")
  @Named(argument = "arg2", name = "name")
  @AutoComplete(method = "autocompleteName", parameterName = "name")
  @AutoComplete(method = "autocompleteOther", parameterName = "arg3")
  public boolean numbers(MyCustomCommandContext ctx, @Bounded(max_int = 10) Integer i, String name,
      String other, Boolean b, @Nullable @Greedy String theRest) {
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
    CommandSource source = ctx.getSource();
    if (source == null) {
      return others.toArray(String[]::new);
    }
    String cur = source.currentArgInput();
    return others
        .stream()
        .filter(el -> el.startsWith(cur))
        .toArray(String[]::new);
  }

  @Command
  public boolean optionals(MyCustomCommandContext ctx, String name, String other,
      @Nullable String third) {
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
