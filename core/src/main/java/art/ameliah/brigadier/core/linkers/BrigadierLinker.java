package art.ameliah.brigadier.core.linkers;

import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.Nullable;

@Nullable
@Referenceable
public abstract class BrigadierLinker {

  protected boolean registered = false;

  public abstract void register();

  public boolean isRegistered() {
    return registered;
  }
}
