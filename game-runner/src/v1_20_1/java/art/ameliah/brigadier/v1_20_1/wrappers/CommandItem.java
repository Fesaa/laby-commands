package art.ameliah.brigadier.v1_20_1.wrappers;

import art.ameliah.brigadier.core.utils.Item;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;

public class CommandItem implements Item<LiteralArgumentBuilder<SharedSuggestionProvider>> {

  private Item<LiteralArgumentBuilder<SharedSuggestionProvider>> parent;
  private LiteralArgumentBuilder<SharedSuggestionProvider> self;

  public CommandItem(LiteralArgumentBuilder<SharedSuggestionProvider> self,
      Item<LiteralArgumentBuilder<SharedSuggestionProvider>> parent) {
    this.parent = parent;
    this.self = self;
  }

  @Override
  public boolean hasParent() {
    return this.parent != null;
  }

  @Override
  public Item<LiteralArgumentBuilder<SharedSuggestionProvider>> getParent() {
    return this.parent;
  }

  @Override
  public void setParent(Item<LiteralArgumentBuilder<SharedSuggestionProvider>> parent) {
    this.parent = parent;
  }

  @Override
  public void updateSelf(
      LiteralArgumentBuilder<SharedSuggestionProvider> self) {
    this.self = self;
  }

  @Override
  public LiteralArgumentBuilder<SharedSuggestionProvider> getSelf() {
    return this.self;
  }

  @Override
  public String toString() {
    return String.format("Name: %s; Parent: [%s]", this.self.getLiteral(), this.parent);
  }

}
