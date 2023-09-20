package art.ameliah.laby.addons.library.commands.v1_20_1.mixins;

import art.ameliah.laby.addons.library.commands.core.service.CommandService.CommandType;
import art.ameliah.laby.addons.library.commands.v1_20_1.VersionedCommandService;
import art.ameliah.laby.addons.library.commands.v1_20_1.wrappers.SharedSuggestionProviderWrapper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

  @Shadow
  @Final
  private static Logger LOGGER;

  @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
  public <S> void sendPacketInject(Packet<?> $$0, PacketSendListener $$1, CallbackInfo ci) {
    if (!$$0.getClass().equals(ServerboundChatCommandPacket.class)) {
      return;
    }

    ServerboundChatCommandPacket packet = (ServerboundChatCommandPacket) $$0;
    String[] parts = packet.command().split(" ");
    VersionedCommandService<?> service = VersionedCommandService.get();
    if (parts.length == 0) {
      return;
    }

    CommandType commandType = service.getCommandType(parts[0]);
    if (commandType.equals(CommandType.SERVER)) {
      return;
    }

    int result = 1;
    SharedSuggestionProviderWrapper s = new SharedSuggestionProviderWrapper();
    if (commandType.equals(CommandType.CUSTOM)) {
      try {
        result = service.dispatcher.execute(packet.command(),s);
      } catch (CommandSyntaxException e) {
        Minecraft.getInstance().gui.getChat().addMessage(
            Component.literal(e.getMessage()).withStyle(ChatFormatting.RED));
      }
    } else {
      result = 0;
      try {
        result = service.injectDispatcher.execute(packet.command(),s);
      } catch (CommandSyntaxException e) {
        LOGGER.error(e.getMessage());
      }
    }
    if (result == 1) {
      ci.cancel();
    }
  }

}
