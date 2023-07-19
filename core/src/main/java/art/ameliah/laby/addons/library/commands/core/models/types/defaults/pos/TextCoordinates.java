package art.ameliah.laby.addons.library.commands.core.models.types.defaults.pos;

public class TextCoordinates {

  public static final TextCoordinates DEFAULT_GLOBAL = new TextCoordinates("~", "~", "~");
  public final String x;
  public final String y;
  public final String z;

  public TextCoordinates(String $$0, String $$1, String $$2) {
    this.x = $$0;
    this.y = $$1;
    this.z = $$2;
  }

  public String getAsString() {
    return String.format("%s %s %s", x, y, z);
  }
}
