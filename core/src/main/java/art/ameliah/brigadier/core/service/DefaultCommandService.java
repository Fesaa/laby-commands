package art.ameliah.brigadier.core.service;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.CommandContext;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandService<T extends CommandContext> extends CommandService<T> {

  @Override
  public boolean registerCommand(@NotNull CommandClass<T> commandClass) {
    return false;
  }

  @Override
  public boolean isCustomCommand(String root) {
    return false;
  }

  @Override
  public boolean removeCommand(@NotNull CommandClass<T> commandClass) {
    return false;
  }
}
