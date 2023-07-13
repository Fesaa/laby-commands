package art.ameliah.brigadier.core.models.exceptions;


import net.labymod.api.util.I18n;

public class CommandException extends Exception {

  public CommandException() {
    super(I18n.translate("brigadier.commands.exceptions.unknown"));
  }

  public CommandException(String msg) {
    super(msg);
  }

  public CommandException(String msg, Object... args) {
    super(String.format(msg, args));
  }

}
