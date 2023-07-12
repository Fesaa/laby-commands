package art.ameliah.brigadier.v1_20_1;

import art.ameliah.brigadier.core.models.CommandSource;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.stream.Stream;
import net.labymod.api.client.resources.ResourceLocation;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;

public class VersionedCommandSource extends CommandSource {

  private final com.mojang.brigadier.context.CommandContext<SharedSuggestionProvider> ctx;
  private final Parameter parameter;

  public VersionedCommandSource(
      com.mojang.brigadier.context.CommandContext<SharedSuggestionProvider> ctx,
      Parameter parameter) {
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
  public @NotNull Stream<ResourceLocation> getAvailableSounds() {
    return this.ctx.getSource().getAvailableSounds()
        .map(loc -> ResourceLocation.create(loc.getNamespace(), loc.getPath()));
  }

  @Override
  public @NotNull Collection<String> getAllTeams() {
    return this.ctx.getSource().getAllTeams();
  }

  @Override
  public @NotNull Collection<String> getOnlinePlayerNames() {
    return this.ctx.getSource().getOnlinePlayerNames();
  }
}
