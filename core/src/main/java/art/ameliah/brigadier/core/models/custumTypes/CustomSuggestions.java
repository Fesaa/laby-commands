package art.ameliah.brigadier.core.models.custumTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper around the Mojang Suggestions
 */
public class CustomSuggestions {

  private static final CustomSuggestions EMPTY = new CustomSuggestions(new ArrayList<>());

  private final List<CustomSuggestion> customSuggestions;


  public CustomSuggestions(final List<CustomSuggestion> customSuggestions) {
    this.customSuggestions = customSuggestions;
  }

  public static CompletableFuture<CustomSuggestions> empty() {
    return CompletableFuture.completedFuture(EMPTY);
  }

  public List<CustomSuggestion> getSuggestions() {
    return customSuggestions;
  }

}
