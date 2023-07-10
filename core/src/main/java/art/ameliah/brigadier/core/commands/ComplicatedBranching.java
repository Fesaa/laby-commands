package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.models.Command;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.Greedy;
import art.ameliah.brigadier.core.models.NoCallback;
import art.ameliah.brigadier.core.models.Optional;

public class ComplicatedBranching {

  @Command
  @NoCallback
  public boolean base(CommandContext ctx) {
    return true;
  }

  @Command(parent = "base")
  public boolean firstBranchOne(CommandContext ctx) {
    ctx.displayClientMessage("firstBranchOne");
    return true;
  }

  @Command(parent = "base")
  public boolean firstBranchTwo(CommandContext ctx) {
    ctx.displayClientMessage("firstBranchTwo");
    return true;
  }

  @Command(parent = "firstBranchOne")
  public boolean baseTwo(CommandContext ctx) {
    ctx.displayClientMessage("baseTwo");
    return true;
  }

  @Command(parent = "baseTwo")
  public boolean secondBranchOne(CommandContext ctx, int i, @Greedy String text) {
    ctx.displayClientMessage("secondBranchOne " + i + " " + text);
    return true;
  }

  @Command(parent = "baseTwo")
  public boolean secondBranchTwo(CommandContext ctx, @Optional Float weight) {
    ctx.displayClientMessage("secondBranchTwo " + (weight == null ? "" : weight));
    return true;
  }

}
