package art.ameliah.brigadier.v1_20_1;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.CommandContext;
import art.ameliah.brigadier.core.models.exceptions.CommandException;
import art.ameliah.brigadier.core.service.CommandService;
import art.ameliah.brigadier.v1_20_1.transformers.CommandClassTransformer;
import art.ameliah.brigadier.v1_20_1.wrappers.McCommand;
import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.labymod.api.util.I18n;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Implements(CommandService.class)
public class VersionedCommandService<T extends CommandContext> extends CommandService<T> {

  private static final Logger logger = LoggerFactory.getLogger(VersionedCommandService.class);
  private static VersionedCommandService<?> instance;
  private final List<McCommand<T>> commandList = new ArrayList<>();
  private final HashMap<CommandClass<T>, List<McCommand<T>>> transformerHashMap = new HashMap<>();
  public CommandDispatcher<SharedSuggestionProvider> dispatcher;

  @Inject
  public VersionedCommandService() {
    instance = this;
  }

  public static VersionedCommandService<?> get() {
    return instance;
  }

  public List<McCommand<T>> getCommandList() {
    return commandList;
  }

  @Override
  public boolean registerCommand(@NotNull CommandClass<T> commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");

    CommandClassTransformer<CommandClass<T>, T> transformer;
    List<McCommand<T>> transformedCommands;
    try {
      transformer = new CommandClassTransformer<>(commandClass);
    } catch (CommandException e) {
      logger.error(I18n.translate("brigadier.exceptions.commands.cannotInitialiseCommandClass", commandClass),
          e);
      return false;
    }

    try {
      transformedCommands = transformer.getCommands();
    } catch (CommandException e) {
      logger.error(I18n.translate("brigadier.exceptions.commands.cannotGetCommands", commandClass), e);
      return false;
    }

    this.commandList.addAll(transformedCommands);
    this.commandClasses.add(commandClass);
    this.transformerHashMap.put(commandClass, transformedCommands);
    return true;
  }

  @Override
  public boolean isCustomCommand(String root) {
    for (McCommand<T> cmd : this.commandList) {
      if (cmd.getLiteral().equals(root)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean removeCommand(@NotNull CommandClass<T> commandClass) {
    Objects.requireNonNull(commandClass, "commandClass");

    List<McCommand<T>> commands = this.transformerHashMap.get(
        commandClass);
    if (commands == null) {
      return false;
    }

    this.transformerHashMap.remove(commandClass, commands);
    this.commandList.removeAll(commands);
    return this.commandClasses.remove(commandClass);
  }
}
