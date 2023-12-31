package art.ameliah.laby.addons.library.commands.core.commands;

import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Command;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Greedy;
import art.ameliah.laby.addons.library.commands.core.models.annotations.NoCallback;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComplicatedBranching extends CommandClass<MyCustomCommandContext> {

  @Command
  @NoCallback
  public boolean base(MyCustomCommandContext ctx) {
    return true;
  }

  @Command(parent = "base")
  public boolean firstBranchOne(MyCustomCommandContext ctx) {
    ctx.displayClientMessage("firstBranchOne");
    return true;
  }

  @Command(parent = "base")
  public boolean firstBranchTwo(MyCustomCommandContext ctx) {
    ctx.displayClientMessage("firstBranchTwo");
    return true;
  }

  @Command(parent = "firstBranchOne")
  public boolean baseTwo(MyCustomCommandContext ctx) {
    ctx.displayClientMessage("baseTwo");
    return true;
  }

  @Command(parent = "baseTwo")
  public boolean secondBranchOne(MyCustomCommandContext ctx, int i, @Greedy String text) {
    ctx.displayClientMessage("secondBranchOne " + i + " " + text);
    return true;
  }

  @Command(parent = "baseTwo")
  public boolean secondBranchTwo(MyCustomCommandContext ctx, @Nullable Float weight) {
    ctx.displayClientMessage("secondBranchTwo " + (weight == null ? "" : weight));
    return true;
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
