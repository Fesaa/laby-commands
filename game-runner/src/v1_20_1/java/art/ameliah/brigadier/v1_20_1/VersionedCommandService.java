package art.ameliah.brigadier.v1_20_1;

import art.ameliah.brigadier.core.CommandException;
import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.service.CommandService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

@Singleton
@Implements(CommandService.class)
public class VersionedCommandService extends CommandService {

  private static VersionedCommandService instance;

  private final List<LiteralArgumentBuilder<CommandSourceStack>> commandList = new ArrayList<>();
  private final HashMap<CommandClass, List<LiteralArgumentBuilder<CommandSourceStack>>> transformerHashMap = new HashMap<>();

  @Inject
  public VersionedCommandService() {
    instance = this;
  }

  public static VersionedCommandService get() {
    return instance;
  }

  public List<LiteralArgumentBuilder<CommandSourceStack>> getCommandList() {
    return commandList;
  }

  @Override
  public boolean registerCommand(@NotNull CommandClass commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");

    CommandClassTransformer<CommandClass> transformer;
    List<LiteralArgumentBuilder<CommandSourceStack>> transformedCommands;
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
  public boolean removeCommand(@NotNull CommandClass commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");

    List<LiteralArgumentBuilder<CommandSourceStack>> commands = this.transformerHashMap.get(
        commandClass);
    if (commands == null) {
      return false;
    }

    this.transformerHashMap.remove(commandClass, commands);
    this.commandList.removeAll(commands);
    return this.commandClasses.remove(commandClass);
  }
}
