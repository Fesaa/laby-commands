package art.ameliah.brigadier.core.service;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.custumTypes.CustomArgumentType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Nullable
@Referenceable
public abstract class CommandService {

  protected final List<CommandClass> commandClasses = new ArrayList<>();
  protected final HashMap<Class<?>, CustomArgumentType<?>> customArguments = new HashMap<>();

  public CommandService() {
  }

  public CustomArgumentType<?> getCustomArgument(Class<?> clazz) {
    return this.customArguments.get(clazz);
  }

  public boolean isCustomArgument(Class<?> clazz) {
    return this.customArguments.containsKey(clazz);
  }

  /**
   * **Must** be registered before commands are
   * @param clazz Class of the argument to convert for
   * @param customArgumentType Converter for the argument
   */
  public void registerCustomArgumentType(Class<?> clazz, CustomArgumentType<?> customArgumentType) {
    this.customArguments.put(clazz, customArgumentType);
  }

  public abstract boolean registerCommand(@NotNull CommandClass commandClass);

  public abstract boolean isCustomCommand(String root);

  /**
   * Will only go in effect on the next world join.
   */
  public abstract boolean removeCommand(@NotNull CommandClass commandClass);

  public List<CommandClass> getCommandClasses() {
    return commandClasses;
  }
}
