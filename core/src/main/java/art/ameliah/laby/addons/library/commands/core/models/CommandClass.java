package art.ameliah.laby.addons.library.commands.core.models;

import art.ameliah.laby.addons.library.commands.core.models.annotations.Check;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Classes containing commands should extend this class.
 */
public abstract class CommandClass<T extends CommandContext> {

  /**
   * @return Component displayed when any check fails
   */
  public abstract Component noPermissionComponent();

  public abstract @NotNull Class<T> getCommandContextClass();

  /**
   * @return Component displayed when an error occurs during command execution
   */
  public Component errorComponent() {
    return Component.translatable("brigadier.exceptions.command.defaultError", NamedTextColor.RED);
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
