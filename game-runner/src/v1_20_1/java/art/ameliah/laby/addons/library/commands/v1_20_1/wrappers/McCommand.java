package art.ameliah.laby.addons.library.commands.v1_20_1.wrappers;

import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.CommandContext;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Function;
import net.minecraft.commands.SharedSuggestionProvider;

public class McCommand<T extends CommandContext> extends
    LiteralArgumentBuilder<SharedSuggestionProvider> {

  private final Function<T, Boolean> shouldRegisterSupplier;
  private final CommandClass<T> commandClass;

  protected McCommand(String literal, Function<T, Boolean> supplier, CommandClass<T> commandClass) {
    super(literal);
    this.shouldRegisterSupplier = supplier;
    this.commandClass = commandClass;
  }

  public static <T extends CommandContext> McCommand<T> literal(String name,
      Function<T, Boolean> supplier, CommandClass<T> commandClass) {
    return new McCommand<>(name, supplier, commandClass);
  }

  public McCommand<T> then(ArgumentBuilder<SharedSuggestionProvider, ?> literal) {
    return (McCommand<T>) super.then(literal);
  }

  public boolean shouldRegister(T ctx) {
    return shouldRegisterSupplier.apply(ctx);
  }

  public Class<T> getCommandContextClass() {
    return commandClass.getCommandContextClass();
  }
}
