package art.ameliah.brigadier.v1_20_1.transformers;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

import art.ameliah.brigadier.core.models.CommandClass;
import art.ameliah.brigadier.core.models.annotations.AutoComplete;
import art.ameliah.brigadier.core.models.annotations.AutoCompleteContainer;
import art.ameliah.brigadier.core.models.annotations.Bounded;
import art.ameliah.brigadier.core.models.annotations.Check;
import art.ameliah.brigadier.core.models.annotations.CheckContainer;
import art.ameliah.brigadier.core.models.annotations.Command;
import art.ameliah.brigadier.core.models.annotations.Greedy;
import art.ameliah.brigadier.core.models.annotations.NoCallback;
import art.ameliah.brigadier.core.models.annotations.Optional;
import art.ameliah.brigadier.core.models.custumTypes.CustomArgumentType;
import art.ameliah.brigadier.core.models.exceptions.CommandException;
import art.ameliah.brigadier.core.utils.Utils;
import art.ameliah.brigadier.v1_20_1.VersionedCommandContext;
import art.ameliah.brigadier.v1_20_1.VersionedCommandService;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.ChatExecutor;
import net.labymod.v1_20_1.client.network.chat.VersionedTextComponent;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;


public class CommandClassTransformer<T extends CommandClass> {

  private final ChatExecutor chatExecutor = Laby.labyAPI().minecraft().chatExecutor();

  private final T commandClass;

  private final HashMap<String, List<Method>> commandChecks = new HashMap<>();
  private final HashMap<String, Method> errorMethods = new HashMap<>();

  public CommandClassTransformer(T commandClass) throws CommandException {
    this.commandClass = commandClass;
    this.populateCheckMaps();
  }

  static void validateMethod(Method method) throws CommandException {
    Parameter[] parameters = method.getParameters();
    int size = parameters.length;
    if (size == 0) {
      throw new CommandException(
          "Commands must include, and start with, an argument with type CommandContext. (%s)",
          method);
    }

    boolean hasPassedOptional = false;
    for (int i = 0; i < size; i++) {
      Parameter parameter = parameters[i];

      if (i == 0 && !parameter.getType()
          .equals(art.ameliah.brigadier.core.models.CommandContext.class)) {
        throw new CommandException("First argument must be CommandContext. (%s)", method);
      }

      if (parameter.getType().equals(art.ameliah.brigadier.core.models.CommandContext.class)
          && i != 0) {
        throw new CommandException("CommandContext can only be used as the first argument. (%s)",
            method);
      }

      if (parameter.isAnnotationPresent(Greedy.class)) {
        if (i != size - 1) {
          throw new CommandException("Greedy can only be used on the last argument. (%s)", method);
        }
        if (!parameter.getType().equals(String.class)) {
          throw new CommandException("Greedy can only be used on Strings. (%s)", method);
        }
      }

      if (parameter.isAnnotationPresent(Optional.class)) {
        hasPassedOptional = true;
      } else if (hasPassedOptional) {
        throw new CommandException(
            "Optional arguments can only be succeeded by optional arguments. (%s)", method);
      }

      if (parameter.isAnnotationPresent(Bounded.class) &&
          !(Utils.typeIsFloat(parameter.getType()) ||
              Utils.typeIsDouble(parameter.getType()) ||
              Utils.typeIsInt(parameter.getType()))) {
        throw new CommandException("Bounded can only be used on Integers, Floats and Doubles. (%s)",
            method);
      }
    }
  }

  static ArgumentType<?> argumentTypeFactory(Parameter parameter) throws CommandException {
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

    CustomArgumentType<?> customArgumentType = VersionedCommandService.get()
        .getCustomArgument(type);
    if (customArgumentType != null) {
      return CustomArgumentTypeTransformer.transform(customArgumentType);
    }

    return null;
  }

