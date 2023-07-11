package art.ameliah.brigadier.v1_20_1;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.exceptions.CommandException;
import art.ameliah.brigadier.core.service.CommandService;
import art.ameliah.brigadier.v1_20_1.transformers.CommandClassTransformer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;

@Singleton
@Implements(CommandService.class)
public class VersionedCommandService extends CommandService {

  private static VersionedCommandService instance;
  private final List<LiteralArgumentBuilder<SharedSuggestionProvider>> commandList = new ArrayList<>();
  private final HashMap<CommandClass, List<LiteralArgumentBuilder<SharedSuggestionProvider>>> transformerHashMap = new HashMap<>();
  public CommandDispatcher<SharedSuggestionProvider> dispatcher;

  @Inject
  public VersionedCommandService() {
    instance = this;
  }

  public static VersionedCommandService get() {
    return instance;
  }

  public List<LiteralArgumentBuilder<SharedSuggestionProvider>> getCommandList() {
    return commandList;
  }

  @Override
  public boolean registerCommand(@NotNull CommandClass commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");

    CommandClassTransformer<CommandClass> transformer;
    List<LiteralArgumentBuilder<SharedSuggestionProvider>> transformedCommands;
    try {
      transformer = new CommandClassTransformer<>(commandClass);
    } catch (CommandException e) {
      System.out.println(
          "An exception occurred initialising CommandClassTransformer for " + commandClass);
      e.printStackTrace();
      return false;
    }

    try {
      transformedCommands = transformer.getCommands();
    } catch (CommandException e) {
      System.out.println("An exception occurred getting commands for " + commandClass);
      e.printStackTrace();
      return false;
    }

    this.commandList.addAll(transformedCommands);
    this.commandClasses.add(commandClass);
    this.transformerHashMap.put(commandClass, transformedCommands);
    return true;
  }

  @Override
  public boolean isCustomCommand(String root) {
    for (LiteralArgumentBuilder<SharedSuggestionProvider> cmd : this.commandList) {
      if (cmd.getLiteral().equals(root)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean removeCommand(@NotNull CommandClass commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");

    List<LiteralArgumentBuilder<SharedSuggestionProvider>> commands = this.transformerHashMap.get(
        commandClass);
    if (commands == null) {
      return false;
    }

    this.transformerHashMap.remove(commandClass, commands);
    this.commandList.removeAll(commands);
    return this.commandClasses.remove(commandClass);
  }
}
