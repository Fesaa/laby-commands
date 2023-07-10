package art.ameliah.brigadier.core;

import art.ameliah.brigadier.core.commands.TestCommands;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;


@AddonMain
public class Brigadier extends LabyAddon<BrigadierConfig> {

  @Override
  protected void enable() {
    this.registerSettingCategory();

    CommandService.registerCommand(new TestCommands());

    this.logger().info("Enabled the Addon");
  }

  @Override
  protected Class<BrigadierConfig> configurationClass() {
    return BrigadierConfig.class;
  }
}
