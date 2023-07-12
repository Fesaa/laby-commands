package art.ameliah.brigadier.core.models;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;

/**
 * Classes containing commands should extend this class.
 */
public abstract class CommandClass<T extends CommandContext> {

  public abstract Component noPermissionComponent();

  public abstract Class<T> getCommandContextClass();

  public Component errorComponent() {
    return Component.text(
        "An error has occured during the executing of the command. Check your game logs for more information.",
        NamedTextColor.RED);
  }

  public boolean classCheck(T ctx) {
    return true;
  }

  public boolean shouldRegister(T ctx) {
    return true;
  }
}
