package art.ameliah.brigadier.v1_20_1.mixins;

import art.ameliah.brigadier.core.CommandException;
import art.ameliah.brigadier.core.CommandService;
import art.ameliah.brigadier.v1_20_1.Transformer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
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
    List<List<LiteralArgumentBuilder<CommandSourceStack>>> cmds = new ArrayList<>();
    for (Object command : CommandService.getCommandClasses()) {
      List<LiteralArgumentBuilder<CommandSourceStack>> commands;
      try {
        commands = Transformer.transform(command);
      } catch (CommandException e) {
        e.printStackTrace();
        continue;
      }
      cmds.add(commands);
    }
    for (List<LiteralArgumentBuilder<CommandSourceStack>> cmdList : cmds) {
      for (LiteralArgumentBuilder<CommandSourceStack> cmd : cmdList) {
        dispatcher.register(cmd);
      }
    }
  }

}
