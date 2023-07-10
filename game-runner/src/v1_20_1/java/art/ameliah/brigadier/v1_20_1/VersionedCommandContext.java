package art.ameliah.brigadier.v1_20_1;

import art.ameliah.brigadier.core.models.CommandContext;
import java.lang.reflect.Parameter;
import java.util.Collection;
import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public class VersionedCommandContext extends CommandContext {

  private final com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx;
  private final Parameter parameter;
  private final LabyAPI api = Laby.labyAPI();


  public VersionedCommandContext(
      com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
    this.ctx = ctx;
    this.parameter = null;
  }

  public VersionedCommandContext(
      com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, Parameter parameter) {
    this.ctx = ctx;
    this.parameter = parameter;
  }

  @Override
  public <V> V getArgument(String name, Class<V> clazz) throws IllegalArgumentException {
    return this.ctx.getArgument(name, clazz);
  }

  @Override
  @NotNull
  public String currentArgInput() {
    if (this.parameter == null) {
      return "";
    }
    try {
      Object object = this.getArgument(parameter.getName(), parameter.getType());
      return object == null ? "" : (String) object;
    } catch (IllegalArgumentException ignored) {
      return "";
    }
  }

  @Override
  public String getInput() {
    return this.ctx.getInput();
  }

  @Override
  public Collection<String> getOnlinePlayerNames() {
    return this.ctx.getSource().getOnlinePlayerNames();
  }
}