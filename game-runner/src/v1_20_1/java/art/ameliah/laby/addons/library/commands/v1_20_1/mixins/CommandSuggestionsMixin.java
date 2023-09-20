package art.ameliah.laby.addons.library.commands.v1_20_1.mixins;

import art.ameliah.laby.addons.library.commands.v1_20_1.VersionedCommandService;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {

  @Shadow
  @Final
  EditBox input;

  @Shadow
  @Nullable
  private CompletableFuture<Suggestions> pendingSuggestions;

  @Shadow
  @Final
  Minecraft minecraft;

  @Shadow
  @Nullable
  private ParseResults<SharedSuggestionProvider> currentParse;

  @Inject(method = "updateUsageInfo", at = @At("HEAD"))
  private void updateUsageInfo(CallbackInfo ci) {
    if (minecraft.player == null || currentParse == null) {
      return;
    }
    String $$0 = input.getValue();
    StringReader $$1 = new StringReader($$0);
    boolean $$2 = $$1.canRead() && $$1.peek() == '/';
    if ($$2) {
      $$1.skip();
      var dispatcher = VersionedCommandService.get().injectDispatcher;
      var parseResult = dispatcher.parse($$1, minecraft.player.connection.getSuggestionsProvider());

      if (parseResult.getExceptions().isEmpty()) {
        currentParse.getExceptions().clear();
        var customSuggestions = dispatcher.getCompletionSuggestions(parseResult, input.getCursorPosition());
        if (pendingSuggestions == null) {
          pendingSuggestions = customSuggestions;
        } else {
          List<Suggestion> sug1 = pendingSuggestions.join().getList();
          List<Suggestion> sug2 = customSuggestions.join().getList();
          sug1.addAll(sug2);
          pendingSuggestions = CompletableFuture.completedFuture(Suggestions.create($$0, sug1));
        }
      } else {
        parseResult.getExceptions().forEach((k, v) -> {
          currentParse.getExceptions().put(k, v);
        });
      }
    }
  }
}
