package art.ameliah.brigadier.core;

import art.ameliah.brigadier.core.commands.ColourCommands;
import art.ameliah.brigadier.core.commands.ComplicatedBranching;
import art.ameliah.brigadier.core.commands.ServerSpecificCommands;
import art.ameliah.brigadier.core.commands.TestCommands;
import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.service.CommandService;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;


@AddonMain
public class Brigadier extends LabyAddon<BrigadierConfig> {

  private static Brigadier instance;
  private final CommandService<CommandClass> commandService = CommandService.defaultCommandService();

  public Brigadier() {
    instance = this;
  }

  public static Brigadier get() {
    return instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();
    commandService.registerCommand(new TestCommands());
    commandService.registerCommand(new ColourCommands());
    commandService.registerCommand(new ComplicatedBranching());
    commandService.registerCommand(new ServerSpecificCommands("play.cubecraft.net"));

    this.logger().info("Enabled the Addon");
  }

  public CommandService<CommandClass> getCommandService() {
    return commandService;
  }

  @Override
  protected Class<BrigadierConfig> configurationClass() {
    return BrigadierConfig.class;
  }
}
