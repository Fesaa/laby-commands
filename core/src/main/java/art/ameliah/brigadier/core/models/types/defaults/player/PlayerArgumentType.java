package art.ameliah.brigadier.core.models.types.defaults.player;

import art.ameliah.brigadier.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.brigadier.core.models.types.CustomArgumentType;
import art.ameliah.brigadier.core.models.types.CustomSuggestion;
import art.ameliah.brigadier.core.models.types.CustomSuggestions;
import art.ameliah.brigadier.core.models.types.StringReaderWrapper;
import art.ameliah.brigadier.core.models.exceptions.SyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.util.I18n;

public class PlayerArgumentType implements CustomArgumentType<Player, MyCustomCommandContext> {

  private final LabyAPI api = Laby.labyAPI();

  @Override
  public Player parse(StringReaderWrapper reader) throws SyntaxException {
    int start = reader.getCursor();
    while (reader.canRead() && this.stringCollector(reader.peek())) {
      reader.skip();
    }
    String string = reader.getString().substring(start, reader.getCursor());
    Optional<Player> player = api.minecraft()
        .clientWorld()
        .getPlayer(string);
    if (player.isEmpty()) {
      reader.setCursor(start);
      throw new SyntaxException(I18n.translate("brigadier.exceptions.types.notAPlayer", string));
    }
    return player.get();
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
    List<CustomSuggestion> players = api
        .minecraft()
        .clientWorld()
        .getPlayers()
        .stream()
        .map(Player::getName)
        .map(CustomSuggestion::withText)
        .toList();
    return CompletableFuture.completedFuture(new CustomSuggestions(players));
  }
}
