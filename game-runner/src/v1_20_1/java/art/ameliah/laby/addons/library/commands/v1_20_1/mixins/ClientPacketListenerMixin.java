package art.ameliah.laby.addons.library.commands.v1_20_1.mixins;

import art.ameliah.laby.addons.library.commands.v1_20_1.VersionedCommandService;
import art.ameliah.laby.addons.library.commands.v1_20_1.transformers.ContextTransformer;
import com.mojang.brigadier.CommandDispatcher;
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
    VersionedCommandService.get()
        .getCommandList()
        .stream()
        .filter(cmd -> cmd.shouldRegister(
            ContextTransformer.createCorrectCtx(null, null, cmd.getCommandContextClass())))
        .forEach(commands::register);
    VersionedCommandService.get().dispatcher = commands;
  }

}
