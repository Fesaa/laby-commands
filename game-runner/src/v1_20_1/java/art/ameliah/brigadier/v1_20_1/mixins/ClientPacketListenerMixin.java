package art.ameliah.brigadier.v1_20_1.mixins;

import art.ameliah.brigadier.v1_20_1.VersionedCommandService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

  @Shadow
  private CommandDispatcher<SharedSuggestionProvider> commands;

  @Inject(method = "handleCommands", at = @At("TAIL"))
  public void handleCommandsInject(CallbackInfo ci) {

    for (LiteralArgumentBuilder<SharedSuggestionProvider> cmd : VersionedCommandService.get()
        .getCommandList()) {
      commands.register(cmd);
    }

    VersionedCommandService.get().dispatcher = commands;
  }

}
