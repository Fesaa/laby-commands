package art.ameliah.brigadier.core.models.types.defaults.pos;

import art.ameliah.brigadier.core.models.types.StringReaderWrapper;
import art.ameliah.brigadier.core.models.exceptions.CommandException;

public class WorldCoordinate {

  private final boolean relative;
  private final float value;


  public WorldCoordinate(boolean relative, float value) {
    this.relative = relative;
    this.value = value;
  }

  public static WorldCoordinate parseInt(StringReaderWrapper reader) throws CommandException {
    if (!reader.canRead()) {
      throw new CommandException("Expected an integer");
    } else {
      boolean rel = isRelative(reader);
      float val;
      if (reader.canRead() && reader.peek() != ' ') {
        val = rel ? reader.readFloat() : (float) reader.readInt();
      } else {
        val = 0.0F;
      }

      return new WorldCoordinate(rel, val);
    }
  }

  public static boolean isRelative(StringReaderWrapper reader) {
    boolean $$2;
    if (reader.peek() == '~') {
      $$2 = true;
      reader.skip();
    } else {
      $$2 = false;
    }

    return $$2;
  }

  public float get(float pos) {
    return this.relative ? this.value + pos : this.value;
  }

  public float get() {
    return this.value;
  }

}
