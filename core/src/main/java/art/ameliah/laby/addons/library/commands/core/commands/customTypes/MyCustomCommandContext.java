package art.ameliah.laby.addons.library.commands.core.commands.customTypes;

import art.ameliah.laby.addons.library.commands.core.models.CommandContext;
import art.ameliah.laby.addons.library.commands.core.models.CommandSource;

public class MyCustomCommandContext extends CommandContext {

  public MyCustomCommandContext(CommandSource source) {
    super(source);
  }

  public void myCustomMethod() {
    this.displayClientMessage("myCustomMethod called from MyCustomCommandContext!");
  }
}
