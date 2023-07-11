package art.ameliah.brigadier.core.models;

import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import org.jetbrains.annotations.NotNull;

/**
 * First argument in Command callbacks and AutoComplete callbacks
 */
public abstract class CommandSource {

  protected final LabyAPI labyAPI = Laby.labyAPI();

  /**
   * @param name  Name of the argument
   * @param clazz Class of the argument
   * @return Returns the value of the argument if present
   * @throws IllegalArgumentException If the argument isn't present an exception is thrown
   */
  public abstract <V> V getArgument(String name, Class<V> clazz) throws IllegalArgumentException;

  /**
   * Returns an empty string if input isn't found. Only present in AutoComplete callbacks
   *
   * @return The input for the current argument
   */
  @NotNull
  public abstract String currentArgInput();

  /**
   * The command as typed by the user in the chat box
   *
   * @return The full command string
   */
  public abstract String getInput();

}
