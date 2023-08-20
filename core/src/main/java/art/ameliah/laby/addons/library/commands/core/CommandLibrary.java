package art.ameliah.laby.addons.library.commands.core;

import art.ameliah.laby.addons.library.commands.core.commands.ColourCommands;
import art.ameliah.laby.addons.library.commands.core.commands.ComplicatedBranching;
import art.ameliah.laby.addons.library.commands.core.commands.CustomCommandContextCommands;
import art.ameliah.laby.addons.library.commands.core.commands.DefaultCustomTypes;
import art.ameliah.laby.addons.library.commands.core.commands.ServerSpecificCommands;
import art.ameliah.laby.addons.library.commands.core.commands.TestCommands;
import art.ameliah.laby.addons.library.commands.core.commands.customTypes.Fraction;
import art.ameliah.laby.addons.library.commands.core.commands.customTypes.FractionArgument;
import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.generated.DefaultReferenceStorage;
import art.ameliah.laby.addons.library.commands.core.service.CommandService;
import art.ameliah.laby.addons.library.commands.core.service.DefaultCommandService;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import javax.inject.Inject;


@AddonMain
public class CommandLibrary extends LabyAddon<LibraryConfig> {

  private static CommandLibrary instance;

  @Inject
  public CommandLibrary() {
    instance = this;
  }

  public static CommandLibrary get() {
    return instance;
  }

  private CommandService<MyCustomCommandContext> commandService;

  @Override
  protected void enable() {
    this.registerSettingCategory();
    DefaultReferenceStorage storage = this.referenceStorageAccessor();
    commandService = storage.getCommandService();
    if (commandService == null) {
      commandService = new DefaultCommandService<>();
    }
    commandService.registerCustomArgumentType(Fraction.class, new FractionArgument());

    commandService.registerCommand(new TestCommands());
    commandService.registerCommand(new ColourCommands());
    commandService.registerCommand(new ComplicatedBranching());
    commandService.registerCommand(new ServerSpecificCommands("play.cubecraft.net"));
    commandService.registerCommand(new CustomCommandContextCommands());
    commandService.registerCommand(new DefaultCustomTypes());

    this.logger().info("Enabled the Addon");
  }

  public CommandService<MyCustomCommandContext> getCommandService() {
    return commandService;
  }

  @Override
  protected Class<LibraryConfig> configurationClass() {
    return LibraryConfig.class;
  }
}
