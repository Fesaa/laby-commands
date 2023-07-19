package art.ameliah.laby.addons.library.commands.core.models;

import java.util.Collection;
import java.util.stream.Stream;
import net.labymod.api.client.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper around the Mojang provided source
 */
public abstract class CommandSource {

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

  @NotNull
  public abstract Stream<ResourceLocation> getAvailableSounds();

  @NotNull
  public abstract Collection<String> getAllTeams();

  @NotNull
  public abstract Collection<String> getOnlinePlayerNames();


}