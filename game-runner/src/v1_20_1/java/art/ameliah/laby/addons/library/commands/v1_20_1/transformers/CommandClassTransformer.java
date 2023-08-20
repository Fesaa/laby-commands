package art.ameliah.laby.addons.library.commands.v1_20_1.transformers;

import static art.ameliah.laby.addons.library.commands.v1_20_1.transformers.HelperTransformers.arrayMapper;
import static art.ameliah.laby.addons.library.commands.v1_20_1.transformers.HelperTransformers.constructCustomArgumentFromContext;
import static art.ameliah.laby.addons.library.commands.v1_20_1.transformers.HelperTransformers.getArgument;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.annotations.AutoComplete;
import art.ameliah.laby.addons.library.commands.core.models.annotations.AutoCompleteContainer;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Bounded;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Check;
import art.ameliah.laby.addons.library.commands.core.models.annotations.CheckContainer;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Command;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Greedy;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Named;
import art.ameliah.laby.addons.library.commands.core.models.annotations.NoCallback;
import art.ameliah.laby.addons.library.commands.core.models.exceptions.CommandException;
import art.ameliah.laby.addons.library.commands.core.models.types.CustomArgumentType;
import art.ameliah.laby.addons.library.commands.core.utils.DequeCollector;
import art.ameliah.laby.addons.library.commands.core.utils.Item;
import art.ameliah.laby.addons.library.commands.core.utils.Utils;
import art.ameliah.laby.addons.library.commands.v1_20_1.VersionedCommandService;
import art.ameliah.laby.addons.library.commands.v1_20_1.wrappers.McCommand;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.ChatExecutor;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.util.I18n;
import net.labymod.v1_20_1.client.network.chat.VersionedTextComponent;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandClassTransformer<T extends CommandClass<S>, S extends art.ameliah.laby.addons.library.commands.core.models.CommandContext> {

  private static final Logger logger = LoggerFactory.getLogger(CommandClassTransformer.class);

  private final ChatExecutor chatExecutor = Laby.labyAPI().minecraft().chatExecutor();

  private final T commandClass;

  private final HashMap<String, List<Method>> commandChecks = new HashMap<>();
  private final HashMap<String, Method> errorMethods = new HashMap<>();

  public CommandClassTransformer(T commandClass) throws CommandException {
    this.commandClass = commandClass;
    populateCheckMaps();
  }

  static @Nullable ArgumentType<?> argumentTypeFactory(@NotNull Parameter parameter)
      throws CommandException {
    Class<?> type = parameter.getType();
    if (Utils.typeIsInt(type)) {
      if (parameter.isAnnotationPresent(Bounded.class)) {
        Bounded annotation = parameter.getAnnotation(Bounded.class);
        return integer(annotation.min_int(), annotation.max_int());
      }
      return integer();
    }
    if (Utils.typeIsBool(type)) {
      return bool();
    }
    if (Utils.typeIsDouble(type)) {
      if (parameter.isAnnotationPresent(Bounded.class)) {
        Bounded annotation = parameter.getAnnotation(Bounded.class);
        return doubleArg(annotation.min_double(), annotation.max_double());
      }
      return doubleArg();
    }
    if (Utils.typeIsFloat(type)) {
      if (parameter.isAnnotationPresent(Bounded.class)) {
        Bounded annotation = parameter.getAnnotation(Bounded.class);
        return floatArg(annotation.min_float(), annotation.max_float());
      }
      return floatArg();
    }
    if (type.equals(String.class)) {
      return parameter.isAnnotationPresent(Greedy.class) ? greedyString() : word();
    }

    if (type.isArray()) {
      Class<?> componentType = type.getComponentType();
      if (componentType.isPrimitive()) {
        throw new CommandException(
            "Array argument cannot contain a primitive type: " + componentType.getName());
      }
      return ArrayTransformer.transform(VersionedCommandService.get(), componentType);
    }

    CustomArgumentType<?, ?> customArgumentType = VersionedCommandService.get()
        .getCustomArgument(type);
    if (customArgumentType != null) {
      return CustomArgumentTypeTransformer.transform(customArgumentType);
    }

    return null;
  }

  private void validateMethod(Method method) throws CommandException {
    Parameter[] parameters = method.getParameters();
    int size = parameters.length;
    if (size == 0) {
      throw new CommandException(
          I18n.translate("brigadier.exceptions.commands.wrongNumberOfArgs", method));
    }

    boolean hasPassedOptional = false;
    for (int i = 0; i < size; i++) {
      Parameter parameter = parameters[i];

      if (i == 0 && !parameter.getType()
          .equals(commandClass.getCommandContextClass())) {
        throw new CommandException(
            I18n.translate("brigadier.exceptions.commands.wrongFirstArg", method));
      }

      if (parameter.getType().equals(commandClass.getCommandContextClass())
          && i != 0) {
        throw new CommandException(
            I18n.translate("brigadier.exceptions.commands.contextWrongArg", method));
      }

      if (parameter.isAnnotationPresent(Greedy.class)) {
        if (i != size - 1) {
          throw new CommandException(
              I18n.translate("brigadier.exceptions.commands.greedyNotLast", method));
        }
        if (!parameter.getType().equals(String.class)) {
          // Skipping as wrong impl anyway
          // TODO: Greedy should repeat any, not capture all text
          throw new CommandException("Greedy can only be used on Strings. (%s)", method);
        }
      }

      if (parameter.isAnnotationPresent(Nullable.class)) {
        hasPassedOptional = true;
      } else if (hasPassedOptional) {
        throw new CommandException(
            I18n.translate("brigadier.exceptions.commands.optionalBeforeNone", method));
      }

      if (parameter.isAnnotationPresent(Bounded.class) &&
          !(Utils.typeIsFloat(parameter.getType()) ||
              Utils.typeIsDouble(parameter.getType()) ||
              Utils.typeIsInt(parameter.getType()))) {
        throw new CommandException(
            I18n.translate("brigadier.exceptions.commands.boundedOnWrongType", method));
      }
    }
  }

  public void populateCheckMaps() throws CommandException {
    for (Method method : commandClass.getClass().getMethods()) {
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }

      if (method.isAnnotationPresent(Check.class) || method.isAnnotationPresent(
          CheckContainer.class)) {
        List<Method> checks = new ArrayList<>();
        for (Check check : method.getAnnotationsByType(Check.class)) {
          try {
            Method checkMethod = commandClass.getClass()
                .getMethod(check.method(), commandClass.getCommandContextClass());
            if (!checkMethod.canAccess(commandClass)) {
              throw new CommandException(
                  I18n.translate("brigadier.exceptions.commands.checkMethodNotPublic",
                      check.method(), method.getName()));
            }
            checks.add(checkMethod);
          } catch (NoSuchMethodException e) {
            throw new CommandException(
                I18n.translate("brigadier.exceptions.commands.checkMethodNotExist",
                    check.method(), method.getName()));
          }

          String errorMethodName = check.failedMethod();
          if (!errorMethodName.equals("noPermissionComponent")) {
            try {
              Method errorMethod = commandClass.getClass().getMethod(errorMethodName);
              if (!errorMethod.canAccess(commandClass)) {
                throw new CommandException(
                    I18n.translate("brigadier.exceptions.commands.errorMethodNotPublic",
                        check.method(), method.getName()));
              }
              errorMethods.put(method.getName(), errorMethod);
            } catch (NoSuchMethodException e) {
              throw new CommandException(
                  I18n.translate("brigadier.exceptions.commands.errorMethodNotExist",
                      check.method(), method.getName()));
            }
          }

        }
        commandChecks.put(method.getName(), checks);
      }
    }
  }

  public @NotNull List<McCommand<S>> getCommands()
      throws CommandException {
    Objects.requireNonNull(commandClass, "commandClass");

    HashMap<String, McCommand<S>> commandNodes = new HashMap<>();

    for (Method method : commandClass.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }
      if (!Utils.typeIsBool(method.getReturnType())) {
        throw new CommandException(
            I18n.translate("brigadier.exceptions.commands.wrongReturnType", method));
      }
      if (!(method.canAccess(commandClass))) {
        throw new CommandException(
            I18n.translate("brigadier.exceptions.commands.commandMethodNotPublic", method));
      }
      commandNodes.put(method.getName(), createLiteralArgumentBuilder(method));
    }

    Map<String, Item<McCommand<S>>> processedCommands = new HashMap<>();
    for (Method method : commandClass.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }
      McCommand<S> cmd = commandNodes.get(method.getName());
      if (cmd == null) {
        throw new CommandException(
            I18n.translate("brigadier.exceptions.commands.commandNotFound", method));
      }

      Item<McCommand<S>> item = processedCommands.get(cmd.getLiteral());
      if (item == null) {
        item = new Item<>(cmd, null);
      }

      String parentName = method.getAnnotation(Command.class).parent();
      if (parentName.isEmpty()) {
        processedCommands.put(cmd.getLiteral(), item);
        continue;
      }

      McCommand<S> parent = commandNodes.get(parentName);
      if (parent == null) {
        throw new CommandException(
            I18n.translate("brigadier.exceptions.commands.parentNotFoundOrNotCommand",
                method.getName(), parentName));
      }

      Item<McCommand<S>> parentItem = processedCommands.get(parentName);
      if (parentItem == null) {
        parentItem = new Item<>(parent, null);
      }

      item.setParent(parentItem);
      processedCommands.put(cmd.getLiteral(), item);
      processedCommands.putIfAbsent(parentName, parentItem);
    }

    Map<Item<McCommand<S>>, List<Item<McCommand<S>>>> parentChildMap = new HashMap<>();
    for (Item<McCommand<S>> item : processedCommands.values()) {
      parentChildMap.putIfAbsent(item, new ArrayList<>());
      Item<McCommand<S>> parent = item.getParent();
      if (parent != null) {
        parentChildMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(item);
      }
    }

    Deque<Item<McCommand<S>>> stack =
        parentChildMap.keySet().stream()
            .filter(item -> parentChildMap.get(item).isEmpty())
            .collect(new DequeCollector<>());

    while (!stack.isEmpty()) {
      Item<McCommand<S>> currentItem = stack.pop();
      Item<McCommand<S>> parent = currentItem.getParent();
      if (parent != null) {
        parent.updateSelf(parent.getSelf().then(currentItem.getSelf()));
        parentChildMap.get(parent).remove(currentItem);
        if (parentChildMap.get(parent).isEmpty()) {
          stack.push(parent);
        }
      }

      parentChildMap.getOrDefault(parent, new ArrayList<>()).forEach(childItem -> {
        if (parentChildMap.get(childItem).isEmpty() && !stack.contains(childItem)) {
          stack.push(childItem);
        }
      });
    }

    return parentChildMap.keySet()
        .stream()
        .filter(Item::hasParent)
        .map(Item::getSelf)
        .toList();
  }

  private McCommand<S> createLiteralArgumentBuilder(
      @NotNull Method method) throws CommandException {
    validateMethod(method);

    McCommand<S> cmd = McCommand.literal(method.getName(), commandClass::shouldRegister,
        commandClass);
    HashMap<String, Method> autoCompleteMap = new HashMap<>();

    if (method.isAnnotationPresent(AutoComplete.class) || method.isAnnotationPresent(
        AutoCompleteContainer.class)) {
      for (AutoComplete autoComplete : method.getAnnotationsByType(AutoComplete.class)) {
        try {
          Method autoCompleteFunc = commandClass.getClass().getMethod(autoComplete.method(),
              commandClass.getCommandContextClass());
          if (!autoCompleteFunc.canAccess(commandClass)) {
            throw new CommandException(
                I18n.translate("brigadier.exceptions.commands.autoCompleteMethodNotPublic",
                    autoComplete.method(), method.getName()));
          }
          autoCompleteMap.put(autoComplete.parameterName(), autoCompleteFunc);
        } catch (NoSuchMethodException e) {
          throw new CommandException(
              I18n.translate("brigadier.exceptions.commands.autoCompleteNotExist",
                  autoComplete.method(), method.getName()));
        }
      }
    }

    Map<String, String> renamedArgs =
        Arrays.stream(method.getAnnotationsByType(Named.class))
            .collect(Collectors.toMap(Named::argument, Named::name));

    Function<String, String> renamer = (s) -> renamedArgs.getOrDefault(s, s);

    Parameter[] parameters = method.getParameters();
    int size = parameters.length;

    boolean canExec = !method.isAnnotationPresent(NoCallback.class);
    boolean encounteredNonOptional = false;
    Parameter parameter;
    ArgumentType<?> argumentType;
    ArgumentBuilder<SharedSuggestionProvider, ?> tail = null;

    if (size > 1) {
      for (int i = size - 1; i >= 1; i--) {
        parameter = parameters[i];
        String parameterName = renamer.apply(parameter.getName());
        argumentType = argumentTypeFactory(parameter);
        if (argumentType == null) {
          throw new CommandException(
              I18n.translate("brigadier.exceptions.commands.unsupportedArgumentType",
                  parameter.getName(), parameter.getType()));
        }

        RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredArgumentBuilder =
            RequiredArgumentBuilder.argument(parameterName, argumentType);

        Method func = autoCompleteMap.get(parameterName);
        if (func != null) {
          Parameter finalParameter = parameter;
          requiredArgumentBuilder.suggests((context, builder) ->
              getSuggestionsCompletableFuture(context, builder, finalParameter, func));
        }

        if (canExec) {
          requiredArgumentBuilder.executes(ctx -> commandEval(method, ctx, renamer));
        }

        tail = tail == null
            ? requiredArgumentBuilder
            : requiredArgumentBuilder.then(tail);

        if (!parameter.isAnnotationPresent(Nullable.class)) {
          encounteredNonOptional = true;
          canExec = false;
        } else {
          if (encounteredNonOptional) {
            throw new CommandException(
                I18n.translate("brigadier.exceptions.commands.optionalBeforeNone", method));
          }
        }
      }
    }
    cmd = tail == null
        ? cmd
        : cmd.then(tail);

    if (canExec) {
      cmd.executes(ctx -> commandEval(method, ctx, renamer));
    }
    return cmd;
  }

  private int commandEval(Method method, CommandContext<SharedSuggestionProvider> ctx,
      Function<String, String> renamer) {
    S versionedCtx = ContextTransformer.createCorrectCtx(ctx, null,
        commandClass.getCommandContextClass());

    if (!commandClass.classCheck(versionedCtx)) {
      chatExecutor.displayClientMessage(commandClass.noPermissionComponent());
      return 1;
    }

    List<Method> checks = commandChecks.get(method.getName());
    if (checks != null) {
      for (Method check : checks) {
        Object checkReturn;
        try {
          checkReturn = check.invoke(commandClass, versionedCtx);
        } catch (InvocationTargetException | IllegalAccessException e) {
          logger.warn(I18n.translate("brigadier.exceptions.commands.checkCannotInvoke", check), e);
          return 1;
        }
        if (!Utils.typeIsBool(checkReturn.getClass())) {
          logger.warn(I18n.translate("brigadier.exceptions.commands.checkNotBoolean", check));
          return 1;
        }
        if (!(Boolean) checkReturn) {
          Method errorComponent = errorMethods.get(method.getName());
          if (errorComponent == null) {
            chatExecutor.displayClientMessage(commandClass.noPermissionComponent());
            return 1;
          }

          Object comp;
          try {
            comp = errorComponent.invoke(commandClass);
          } catch (InvocationTargetException | IllegalAccessException e) {
            logger.warn(I18n.translate("brigadier.exceptions.commands.errorMethodCannotInvoke",
                errorComponent, check), e);
            return 1;
          }

          if (comp.getClass().equals(VersionedTextComponent.class)) {
            chatExecutor.displayClientMessage((VersionedTextComponent) comp);
            return 1;
          }
          logger.warn(
              I18n.translate("brigadier.exceptions.commands.errorMethodWrongType", errorComponent,
                  comp.getClass()));
          return 1;
        }
      }
    }

    List<Object> values = new ArrayList<>();
    values.add(versionedCtx);
    Arrays.stream(method.getParameters())
        .filter(par -> !par.getType().equals(commandClass.getCommandContextClass()))
        .forEach(par -> {
          String name = renamer.apply(par.getName());
          Object obj;
          Class<?> clazz = par.getType();
          if (VersionedCommandService.get().isCustomArgument(clazz)) {
            obj = constructCustomArgumentFromContext(clazz, ctx, name);
          } else if (clazz.isArray()) {
            Object[] array = getArgument(Object[].class, ctx, name);
            if (array == null) {
              obj = null;
            } else {
              obj = Arrays.stream(array)
                  .map(arrayMapper(clazz.getComponentType()))
                  .toArray(size -> (Object[]) Array.newInstance(clazz.getComponentType(), array.length));
            }
          } else {
            obj = getArgument(clazz, ctx, name);
          }
          values.add(obj);
        });
    Object re;
    try {
      re = method.invoke(commandClass, values.toArray());
    } catch (Exception e) {
      logger.error(I18n.translate("brigadier.exceptions.commands.commandCannotInvoke"), e);
      chatExecutor.displayClientMessage(
          Component.translatable("brigadier.exceptions.commands.defaultError",
              NamedTextColor.RED));
      return 1;
    }
    return Utils.typeIsBool(re.getClass()) ? (Boolean) re ? 1 : 0 : 0;
  }

  private CompletableFuture<Suggestions> getSuggestionsCompletableFuture(
      CommandContext<SharedSuggestionProvider> context, SuggestionsBuilder builder,
      Parameter parameter,
      Method func) {
    Object result;

    try {
      result = func.invoke(commandClass,
          ContextTransformer.createCorrectCtx(context, parameter,
              commandClass.getCommandContextClass()));
    } catch (Exception e) {
      logger.warn(I18n.translate("brigadier.exceptions.commands.suggestionCannotComplete"), e);
      return Suggestions.empty();
    }

    Object[] resultsArray = (Object[]) result;
    for (Object obj : resultsArray) {
      String s = (String) obj;
      builder.suggest(s);
    }
    return builder.buildFuture();
  }
}
