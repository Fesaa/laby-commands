package art.ameliah.brigadier.core.commands.customTypes;

import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.CommandSource;

public class MyCustomCommandContext extends CommandContext {

  public MyCustomCommandContext(CommandSource source) {
    super(source);
  }

  public void myCustomMethod() {
    this.displayClientMessage("myCustomMethod called from MyCustomCommandContext!");
  }
}
