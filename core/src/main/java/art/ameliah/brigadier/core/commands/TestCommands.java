package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.annotations.AutoComplete;
import art.ameliah.brigadier.core.models.annotations.Bounded;
import art.ameliah.brigadier.core.models.annotations.Command;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.annotations.Greedy;
import art.ameliah.brigadier.core.models.annotations.Optional;
import java.util.List;

public class TestCommands {

  @Command
  public boolean foo(CommandContext ctx) {
    ctx.displayClientMessage("Executed foo");
    return true;
  }

  @Command(parent = "foo")
  public boolean bar(CommandContext ctx) {
    ctx.displayClientMessage("Executed bar");
    return true;
  }

  @Command
  @AutoComplete(method = "autocompleteName", parameterName = "arg2")
  @AutoComplete(method = "autocompleteOther", parameterName = "arg3")
  public boolean numbers(CommandContext ctx, @Bounded(max_int = 10) Integer i, String name,
      String other, Boolean b, @Optional @Greedy String theRest) {
    ctx.displayClientMessage(
        "Number: " + i + " Name: " + name + " Other: " + other + " Bool: " + b + " Rest?: "
            + theRest);
    return true;
  }

  public String[] autocompleteName(CommandContext ctx) {
    return new String[]{"Fesa", "Amelia"};
  }

  public String[] autocompleteOther(CommandContext ctx) {
    List<String> others = List.of("Eva", "Cathie", "Hoshi", "Mivke", "Casper");
    return others.stream().filter(el -> el.startsWith(ctx.currentArgInput()))
        .toArray(String[]::new);
  }

  @Command
  public boolean optionals(CommandContext ctx, String name, String other, @Optional String third) {
    ctx.displayClientMessage(name + " " + other + (third == null ? "" : " " + third));
    return true;
  }

}
