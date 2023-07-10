package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.AutoComplete;
import art.ameliah.brigadier.core.models.Bounded;
import art.ameliah.brigadier.core.models.Command;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.CommandGroup;
import art.ameliah.brigadier.core.models.Optional;
import net.labymod.api.Laby;

public class TestCommands {

  @CommandGroup
  public boolean foo() {
    Laby.labyAPI().minecraft().chatExecutor().displayClientMessage("Executed foo");
    return true;
  }

  @Command(parent = "foo")
  public boolean bar() {
    Laby.labyAPI().minecraft().chatExecutor().displayClientMessage("Executed bar");
    return true;
  }

  @Command
  @AutoComplete(method = "autocompleteLovers", parameterName = "arg2")
  public boolean numbers(@Bounded(max_int = 10) Integer i, String name, String lovers, Boolean b) {
    Laby.labyAPI().minecraft().chatExecutor().displayClientMessage("You submitted number " + i + " with name " + name + " with lovers " + lovers + " and bool " + b);
    return true;
  }

  public String[] autocompleteLovers(String lovers) {
    return new String[]{"aaa", "bbb"};
  }

  @Command
  public boolean optionals(String name, String other, @Optional String third) {
    Laby.labyAPI().minecraft().chatExecutor().displayClientMessage(name + " " + other + (third == null ? "" : " " + third));
    return true;
  }

}
