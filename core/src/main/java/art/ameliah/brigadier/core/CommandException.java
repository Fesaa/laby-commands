package art.ameliah.brigadier.core;


public class CommandException extends Exception {

  public CommandException() {
    super("An unknown error occurred building the command");
  }

  public CommandException(String msg) {
    super(msg);
  }

}