package art.ameliah.brigadier.core;

import art.ameliah.brigadier.core.events.tick;
import art.ameliah.brigadier.core.generated.DefaultReferenceStorage;
import art.ameliah.brigadier.core.linkers.BrigadierLinker;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;


@AddonMain
public class Brigadier extends LabyAddon<BrigadierConfig> {

  @Override
  protected void enable() {
    this.registerSettingCategory();

    DefaultReferenceStorage referenceStorage = this.referenceStorageAccessor();

    BrigadierLinker link = referenceStorage.getBrigadierLinker();
    if (link != null) {
      this.registerListener(new tick(link));
    }

    this.logger().info("Enabled the Addon");
  }

  @Override
  protected Class<BrigadierConfig> configurationClass() {
    return BrigadierConfig.class;
  }
}
