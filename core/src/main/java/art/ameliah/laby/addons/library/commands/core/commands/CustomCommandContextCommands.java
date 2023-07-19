package art.ameliah.laby.addons.library.commands.core.commands;

import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Command;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class CustomCommandContextCommands extends CommandClass<MyCustomCommandContext> {

  @Override
  public Component noPermissionComponent() {
    return Component.text("NO!", NamedTextColor.RED);
  }

  @Override
  public @NotNull Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }

  @Command
  public boolean myCommand(MyCustomCommandContext ctx) {
    ctx.myCustomMethod();
    return true;
  }
}
