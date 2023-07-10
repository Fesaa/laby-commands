package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.AutoComplete;
import art.ameliah.brigadier.core.models.Bounded;
import art.ameliah.brigadier.core.models.Command;
import art.ameliah.brigadier.core.models.CommandGroup;
import art.ameliah.brigadier.core.models.Greedy;
import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;

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
  public boolean numbers(@Bounded(max = 10) Integer i, String name, String lovers, Boolean b) {
    Laby.labyAPI().minecraft().chatExecutor().displayClientMessage("You submitted number " + i + " with name " + name + " with lovers " + lovers + " and bool " + b);
    return true;
  }

  public String[] autocompleteLovers(String lovers) {
    System.out.println("AUTO COMPLETE IS BEING CALLED");
    return new String[]{"aaa", "bbb"};
  }

}
