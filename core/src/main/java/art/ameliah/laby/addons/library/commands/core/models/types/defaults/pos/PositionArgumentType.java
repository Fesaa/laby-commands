package art.ameliah.laby.addons.library.commands.core.models.types.defaults.pos;

import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.models.types.CustomArgumentType;
import art.ameliah.laby.addons.library.commands.core.models.types.CustomSuggestions;
import art.ameliah.laby.addons.library.commands.core.models.types.StringReaderWrapper;
import art.ameliah.laby.addons.library.commands.core.models.exceptions.CommandException;
import art.ameliah.laby.addons.library.commands.core.models.exceptions.SyntaxException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import net.labymod.api.Laby;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.util.I18n;
import net.labymod.api.util.math.vector.FloatVector3;

public class PositionArgumentType implements
    CustomArgumentType<FloatVector3, MyCustomCommandContext> {

  @Override
  public FloatVector3 parse(StringReaderWrapper reader) throws SyntaxException, CommandException {
    ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
    if (player == null) {
      throw new CommandException("No player?");
    }
    int start = reader.getCursor();
    WorldCoordinate x = WorldCoordinate.parseInt(reader);
    if (reader.canRead() && reader.peek() == ' ') {
      reader.skip();
      WorldCoordinate y = WorldCoordinate.parseInt(reader);
      if (reader.canRead() && reader.peek() == ' ') {
        reader.skip();
        WorldCoordinate z = WorldCoordinate.parseInt(reader);
        return new FloatVector3(
            x.get(player.getPosX()),
            y.get(player.getPosY()),
            z.get(player.getPosZ()));
      } else {
        reader.setCursor(start);
        throw new CommandException(I18n.translate("brigadier.exceptions.types.positionNotComplete"));
      }
    } else {
      reader.setCursor(start);
      throw new CommandException(I18n.translate("brigadier.exceptions.types.positionNotComplete"));
    }
  }

  @Override
  public Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }

  @Override
  public boolean stringCollector(char c) {
    return (c >= '0' && c <= '9') || c == ' ' || c == '~';
  }

  @Override
  public CompletableFuture<CustomSuggestions> listSuggestions(MyCustomCommandContext ctx) {
    if (ctx.getSource() == null) {
      return CustomSuggestions.empty();
    }
    String input = ctx.getSource().currentArgInput();
    return CustomSuggestions.suggestCoordinates(input,
        Collections.singleton(TextCoordinates.DEFAULT_GLOBAL), (s) -> {
          try {
            this.parse(new StringReaderWrapper(s));
            return true;
          } catch (CommandException | SyntaxException ignored) {
            return false;
          }
        });
  }
}
