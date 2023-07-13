package art.ameliah.brigadier.core.models.types.defaults.uuid;

import art.ameliah.brigadier.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.brigadier.core.models.types.CustomArgumentType;
import art.ameliah.brigadier.core.models.types.StringReaderWrapper;
import art.ameliah.brigadier.core.models.exceptions.CommandException;
import art.ameliah.brigadier.core.models.exceptions.SyntaxException;
import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDArgumentType implements CustomArgumentType<UUID, MyCustomCommandContext> {

  private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");

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
      throw new SyntaxException(string + " is not a valid uuid");
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
