package art.ameliah.brigadier.v1_18_2.mixins;

import art.ameliah.brigadier.v1_18_2.VersionedCommandService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {

  @Shadow
  @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;

  @Inject(method = "<init>", at = @At("TAIL"))
  private void injectAtConstructorEnd(CallbackInfo ci) {
    for (LiteralArgumentBuilder<CommandSourceStack> cmd : VersionedCommandService.get()
        .getCommandList()) {
      dispatcher.register(cmd);
    }
  }

}
