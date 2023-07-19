package art.ameliah.laby.addons.library.commands.v1_20_1;

import art.ameliah.laby.addons.library.commands.core.models.CommandSource;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VersionedCommandSource extends CommandSource {

  @Nullable
  private final com.mojang.brigadier.context.CommandContext<SharedSuggestionProvider> ctx;
  private final Parameter parameter;
  private final String remaining;

  public VersionedCommandSource(
      com.mojang.brigadier.context.@Nullable CommandContext<SharedSuggestionProvider> ctx,
      Parameter parameter) {
    this.ctx = ctx;
    this.parameter = parameter;
    this.remaining = null;
  }

  public VersionedCommandSource(
      com.mojang.brigadier.context.@Nullable CommandContext<SharedSuggestionProvider> ctx,
      Parameter parameter, String remaining) {
    this.ctx = ctx;
    this.parameter = parameter;
    this.remaining = remaining;
  }

  @Override
  public <V> V getArgument(String name, Class<V> clazz) throws IllegalArgumentException {
    if (this.ctx == null) {
      throw new IllegalArgumentException(I18n.translate("brigadier.exceptions.commands.noCommandSource"));
    }
    return this.ctx.getArgument(name, clazz);
  }

  @Override
  @NotNull
  public String currentArgInput() {
    if (this.remaining != null) {
      return remaining;
    }
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
    return this.ctx == null ? "" : this.ctx.getInput();
  }

  @Override
  public @NotNull Stream<ResourceLocation> getAvailableSounds() {
    if (this.ctx == null) {
      return Stream.empty();
    }
    return this.ctx.getSource().getAvailableSounds()
        .map(loc -> ResourceLocation.create(loc.getNamespace(), loc.getPath()));
  }

  @Override
  public @NotNull Collection<String> getAllTeams() {
    if (this.ctx == null) {
      return Collections.emptyList();
    }
    return this.ctx.getSource().getAllTeams();
  }

  @Override
  public @NotNull Collection<String> getOnlinePlayerNames() {
    if (this.ctx == null) {
      return Collections.emptyList();
    }
    return this.ctx.getSource().getOnlinePlayerNames();
  }
}
