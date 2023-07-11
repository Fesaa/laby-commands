package art.ameliah.brigadier.v1_20_1.mixins;

import art.ameliah.brigadier.v1_20_1.VersionedCommandService;
import art.ameliah.brigadier.v1_20_1.wrappers.SharedSuggestionProviderWrapper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

  @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
  public void sendPacketInject(Packet<?> $$0, PacketSendListener $$1, CallbackInfo ci) {
    if ($$0.getClass().equals(ServerboundChatCommandPacket.class)) {
      ServerboundChatCommandPacket packet = (ServerboundChatCommandPacket) $$0;
      String[] parts = packet.command().split(" ");
      if (parts.length > 0 && VersionedCommandService.get().isCustomCommand(parts[0])) {
        try {
          VersionedCommandService.get().dispatcher.execute(packet.command(),
              new SharedSuggestionProviderWrapper());
        } catch (CommandSyntaxException e) {
          Minecraft.getInstance().gui.getChat().addMessage(
              Component.literal(e.getMessage()).withStyle(ChatFormatting.RED));
        }
        ci.cancel();
      }
    }

  }

}
