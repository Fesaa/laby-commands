package art.ameliah.brigadier.core.service;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.custumTypes.CustomArgumentType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service used to register your {@link CommandClass}'s. Obtain with
 * {@link LabyAddon#referenceStorageAccessor()}
 *
 * @param <T> Your used {@link CommandContext}
 */
@Nullable
@Referenceable
public abstract class CommandService<T extends CommandContext> {

  protected final List<CommandClass<T>> commandClasses = new ArrayList<>();
  protected final HashMap<Class<?>, CustomArgumentType<?, ?>> customArguments = new HashMap<>();

  public CommandService() {
  }

  public CustomArgumentType<?, ?> getCustomArgument(Class<?> clazz) {
    return this.customArguments.get(clazz);
  }

  public boolean isCustomArgument(Class<?> clazz) {
    return this.customArguments.containsKey(clazz);
  }

  /**
   * <b>Must</b> be registered before {@link CommandClass}'s are
   *
   * @param clazz              Class of the argument to convert for
   * @param customArgumentType Converter for the argument
   */
  public void registerCustomArgumentType(Class<?> clazz,
      CustomArgumentType<?, ?> customArgumentType) {
    this.customArguments.put(clazz, customArgumentType);
  }

  /**
   * @return Whether the registration was successful
   */
  public abstract boolean registerCommand(@NotNull CommandClass<T> commandClass);

  public abstract boolean isCustomCommand(String root);

  /**
   * Will only go in effect on the next server join.
   */
  public abstract boolean removeCommand(@NotNull CommandClass<T> commandClass);

  public List<CommandClass<T>> getCommandClasses() {
    return commandClasses;
  }
}
