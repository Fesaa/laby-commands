package art.ameliah.brigadier.core.commands;

import art.ameliah.brigadier.core.Brigadier;
import art.ameliah.brigadier.core.models.Command;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.CommandGroup;
import art.ameliah.brigadier.core.models.Greedy;
import art.ameliah.brigadier.core.models.NoCallback;

public class ColourCommands {

  private final Brigadier addon;

  public ColourCommands(Brigadier addon) {
    this.addon = addon;
  }

  @CommandGroup
  @NoCallback
  public boolean colour() {
    return true;
  }

  @Command(parent = "colour")
  public boolean code(@Greedy String text) {
    this.addon.displayMessage(text.replace("&", "§"));
    return true;
  }

  @Command(parent = "code")
  public boolean rainbow(@Greedy String text) {
    this.addon.displayMessage("§4 TEST");
    return true;
  }

}
