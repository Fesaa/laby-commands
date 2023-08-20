package art.ameliah.laby.addons.library.commands.core.models.types.defaults.NetworkPlayer;

import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.models.exceptions.CommandException;
import art.ameliah.laby.addons.library.commands.core.models.exceptions.SyntaxException;
import art.ameliah.laby.addons.library.commands.core.models.types.CustomArgumentType;
import art.ameliah.laby.addons.library.commands.core.models.types.CustomSuggestion;
import art.ameliah.laby.addons.library.commands.core.models.types.CustomSuggestions;
import art.ameliah.laby.addons.library.commands.core.models.types.StringReaderWrapper;
import net.labymod.api.Laby;
import net.labymod.api.client.network.ClientPacketListener;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.mojang.GameProfile;
import net.labymod.api.util.I18n;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetworkPlayerArgumentType implements CustomArgumentType<NetworkPlayerInfo, MyCustomCommandContext> {

  @Override
  public NetworkPlayerInfo parse(StringReaderWrapper reader)
      throws SyntaxException, CommandException {
    int start = reader.getCursor();
    while (reader.canRead() && this.stringCollector(reader.peek())) {
      reader.skip();
    }
    String string = reader.getString().substring(start, reader.getCursor());

    ClientPacketListener clientPacketListener = Laby.labyAPI().minecraft().getClientPacketListener();
    if (clientPacketListener == null) {
      reader.setCursor(start);
      throw new CommandException("ClientPacketListener is null");
    }

    NetworkPlayerInfo networkPlayerInfo = clientPacketListener.getNetworkPlayerInfo(string);
    if (networkPlayerInfo == null) {
      reader.setCursor(start);
      throw new SyntaxException(I18n.translate("brigadier.exceptions.types.notAPlayer", string));
    }
    return networkPlayerInfo;
  }

  @Override
  public Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }

  @Override
  public boolean stringCollector(char c) {
    return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  @Override
  public CompletableFuture<CustomSuggestions> listSuggestions(MyCustomCommandContext ctx) {
    ClientPacketListener clientPacketListener = Laby.labyAPI().minecraft().getClientPacketListener();
    if (clientPacketListener == null) {
      return CompletableFuture.completedFuture(new CustomSuggestions(List.of()));
    }
    List<CustomSuggestion> players = clientPacketListener
        .getShownOnlinePlayers()
        .stream()
        .map(NetworkPlayerInfo::profile)
        .map(GameProfile::getUsername)
        .map(CustomSuggestion::withText)
        .toList();
    return CompletableFuture.completedFuture(new CustomSuggestions(players));
  }
}
