package art.ameliah.brigadier.core.service;

import art.ameliah.brigadier.core.models.CommandClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class CommandService<T extends CommandClass> {

  private final List<T> commandClasses = new ArrayList<>();

  public CommandService() {
  }

  public static CommandService<CommandClass> defaultCommandService() {
    return new CommandService<>();
  }

  public void registerCommand(@NotNull T commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");
    this.commandClasses.add(commandClass);
  }

  /**
   * Will only go in effect on the next world join.
   */
  public boolean removeCommand(@NotNull T commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");

    return this.commandClasses.remove(commandClass);
  }

  public List<T> getCommandClasses() {
    return commandClasses;
  }
}
