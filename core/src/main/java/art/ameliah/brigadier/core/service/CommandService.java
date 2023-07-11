package art.ameliah.brigadier.core.service;

import art.ameliah.brigadier.core.models.CommandClass;
import java.util.ArrayList;
import java.util.List;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Nullable
@Referenceable
public abstract class CommandService {

  protected final List<CommandClass> commandClasses = new ArrayList<>();

  public CommandService() {
  }

  public abstract boolean registerCommand(@NotNull CommandClass commandClass);

  /**
   * Will only go in effect on the next world join.
   */
  public abstract boolean removeCommand(@NotNull CommandClass commandClass);

  public List<CommandClass> getCommandClasses() {
    return commandClasses;
  }
}
