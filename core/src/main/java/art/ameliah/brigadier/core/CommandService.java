package art.ameliah.brigadier.core;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandService {

  private static List<Object> commandClasses = new ArrayList<>();

  public static void registerCommand(@NotNull Object commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");
    commandClasses.add(commandClass);
  }

  public static List<Object> getCommandClasses() {
    return commandClasses;
  }
}
