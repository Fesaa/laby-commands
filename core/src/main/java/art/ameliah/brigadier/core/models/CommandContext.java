package art.ameliah.brigadier.core.models;

import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.network.server.ServerData;
import net.labymod.api.util.math.vector.FloatVector3;
import org.jetbrains.annotations.Nullable;

public class CommandContext {

  private final CommandSource source;
  private final LabyAPI labyAPI = Laby.labyAPI();

  public CommandContext(CommandSource source) {
    this.source = source;
  }

  public CommandSource getSource() {
    return this.source;
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

}
