package art.ameliah.laby.addons.library.commands.v1_20_1.mixins;

import art.ameliah.laby.addons.library.commands.v1_20_1.VersionedCommandService;
import art.ameliah.laby.addons.library.commands.v1_20_1.wrappers.McCommand;
import art.ameliah.laby.addons.library.commands.v1_20_1.wrappers.SharedSuggestionProviderWrapper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin {


  @Shadow
  @Final
  EditBox input;

  @Shadow
  @Nullable
  private CompletableFuture<Suggestions> pendingSuggestions;

@Inject(method = "updateCommandInfo", at = @At("TAIL"))
  public void updateCommandInfo(CallbackInfo ci) {
    String $$0 = input.getValue();
    System.out.println($$0);
    var dispatcher = VersionedCommandService.get().injectDispatcher;
    var parseResult = dispatcher.parse($$0, new SharedSuggestionProviderWrapper());
    var suggestions = dispatcher.getCompletionSuggestions(parseResult);

    if (pendingSuggestions == null) {
      pendingSuggestions = suggestions;
      return;
    }

    pendingSuggestions = pendingSuggestions.thenCombineAsync(suggestions, (sug1, sug2) -> {
      List<Suggestion> suggestionList1 = sug1.getList();
      List<Suggestion> suggestionList2 = sug2.getList();

      System.out.println(suggestionList1);
      System.out.println(suggestionList2);

      suggestionList1.addAll(suggestionList2);
      return Suggestions.create($$0, suggestionList1);
    });
  }

}
