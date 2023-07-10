package art.ameliah.brigadier.core.models;

import java.util.Collection;
import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.reference.annotation.Referenceable;
import net.labymod.api.util.math.vector.FloatVector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Nullable
@Referenceable
public abstract class CommandContext {

  protected final LabyAPI labyAPI = Laby.labyAPI();

  public abstract <V> V getArgument(String name, Class<V> clazz) throws IllegalArgumentException;

  @NotNull
  public abstract String currentArgInput();

  public abstract String getInput();

  public abstract Collection<String> getOnlinePlayerNames();

  public ClientPlayer getClientPlayer() {
    return this.labyAPI.minecraft().getClientPlayer();
  }

  ;

  public FloatVector3 getPosition() {
    ClientPlayer player = this.labyAPI.minecraft().getClientPlayer();
    if (player == null) {
      return null;
    }
    return player.position();
  }

  ;

  public void sendChatMessage(String message) {
    this.labyAPI.minecraft().chatExecutor().chat(message);
  }

  ;

  public void displayClientMessage(String message) {
    this.labyAPI.minecraft().chatExecutor().displayClientMessage(message);
  }

  ;

  public void displayClientMessage(Component message) {
    this.labyAPI.minecraft().chatExecutor().displayClientMessage(message);
  }

  ;

}
