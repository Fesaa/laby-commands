package art.ameliah.brigadier.v1_20_1.wrappers;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;

public class McCommand extends LiteralArgumentBuilder<SharedSuggestionProvider> {

  protected McCommand(String literal) {
    super(literal);
  }

  public static McCommand literal(String name) {
    return new McCommand(name);
  }

  public McCommand then(ArgumentBuilder<SharedSuggestionProvider, ?> literal) {
    return (McCommand) super.then(literal);
  }
}
