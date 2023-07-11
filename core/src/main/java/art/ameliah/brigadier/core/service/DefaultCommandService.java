package art.ameliah.brigadier.core.service;

import art.ameliah.brigadier.core.models.CommandClass;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandService extends CommandService {

  @Override
  public boolean registerCommand(@NotNull CommandClass commandClass) {
    return false;
  }

  @Override
  public boolean removeCommand(@NotNull CommandClass commandClass) {
    return false;
  }
}
