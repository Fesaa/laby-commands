package art.ameliah.brigadier.core.models.exceptions;


public class CommandException extends Exception {

  public CommandException() {
    super("An unknown error occurred building the command");
  }

  public CommandException(String msg) {
    super(msg);
  }

  public CommandException(String msg, Object... args) {
    super(String.format(msg, args));
  }

}