  public void populateCheckMaps() throws CommandException {
    for (Method method : this.commandClass.getClass().getMethods()) {
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }

      if (method.isAnnotationPresent(Check.class) || method.isAnnotationPresent(
          CheckContainer.class)) {
        List<Method> checks = new ArrayList<>();
        for (Check check : method.getAnnotationsByType(Check.class)) {
          try {
            Method checkMethod = this.commandClass.getClass()
                .getMethod(check.method(), art.ameliah.brigadier.core.models.CommandContext.class);
            if (!checkMethod.canAccess(this.commandClass)) {
              throw new CommandException("CheckMethod method (%s) must be public for %s.",
                  check.method(), method.getName());
            }
            checks.add(checkMethod);
          } catch (NoSuchMethodException e) {
            throw new CommandException("CheckMethod method (%s) does not exists for %s.",
                check.method(), method.getName());
          }

          String errorMethodName = check.errorMethod();
          if (!errorMethodName.equals("noPermissionComponent")) {
            try {
              Method errorMethod = this.commandClass.getClass().getMethod(errorMethodName);
              if (!errorMethod.canAccess(this.commandClass)) {
                throw new CommandException("ErrorMethod method (%s) must be public for %s.",
                    check.method(), method.getName());
              }
              this.errorMethods.put(method.getName(), errorMethod);
            } catch (NoSuchMethodException e) {
              throw new CommandException("ErrorMethod method (%s) does not exists for %s.",
                  check.method(), method.getName());
            }
          }

        }
        this.commandChecks.put(method.getName(), checks);
      }
    }
  }

  public @NotNull List<LiteralArgumentBuilder<SharedSuggestionProvider>> getCommands()
      throws CommandException {
    Objects.requireNonNull(commandClass, "commandClass");

    HashMap<String, LiteralArgumentBuilder<SharedSuggestionProvider>> commandNodes = new HashMap<>();

    for (Method method : commandClass.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }
      if (!Utils.typeIsBool(method.getReturnType())) {
        throw new CommandException("Command must return a boolean. (%s)", method);
      }
      if (!(method.canAccess(commandClass))) {
        throw new CommandException("Method must be public. (%s)", method);
      }
      commandNodes.put(method.getName(), this.createLiteralArgumentBuilder(method));
    }
    List<String> bases = new ArrayList<>();

    for (Method method : commandClass.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }
      LiteralArgumentBuilder<SharedSuggestionProvider> cmd = commandNodes.get(method.getName());
      if (cmd == null) {
        throw new CommandException("Found a non existing command? (%s)", method);
      }

      String parentName = method.getAnnotation(Command.class).parent();
      if (!parentName.equals("")) {
        LiteralArgumentBuilder<SharedSuggestionProvider> parent = commandNodes.get(parentName);
        if (parent == null) {
          throw new CommandException(
              "%s's parent %s doesn't exist or isn't annotated with @Command.", method.getName(),
              parentName);
        }
        parent.then(cmd);
      } else {
        bases.add(method.getName());
      }
    }

    return bases.stream().map(commandNodes::get).toList();
  }

  private LiteralArgumentBuilder<SharedSuggestionProvider> createLiteralArgumentBuilder(
      @NotNull Method method) throws CommandException {
    validateMethod(method);

    LiteralArgumentBuilder<SharedSuggestionProvider> cmd = LiteralArgumentBuilder.literal(
        method.getName());
    HashMap<String, Method> autoCompleteMap = new HashMap<>();

    if (method.isAnnotationPresent(AutoComplete.class) || method.isAnnotationPresent(
        AutoCompleteContainer.class)) {
      for (AutoComplete autoComplete : method.getAnnotationsByType(AutoComplete.class)) {
        try {
          Method autoCompleteFunc = this.commandClass.getClass().getMethod(autoComplete.method(),
              art.ameliah.brigadier.core.models.CommandContext.class);
          if (!autoCompleteFunc.canAccess(this.commandClass)) {
            throw new CommandException("Autocomplete method (%s) must be public for %s.",
                autoComplete.method(), method.getName());
          }
          autoCompleteMap.put(autoComplete.parameterName(), autoCompleteFunc);
        } catch (NoSuchMethodException e) {
          throw new CommandException("Autocomplete method (%s) does not exists for %s.",
              autoComplete.method(), method.getName());
        }
      }
    }

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
        argumentType = argumentTypeFactory(parameter);
        if (argumentType == null) {
          throw new CommandException("Unsupported type for argument (%s) with type %s",
              parameter.getName(), parameter.getType());
        }

        RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(
            parameter.getName(), argumentType);

        Method func = autoCompleteMap.get(parameter.getName());
        if (func != null) {
          Parameter finalParameter = parameter;
          requiredArgumentBuilder.suggests(
              (context, builder) -> this.getSuggestionsCompletableFuture(context, builder,
                  finalParameter, func));
        }

        if (canExec) {
          requiredArgumentBuilder.executes(ctx -> commandEval(method, ctx));
        }

        tail = tail == null
            ? requiredArgumentBuilder
            : requiredArgumentBuilder.then(tail);

        if (!parameter.isAnnotationPresent(Optional.class)) {
          encounteredNonOptional = true;
          canExec = false;
        } else {
          if (encounteredNonOptional) {
            throw new CommandException(
                "Optional arguments can only be succeeded by optional arguments. (%s)", method);
          }
        }
      }
    }
    cmd = tail == null
        ? cmd
        : cmd.then(tail);

    if (canExec) {
      cmd.executes(ctx -> commandEval(method, ctx));
    }
    return cmd;
  }

  private int commandEval(Method method, CommandContext<SharedSuggestionProvider> ctx) {
    VersionedCommandContext versionedCtx = new VersionedCommandContext(ctx);

    if (!this.commandClass.classCheck(versionedCtx)) {
      this.chatExecutor.displayClientMessage(this.commandClass.noPermissionComponent());
      return 1;
    }

    List<Method> checks = this.commandChecks.get(method.getName());
    if (checks != null) {
      for (Method check : checks) {
        try {
          Object re = check.invoke(this.commandClass, versionedCtx);
          if (!Utils.typeIsBool(re.getClass())) {
            return 0;
          }
          if (!(Boolean) re) {
            Method errorComponent = this.errorMethods.get(method.getName());
            if (errorComponent == null) {
              this.chatExecutor.displayClientMessage(this.commandClass.noPermissionComponent());
              return 1;
            }

            Object comp = errorComponent.invoke(this.commandClass);

            if (comp.getClass().equals(VersionedTextComponent.class)) {
              this.chatExecutor.displayClientMessage((VersionedTextComponent) comp);
              return 1;
            }
            return 0;
          }
        } catch (Exception e) {
          e.printStackTrace();
          this.chatExecutor.displayClientMessage(this.commandClass.errorComponent());
          return 1;
        }
      }
    }

    List<Object> values = new ArrayList<>();
    values.add(versionedCtx);
    Arrays.stream(method.getParameters())
        .filter(
            par -> !par.getType().equals(art.ameliah.brigadier.core.models.CommandContext.class))
        .forEach(par -> {
          try {

            Object obj;
            Class<?> clazz = par.getType();
            if (VersionedCommandService.get().isCustomArgument(clazz)) {
              Method classGetter = clazz.getMethod("getClassType");
              Object returnValue = classGetter.invoke(clazz);
              Class<?> returnClass = (Class<?>) returnValue;
              Constructor<?> constructor = clazz.getConstructor(returnClass);
              obj = constructor.newInstance(ctx.getArgument(par.getName(), returnClass));
            } else {
              obj = ctx.getArgument(par.getName(), par.getType());
            }
            values.add(obj);
          } catch (IllegalArgumentException | InstantiationException ignored) {
            values.add(null);
          } catch (NoSuchMethodException | InvocationTargetException |
                   IllegalAccessException ignored) {
          }
        });
    Object re;
    try {
      re = method.invoke(commandClass, values.toArray());
    } catch (Exception e) {
      e.printStackTrace();
      chatExecutor.displayClientMessage("Error during command execution. Checks logs.");
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
      result = func.invoke(this.commandClass, new VersionedCommandContext(context, parameter));
    } catch (Exception e) {
      System.out.println("Suggestions couldn't complete. Returning empty;" + Utils.stackTraceToString(e.getStackTrace()));
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
