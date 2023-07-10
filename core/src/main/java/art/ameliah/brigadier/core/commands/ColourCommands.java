package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.annotations.Command;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.annotations.Greedy;
import art.ameliah.brigadier.core.models.annotations.NoCallback;

public class ColourCommands {

  public ColourCommands() {
  }

  @Command
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
