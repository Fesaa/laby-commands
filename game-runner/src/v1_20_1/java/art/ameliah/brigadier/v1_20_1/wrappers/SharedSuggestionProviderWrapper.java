package art.ameliah.brigadier.v1_20_1.wrappers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;

public class SharedSuggestionProviderWrapper implements SharedSuggestionProvider {

  @Override
  public Collection<String> getOnlinePlayerNames() {
    return null;
  }

  @Override
  public Collection<String> getAllTeams() {
    return null;
  }

  @Override
  public Stream<ResourceLocation> getAvailableSounds() {
    return null;
  }

  @Override
  public Stream<ResourceLocation> getRecipeNames() {
    return null;
  }

  @Override
  public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> commandContext) {
    return null;
  }

  @Override
  public Set<ResourceKey<Level>> levels() {
    return null;
  }

  @Override
  public RegistryAccess registryAccess() {
    return null;
  }

  @Override
  public FeatureFlagSet enabledFeatures() {
    return null;
  }

  @Override
  public CompletableFuture<Suggestions> suggestRegistryElements(
      ResourceKey<? extends Registry<?>> resourceKey, ElementSuggestionType elementSuggestionType,
      SuggestionsBuilder suggestionsBuilder, CommandContext<?> commandContext) {
    return null;
  }

  @Override
  public boolean hasPermission(int i) {
    return false;
  }
}
