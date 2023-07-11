package art.ameliah.brigadier.core;

import art.ameliah.brigadier.core.commands.ColourCommands;
import art.ameliah.brigadier.core.commands.ComplicatedBranching;
import art.ameliah.brigadier.core.commands.CustomCommandContextCommands;
import art.ameliah.brigadier.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.brigadier.core.commands.ServerSpecificCommands;
import art.ameliah.brigadier.core.commands.TestCommands;
import art.ameliah.brigadier.core.commands.customTypes.Fraction;
import art.ameliah.brigadier.core.commands.customTypes.FractionArgument;
import art.ameliah.brigadier.core.generated.DefaultReferenceStorage;
import art.ameliah.brigadier.core.service.CommandService;
import art.ameliah.brigadier.core.service.DefaultCommandService;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;


@AddonMain
public class Brigadier extends LabyAddon<BrigadierConfig> {

  private static Brigadier instance;
  private CommandService<MyCustomCommandContext> commandService;

  public Brigadier() {
    instance = this;
  }

  public static Brigadier get() {
    return instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    DefaultReferenceStorage storage = this.referenceStorageAccessor();
    commandService = storage.getCommandService();
    if (commandService == null) {
      commandService = new DefaultCommandService();
    }

    commandService.registerCustomArgumentType(Fraction.class, new FractionArgument());

    commandService.registerCommand(new TestCommands());
    commandService.registerCommand(new ColourCommands());
    commandService.registerCommand(new ComplicatedBranching());
    commandService.registerCommand(new ServerSpecificCommands("play.cubecraft.net"));
    commandService.registerCommand(new CustomCommandContextCommands());

    this.logger().info("Enabled the Addon");
  }

  public CommandService<MyCustomCommandContext> getCommandService() {
    return commandService;
  }

  @Override
  protected Class<BrigadierConfig> configurationClass() {
    return BrigadierConfig.class;
  }
}
