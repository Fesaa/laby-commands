package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.Command;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.CommandGroup;
import art.ameliah.brigadier.core.models.Greedy;
import art.ameliah.brigadier.core.models.NoCallback;

public class ColourCommands {

  public ColourCommands() {
  }

  @CommandGroup
  @NoCallback
  public boolean colour(CommandContext ctx) {
    return true;
  }

  @Command(parent = "colour")
  public boolean code(CommandContext ctx, @Greedy String text) {
    ctx.displayClientMessage(text.replace("&", "§"));
    return true;
  }

  @Command(parent = "colour")
  public boolean rainbow(CommandContext ctx, @Greedy String text) {
    ctx.displayClientMessage("§4 TEST");
    return true;
  }

}
