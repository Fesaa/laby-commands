package art.ameliah.brigadier.v1_20_1;

import art.ameliah.brigadier.core.CommandException;
import art.ameliah.brigadier.core.models.AutoComplete;
import art.ameliah.brigadier.core.models.Bounded;
import art.ameliah.brigadier.core.models.Command;
import art.ameliah.brigadier.core.models.CommandGroup;
import art.ameliah.brigadier.core.models.Greedy;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.ChatExecutor;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.Sys;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class Transformer {

  private static final ChatExecutor chatExecutor = Laby.labyAPI().minecraft().chatExecutor();


  private static CompletableFuture<Suggestions> getSuggestionsCompletableFuture(
      CommandContext context, SuggestionsBuilder builder, Parameter last, Method func, Object commandClass) {
    System.out.println("RETURNING AUTO COMPLETE");
    Object current = context.getArgument(last.getName(), last.getType());
    Object result;

    try {
      result = func.invoke(commandClass, current);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    Object[] resultsArray = (Object[]) result;
    List<Suggestion> suggestions = new ArrayList<>();
    int max = 0;
    for (Object obj : resultsArray) {
      String s = (String) obj;
      builder.suggest(s);
    }
    return builder.buildFuture();
  }

  private static ArgumentType argumentTypeFactory(Parameter parameter) {
    Class<?> type = parameter.getType();
    if (type.equals(int.class) || type.equals(Integer.class)) {
      if (parameter.isAnnotationPresent(Bounded.class)) {
        Bounded annotation = parameter.getAnnotation(Bounded.class);
        return integer(annotation.min(), annotation.max());
      }
      return integer();
    }
    if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      return bool();
    }
    if (type.equals(double.class) || type.equals(Double.class)) {
      return doubleArg();
    }
    if (type.equals(float.class) || type.equals(Float.class)) {
      return floatArg();
    }
    if (type.equals(String.class)) {
      return parameter.isAnnotationPresent(Greedy.class) ? greedyString() : word();
    }
    return null;
  }

  private static LiteralArgumentBuilder<CommandSourceStack>
  createLiteralArgumentBuilder(@NotNull Method method, Object commandClass) throws CommandException {
    LiteralArgumentBuilder<CommandSourceStack> cmd = LiteralArgumentBuilder.<CommandSourceStack>literal(method.getName());

    HashMap<String, Method> parameterMethodMap = new HashMap<>();

    if (method.isAnnotationPresent(AutoComplete.class)) {
      AutoComplete[] annotations = method.getAnnotationsByType(AutoComplete.class);
      for (AutoComplete autoComplete : annotations) {
        Method autoCompleteFunc;
        try {
          autoCompleteFunc = commandClass.getClass().getMethod(autoComplete.method(), String.class);
          if (!autoCompleteFunc.canAccess(commandClass)) {
            throw new CommandException("Autocomplete method must be public.");
          }
        } catch (NoSuchMethodException e) {
          throw new CommandException("Autocomplete points to not existing method.");
        }
        parameterMethodMap.put(autoComplete.parameterName(), autoCompleteFunc);
      }
    }

    Parameter[] parameters = method.getParameters();
    int size = parameters.length;

    Arrays.stream(parameters).forEach(e -> System.out.println(e.getName()));

    if (size != 0){
      Parameter last = parameters[size -1];
      ArgumentType argumentType = argumentTypeFactory(last);
      if (argumentType == null) {
        throw new CommandException("Unsupported type for argument: " + last.getType());
      }

      ArgumentBuilder argumentBuilder;

      RequiredArgumentBuilder requiredArgumentBuilder = RequiredArgumentBuilder.argument(last.getName(), argumentType);

      Method func = parameterMethodMap.get(last.getName());
      System.out.println("FUNC FOR " + last.getName() + " IS " + func);
      if (func != null) {
        requiredArgumentBuilder = requiredArgumentBuilder.suggests((context, builder) ->
            getSuggestionsCompletableFuture(context, builder, last, func, commandClass)
        );
      }
      argumentBuilder = requiredArgumentBuilder;

      argumentBuilder = argumentBuilder.executes(ctx -> {
        try {
          List<Object> params = new ArrayList<>();

          for (Parameter parameter : method.getParameters()) {
            Object paramRe = ctx.getArgument(parameter.getName(), parameter.getType());
            params.add(paramRe);
          }

          Object re = params.size() == 0
              ? method.invoke(commandClass)
              : method.invoke(commandClass, params.toArray());

          if (re.getClass() == Boolean.class) {
            return (Boolean) re ? 1 : 0;
          }
          return 0;
        } catch (Exception e) {
          e.printStackTrace();
          chatExecutor.displayClientMessage("Error during command execution. Checks logs.");
          return 1;
        }
      });

      if (size > 1) {
        for (int i = size - 2; i >= 0; i--) {
          Parameter current = parameters[i];
          ArgumentType currentArgumentType = argumentTypeFactory(current);
          if (currentArgumentType == null) {
            throw new CommandException("Unsupported type for argument: " + current.getType());
          }

          RequiredArgumentBuilder currentRequiredArgumentBuilder = RequiredArgumentBuilder
              .argument(current.getName(), currentArgumentType);
          Method currentFunc = parameterMethodMap.get(current.getName());
          System.out.println("FUNC FOR " + current.getName() + " IS " + currentFunc);
          if (currentFunc != null) {
            requiredArgumentBuilder = requiredArgumentBuilder.suggests((context, builder) ->
                getSuggestionsCompletableFuture(context, builder, current, currentFunc, commandClass)
            );
          }

          argumentBuilder = currentRequiredArgumentBuilder.then(argumentBuilder);
        }
      }

      cmd = cmd.then(argumentBuilder);
      } else {
      cmd = cmd.executes(ctx -> {
        try {
          Object re = method.invoke(commandClass);
          if (re.getClass() == Boolean.class) {
            return (Boolean) re ? 1 : 0;
          }
          return 0;
        } catch (Exception e) {
          e.printStackTrace();
          chatExecutor.displayClientMessage("Error during command execution. Checks logs.");
          return 1;
        }
      });
    }

    return cmd;
  }

  public static List<LiteralArgumentBuilder<CommandSourceStack>>
    transform(@NotNull Object commandClass) throws CommandException {
    Objects.requireNonNull(commandClass, "commandClass");

    List<LiteralArgumentBuilder<CommandSourceStack>> commands = new ArrayList<>();
    HashMap<String, LiteralArgumentBuilder<CommandSourceStack>> commandMap = new HashMap<>();

    for (Method method : commandClass.getClass().getMethods()) {
      if (!method.isAnnotationPresent(CommandGroup.class)) {
        continue;
      }
      if (!(method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class )) {
        throw new CommandException("Command must return a boolean");
      }
      if (!(method.canAccess(commandClass))) {
        throw new CommandException("Method must be public");
      }
      commandMap.put(method.getName(), createLiteralArgumentBuilder(method, commandClass));
    }

    for (Method method : commandClass.getClass().getMethods()) {
      if (!method.isAnnotationPresent(Command.class)) {
        continue;
      }
      if (!(method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class )) {
        throw new CommandException("Command must return a boolean");
      }
      if (!(method.canAccess(commandClass))) {
        throw new CommandException("Method must be public");
      }
      LiteralArgumentBuilder<CommandSourceStack> cmd = createLiteralArgumentBuilder(method, commandClass);

      Command annotation = method.getAnnotation(Command.class);
      String parentName = annotation.parent();

      if (parentName.equals("")) {
        commands.add(cmd);
      } else {
        LiteralArgumentBuilder<CommandSourceStack> parent = commandMap.get(parentName);
        if (parent == null) {
          throw new CommandException("Command parent does not exists.");
        }
        parent.then(cmd);
      }
    }

    commands.addAll(commandMap.values());
    return commands;
  }
}
