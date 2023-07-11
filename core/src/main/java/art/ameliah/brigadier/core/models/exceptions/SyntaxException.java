package art.ameliah.brigadier.core.models.exceptions;

public class SyntaxException extends Exception {

  public SyntaxException(String s) {
    super(s);
  }

  public SyntaxException(String msg, Object... args) {
    super(String.format(msg, args));
  }

  public SyntaxException(Exception e) {
    super(e);
  }

}
