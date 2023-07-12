package art.ameliah.brigadier.core.models;

import art.ameliah.brigadier.core.models.annotations.Check;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;

/**
 * Classes containing commands should extend this class.
 */
public abstract class CommandClass<T extends CommandContext> {

  /**
   * @return Component displayed when any check fails
   */
  public abstract Component noPermissionComponent();

  public abstract Class<T> getCommandContextClass();

  /**
   * @return Component displayed when an error occurs during command execution
   */
  public Component errorComponent() {
    return Component.text(
        "An error has occurred during the executing of the command. Check your game logs for more information.",
        NamedTextColor.RED);
  }

  /**
   * A {@link Check} applied to all commands in the class
   */
  public boolean classCheck(T ctx) {
    return true;
  }

  /**
   * Whether a command should be registered upon joining a server (or single-player)
   *
   * @param ctx {@link CommandContext#getSource()} will return {@code null}
   */
  public boolean shouldRegister(T ctx) {
    return true;
  }
}
