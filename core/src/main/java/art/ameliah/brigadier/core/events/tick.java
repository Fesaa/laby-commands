package art.ameliah.brigadier.core.events;

import art.ameliah.brigadier.core.linkers.BrigadierLinker;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

public class tick {

  private final BrigadierLinker link;


  public tick(BrigadierLinker linker) {
    link = linker;
  }

  @Subscribe
  public void onTick(GameTickEvent e) {
    if (!link.isRegistered()) {
      link.register();
    }
  }

}
