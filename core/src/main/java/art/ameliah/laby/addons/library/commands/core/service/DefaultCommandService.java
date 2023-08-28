package art.ameliah.laby.addons.library.commands.core.service;

import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.CommandContext;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandService<T extends CommandContext> extends CommandService<T> {

  @Override
  public boolean registerCommand(@NotNull CommandClass<T> commandClass) {
    return false;
  }

  @Override
  public CommandType getCommandType(String root) {
    return CommandType.SERVER;
  }

  @Override
  public boolean removeCommand(@NotNull CommandClass<T> commandClass) {
    return false;
  }
}
