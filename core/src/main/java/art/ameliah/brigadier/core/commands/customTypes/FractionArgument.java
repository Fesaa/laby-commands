package art.ameliah.brigadier.core.commands.customTypes;

import art.ameliah.brigadier.core.models.custumTypes.CustomArgumentType;
import art.ameliah.brigadier.core.models.exceptions.SyntaxException;
import java.util.Collection;
import java.util.List;

public class FractionArgument implements CustomArgumentType<Float, MyCustomCommandContext> {

  @Override
  public Collection<String> getExamples() {
    return List.of("1/2", "4/6", "9/2");
  }

  @Override
  public Float parse(String string) throws SyntaxException {
    StringBuilder numeratorString = new StringBuilder();
    StringBuilder denominatorString = new StringBuilder();
    boolean passedSlash = false;

    for (int i = 0; i < string.length(); i++) {
      String c = String.valueOf(string.charAt(i));
      if (c.equals("/")) {
        passedSlash = true;
        continue;
      }
      if (passedSlash) {
        denominatorString.append(c);
      } else {
        numeratorString.append(c);
      }
    }
    try {
      float numerator = Float.parseFloat(numeratorString.toString().trim());
      float denominator = denominatorString.length() == 0 ? 1
          : Float.parseFloat(denominatorString.toString().trim());
      return numerator / denominator;
    } catch (NumberFormatException e) {
      throw new SyntaxException("Expected a fraction got %s / %s", numeratorString,
          denominatorString);
    }
  }

  @Override
  public Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }

  @Override
  public boolean stringCollector(char c) {
    return (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '/';
  }
}
