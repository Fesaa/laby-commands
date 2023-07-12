package art.ameliah.brigadier.v1_20_1.wrappers;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.status.ServerStatus.Players;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;

public class SharedSuggestionProviderWrapper implements SharedSuggestionProvider {

  private final Minecraft mc = Minecraft.getInstance();

  @Override
  public @NotNull Collection<String> getOnlinePlayerNames() {
    ServerData serverData = mc.getCurrentServer();
    if (serverData == null) {
      return Lists.newArrayList();
    }
    Players players = serverData.players;
    if (players == null) {
      return Lists.newArrayList();
    }
    return players.sample().stream().map(GameProfile::getName).toList();
  }

  @Override
  public @NotNull Collection<String> getAllTeams() {
    Player player = mc.player;
    if (player == null) {
      return Lists.newArrayList();
    }
    Scoreboard scoreboard = player.getScoreboard();
    return scoreboard.getTeamNames();
  }

  @Override
  public @NotNull Stream<ResourceLocation> getAvailableSounds() {
    return BuiltInRegistries.SOUND_EVENT.stream().map(SoundEvent::getLocation);
  }

  /**
   * Not implemented
   *
   * @return null
   */
  @Override
  public Stream<ResourceLocation> getRecipeNames() {
    return null;
  }

  /**
   * Not implemented
   *
   * @param commandContext ctx
   * @return null
   */
  @Override
  public CompletableFuture<Suggestions> customSuggestion(
      @NotNull CommandContext<?> commandContext) {
    return null;
  }

  /**
   * Not implemented
   *
   * @return null
   */
  @Override
  public Set<ResourceKey<Level>> levels() {
    return null;
  }

  /**
   * Not implemented
   *
   * @return null
   */
  @Override
  public RegistryAccess registryAccess() {
    return null;
  }

  /**
   * Not implemented
   *
   * @return null
   */
  @Override
  public FeatureFlagSet enabledFeatures() {
    return null;
  }

  /**
   * Not implemented
   *
   * @return null
   */
  @Override
  public CompletableFuture<Suggestions> suggestRegistryElements(
      ResourceKey<? extends Registry<?>> resourceKey, ElementSuggestionType elementSuggestionType,
      SuggestionsBuilder suggestionsBuilder, CommandContext<?> commandContext) {
    return null;
  }

  /**
   * Not implemented
   *
   * @return null
   */
  @Override
  public boolean hasPermission(int i) {
    return false;
  }
}
