package art.ameliah.brigadier.core.models;

import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.network.server.ServerData;
import net.labymod.api.reference.annotation.Referenceable;
import net.labymod.api.util.math.vector.FloatVector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * First argument in Command callbacks and AutoComplete callbacks
 */
@Nullable
@Referenceable
public abstract class CommandContext {

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


  /**
   * @return The player executing the command
   */
  @Nullable
  public ClientPlayer getClientPlayer() {
    return this.labyAPI.minecraft().getClientPlayer();
  }

  /**
   * @return The position of the executing player
   */
  public FloatVector3 getPosition() {
    ClientPlayer player = this.getClientPlayer();
    return player == null ? null : player.position();
  }

  ;

  public ServerData getServerData() {
    return this.labyAPI.serverController().getCurrentServerData();
  }

  public void sendChatMessage(String message) {
    this.labyAPI.minecraft().chatExecutor().chat(message);
  }

  public void displayClientMessage(String message) {
    this.labyAPI.minecraft().chatExecutor().displayClientMessage(message);
  }

  public void displayClientMessage(Component message) {
    this.labyAPI.minecraft().chatExecutor().displayClientMessage(message);
  }

}
