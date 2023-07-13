package art.ameliah.brigadier.core.models.custumTypes;

import art.ameliah.brigadier.core.models.defaultCustomTypes.pos.TextCoordinates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.include.com.google.common.base.Strings;

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

  public static @NotNull CompletableFuture<CustomSuggestions> suggestCoordinates(String input,
      Collection<TextCoordinates> coords, Predicate<String> predicate) {
    List<String> suggestions = new ArrayList<>();

    if (Strings.isNullOrEmpty(input)) {
      for (TextCoordinates coordinates : coords) {
        if (predicate.test(coordinates.getAsString())) {
          suggestions.add(coordinates.x);
          suggestions.add(coordinates.x + " " + coordinates.y);
          suggestions.add(coordinates.getAsString());
        }
      }
    } else {
      String[] parts = input.split(" ");
      String maybeCoords;
      TextCoordinates coordinates;
      Iterator<TextCoordinates> iterator = coords.iterator();

      if (parts.length == 1) {
        coordinates = iterator.next();
        maybeCoords = parts[0] + " " + coordinates.y + " " + coordinates.z;
        if (predicate.test(maybeCoords)) {
          suggestions.add(parts[0] + " " + coordinates.y);
          suggestions.add(maybeCoords);
        }
      } else if (parts.length == 2) {
        coordinates = iterator.next();
        maybeCoords = parts[0] + " " + parts[1] + " " + coordinates.z;
        if (predicate.test(maybeCoords)) {
          suggestions.add(maybeCoords);
        }
      }
    }

    return CompletableFuture.completedFuture(
        new CustomSuggestions(
            suggestions.stream()
                .map(CustomSuggestion::withText)
                .toList()
        ));

  }

  public List<CustomSuggestion> getSuggestions() {
    return customSuggestions;
  }

}
