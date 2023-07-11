package art.ameliah.brigadier.core.models.custumTypes;

public class CustomSuggestion {

  private final String text;
  private final String tooltip;

  public CustomSuggestion(String text, String tooltip) {
    this.text = text;
    this.tooltip = tooltip;
  }

  public CustomSuggestion(String text) {
    this(text, null);
  }

  public String getText() {
    return text;
  }

  public String getTooltip() {
    return tooltip;
  }
}
