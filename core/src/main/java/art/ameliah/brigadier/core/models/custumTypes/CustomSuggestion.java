package art.ameliah.brigadier.core.models.custumTypes;

import art.ameliah.brigadier.core.models.defaultCustomTypes.pos.TextCoordinates;

/**
 * Wrapper around the Mojang suggestion
 */
public class CustomSuggestion {

  private final String text;
  private final String tooltip;

  private CustomSuggestion(String text, String tooltip) {
    this.text = text;
    this.tooltip = tooltip;
  }

  private CustomSuggestion(String text) {
    this(text, null);
  }

  public static CustomSuggestion withText(String text) {
    return new CustomSuggestion(text);
  }

  public static CustomSuggestion withTextAndToolTip(String text, String tooltip) {
    return new CustomSuggestion(text, tooltip);
  }

  public static CustomSuggestion fromTextCoordinate(TextCoordinates coords) {
    return new CustomSuggestion(coords.getAsString());
  }


  public String getText() {
    return text;
  }

  public String getTooltip() {
    return tooltip;
  }
}
