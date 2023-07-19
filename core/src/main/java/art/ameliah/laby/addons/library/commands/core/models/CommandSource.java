package art.ameliah.laby.addons.library.commands.core.models;

import java.util.Collection;
import java.util.stream.Stream;
import net.labymod.api.client.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper around the Mojang provided source
 */
public interface CommandSource {

  /**
   * @param name  Name of the argument
   * @param clazz Class of the argument
   * @return Returns the value of the argument if present
   * @throws IllegalArgumentException If the argument isn't present an exception is thrown
   */
   <V> V getArgument(String name, Class<V> clazz) throws IllegalArgumentException;

  /**
   * Returns an empty string if input isn't found. Only present in AutoComplete callbacks
   *
   * @return The input for the current argument
   */
  @NotNull
  String currentArgInput();

  /**
   * The command as typed by the user in the chat box
   *
   * @return The full command string
   */
  String getInput();

  @NotNull
  Stream<ResourceLocation> getAvailableSounds();

  @NotNull
  Collection<String> getAllTeams();

  @NotNull
  Collection<String> getOnlinePlayerNames();


}
