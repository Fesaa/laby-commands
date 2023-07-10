package art.ameliah.brigadier.v1_20_1;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

import art.ameliah.brigadier.core.CommandException;
import art.ameliah.brigadier.core.models.AutoComplete;
import art.ameliah.brigadier.core.models.AutoCompleteContainer;
import art.ameliah.brigadier.core.models.Bounded;
import art.ameliah.brigadier.core.models.Command;
import art.ameliah.brigadier.core.models.Greedy;
import art.ameliah.brigadier.core.models.NoCallback;
import art.ameliah.brigadier.core.models.Optional;
import art.ameliah.brigadier.core.utils.Utils;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public class Transformer {

  private static final ChatExecutor chatExecutor = Laby.labyAPI().minecraft().chatExecutor();

  private static void validateMethod(Method method) throws CommandException {
    Parameter[] parameters = method.getParameters();
    int size = parameters.length;
    boolean hasPassedOptional = false;
    for (int i = 0; i < size; i++) {
      Parameter parameter = parameters[i];

      if (i == 0 && !parameter.getType()
          .equals(art.ameliah.brigadier.core.models.CommandContext.class)) {
        throw new CommandException("First argument must be CommandContext");
      }

      if (parameter.getType().equals(art.ameliah.brigadier.core.models.CommandContext.class)
          && i != 0) {
        throw new CommandException("CommandContext must be the first argument");
      }

      if (parameter.isAnnotationPresent(Greedy.class)) {
        if (i != size - 1) {
          throw new CommandException("Greedy can only be used on the last argument.");
        }
        if (!parameter.getType().equals(String.class)) {
          throw new CommandException("Greedy can only be used on Strings.");
        }
      }

      if (parameter.isAnnotationPresent(Optional.class)) {
        hasPassedOptional = true;
      } else if (hasPassedOptional) {
        throw new CommandException("Not optional argument cannot come after an optional one.");
      }

      if (parameter.isAnnotationPresent(Bounded.class) &&
          !(Utils.typeIsFloat(parameter.getType()) ||
              Utils.typeIsDouble(parameter.getType()) ||
              Utils.typeIsInt(parameter.getType()))) {
        throw new CommandException("Bounded can only be used on Integers, Floats and Doubles.");
      }
    }
  }


  private static CompletableFuture<Suggestions> getSuggestionsCompletableFuture(
      CommandContext<CommandSourceStack> context, SuggestionsBuilder builder, Parameter parameter,
      Method func, Object commandClass) {
    Object result;

    try {
      result = func.invoke(commandClass, new VersionedCommandContext(context, parameter));
    } catch (Exception e) {
      e.printStackTrace();
      return Suggestions.empty();
    }

    Object[] resultsArray = (Object[]) result;
    for (Object obj : resultsArray) {
      String s = (String) obj;
      builder.suggest(s);
    }
    return builder.buildFuture();
  }

  private static int commandEval(Method method, Object commandClass,
      CommandContext<CommandSourceStack> ctx) {
    try {
      List<Object> values = new ArrayList<>();
      values.add(new VersionedCommandContext(ctx));
      Arrays.stream(method.getParameters())
          .filter(
              par -> !par.getType().equals(art.ameliah.brigadier.core.models.CommandContext.class))
          .forEach(par -> {
            try {
              Object obj = ctx.getArgument(par.getName(), par.getType());
              values.add(obj);
            } catch (IllegalArgumentException ignored) {
              values.add(null);
            }
          });

      Object re = values.size() == 0
          ? method.invoke(commandClass)
          : method.invoke(commandClass, values.toArray());

      return Utils.typeIsBool(re.getClass()) ? (Boolean) re ? 1 : 0 : 0;
    } catch (Exception e) {
      e.printStackTrace();
      chatExecutor.displayClientMessage("Error during command execution. Checks logs.");
      return 1;
    }
  }

  private static ArgumentType<?> argumentTypeFactory(Parameter parameter) {
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
    return null;
  }

  private static LiteralArgumentBuilder<CommandSourceStack> createLiteralArgumentBuilder(
      @NotNull Method method, Object commandClass) throws CommandException {
    validateMethod(method);

    LiteralArgumentBuilder<CommandSourceStack> cmd = LiteralArgumentBuilder.literal(
        method.getName());
    HashMap<String, Method> autoCompleteMap = new HashMap<>();

    if (method.isAnnotationPresent(AutoComplete.class) || method.isAnnotationPresent(
        AutoCompleteContainer.class)) {
      AutoComplete[] annotations = method.getAnnotationsByType(AutoComplete.class);
      for (AutoComplete autoComplete : annotations) {
        try {
          Method autoCompleteFunc = commandClass.getClass().getMethod(autoComplete.method(),
              art.ameliah.brigadier.core.models.CommandContext.class);
          if (!autoCompleteFunc.canAccess(commandClass)) {
            throw new CommandException("Autocomplete method must be public.");
          }
          autoCompleteMap.put(autoComplete.parameterName(), autoCompleteFunc);
        } catch (NoSuchMethodException e) {
          throw new CommandException("Autocomplete method does not exists");
        }
      }
    }

    Parameter[] parameters = method.getParameters();
    int size = parameters.length;

    boolean canExec = !method.isAnnotationPresent(NoCallback.class);
    boolean encounteredNonOptional = false;
    Parameter parameter;
    ArgumentType<?> argumentType;
    ArgumentBuilder<CommandSourceStack, ?> tail = null;

    if (size > 1) {
      for (int i = size - 1; i >= 1; i--) {
        parameter = parameters[i];
        argumentType = argumentTypeFactory(parameter);
        if (argumentType == null) {
          throw new CommandException(
              "Unsupported type for argument " + parameter.getName() + "with type "
                  + parameter.getType());
        }

        RequiredArgumentBuilder<CommandSourceStack, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(
            parameter.getName(), argumentType);

        Method func = autoCompleteMap.get(parameter.getName());
        if (func != null) {
          Parameter finalParameter = parameter;
          requiredArgumentBuilder.suggests(
              (context, builder) -> getSuggestionsCompletableFuture(context, builder,
                  finalParameter, func, commandClass));
        }

        if (canExec) {
          requiredArgumentBuilder.executes(ctx -> commandEval(method, commandClass, ctx));
        }

        tail = tail == null
            ? requiredArgumentBuilder
            : requiredArgumentBuilder.then(tail);

        if (!parameter.isAnnotationPresent(Optional.class)) {
          encounteredNonOptional = true;
          canExec = false;
        } else {
          if (encounteredNonOptional) {
            throw new CommandException("Cannot have an Optional before required argument");
          }
        }
      }
    }
    cmd = tail == null
        ? cmd
        : cmd.then(tail);

    if (canExec) {
      cmd.executes(ctx -> commandEval(method, commandClass, ctx));
    }
    return cmd;
  }

  public static @NotNull List<LiteralArgumentBuilder<CommandSourceStack>> transform(
      @NotNull Object commandClass) throws CommandException {
    Objects.requireNonNull(commandClass, "commandClass");

    HashMap<String, LiteralArgumentBuilder<CommandSourceStack>> commandNodes = new HashMap<>();

    for (Method method : commandClass.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }
      if (!Utils.typeIsBool(method.getReturnType())) {
        throw new CommandException("Command must return a boolean");
      }
      if (!(method.canAccess(commandClass))) {
        throw new CommandException("Method must be public");
      }
      commandNodes.put(method.getName(), createLiteralArgumentBuilder(method, commandClass));
    }

    Method[] methods = commandClass.getClass().getDeclaredMethods();
    List<String> bases = new ArrayList<>();

    System.out.println(Arrays.toString(methods));
    commandNodes.forEach((key, value) -> System.out.println(key + " " + value));
    for (int i = methods.length - 1; i >= 0; i--) {
      Method method = methods[i];
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }
      LiteralArgumentBuilder<CommandSourceStack> cmd = commandNodes.get(method.getName());
      if (cmd == null) {
        throw new CommandException("Found a non existing command? " + method);
      }
      //commandNodes.remove(method.getName());
      String parentName = method.getAnnotation(Command.class).parent();

      if (!parentName.equals("")) {
        LiteralArgumentBuilder<CommandSourceStack> parent = commandNodes.get(parentName);
        if (parent == null) {
          throw new CommandException(method.getName() + "'s parent " + parentName
              + " isn't present. Was it defined beforehand?");
        }
        parent.then(cmd);
      } else {
        bases.add(method.getName());
      }
    }

    return bases.stream().map(commandNodes::get).toList();
  }
}
