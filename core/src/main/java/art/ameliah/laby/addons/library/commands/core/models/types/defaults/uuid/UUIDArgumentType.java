package art.ameliah.laby.addons.library.commands.core.models.types.defaults.uuid;

import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.models.types.CustomArgumentType;
import art.ameliah.laby.addons.library.commands.core.models.types.StringReaderWrapper;
import art.ameliah.laby.addons.library.commands.core.models.exceptions.CommandException;
import art.ameliah.laby.addons.library.commands.core.models.exceptions.SyntaxException;
import net.labymod.api.util.I18n;
import java.util.UUID;

public class UUIDArgumentType implements CustomArgumentType<UUID, MyCustomCommandContext> {

  @Override
  public UUID parse(StringReaderWrapper reader) throws SyntaxException, CommandException {
    int start = reader.getCursor();
    while (reader.canRead() && reader.peek() != ' ') {
      reader.skip();
    }
    String string = reader.getString().substring(start, reader.getCursor());
    try {
      return UUID.fromString(string);
    } catch (IllegalArgumentException ignored) {
      reader.setCursor(start);
      throw new SyntaxException(I18n.translate("brigadier.exceptions.types.notAUUID", string));
    }
  }

  @Override
  public Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }

  @Override
  public boolean stringCollector(char c) {
    return false;
  }
}
