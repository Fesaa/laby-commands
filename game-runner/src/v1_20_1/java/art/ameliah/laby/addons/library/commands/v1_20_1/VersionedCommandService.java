package art.ameliah.laby.addons.library.commands.v1_20_1;

import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.CommandContext;
import art.ameliah.laby.addons.library.commands.core.models.exceptions.CommandException;
import art.ameliah.laby.addons.library.commands.core.service.CommandService;
import art.ameliah.laby.addons.library.commands.v1_20_1.transformers.CommandClassTransformer;
import art.ameliah.laby.addons.library.commands.v1_20_1.wrappers.McCommand;
import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.labymod.api.util.I18n;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Implements(CommandService.class)
public class VersionedCommandService<T extends CommandContext> extends CommandService<T> {

  private static final Logger logger = LoggerFactory.getLogger(VersionedCommandService.class);
  private static VersionedCommandService<?> instance;

  public CommandDispatcher<SharedSuggestionProvider> dispatcher;
  public CommandDispatcher<SharedSuggestionProvider> injectDispatcher = new CommandDispatcher<>();
  private final List<McCommand<T>> commandList = new ArrayList<>();
  private final HashMap<CommandClass<T>, List<McCommand<T>>> transformerHashMap = new HashMap<>();

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
  public CommandType getCommandType(String root) {
    for (McCommand<T> cmd : this.commandList) {
      if (cmd.getLiteral().equals(root)) {
      return cmd.isInjected() ? CommandType.INJECT : CommandType.CUSTOM;
      }
    }
    return CommandType.SERVER;
  }

  public @Nullable McCommand<T> getCommand(String root) {
    for (McCommand<T> cmd : this.commandList) {
      if (cmd.getLiteral().equals(root)) {
        return cmd.isInjected() ? cmd : null;
      }
    }
    return null;
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
