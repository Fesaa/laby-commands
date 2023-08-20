package art.ameliah.laby.addons.library.commands.v1_20_1.transformers;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

import art.ameliah.laby.addons.library.commands.core.models.types.CustomArgumentType;
import art.ameliah.laby.addons.library.commands.core.service.CommandService;
import art.ameliah.laby.addons.library.commands.core.utils.Utils;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class ArrayTransformer {

  private static final Logger logger = LoggerFactory.getLogger(CommandClassTransformer.class);

  static <T, U extends art.ameliah.laby.addons.library.commands.core.models.CommandContext> ArgumentType<T[]>
  transform(CommandService<U> service, Class<T> arrayType) {
    ArgumentType<?> inner;
    if (Utils.typeIsBool(arrayType)) {
      inner = bool();
    } else if (Utils.typeIsInt(arrayType)) {
      inner = integer();
    } else if (Utils.typeIsDouble(arrayType)) {
      inner = doubleArg();
    } else if (Utils.typeIsFloat(arrayType)) {
      inner = floatArg();
    } else {
      CustomArgumentType<?, ?> customArgumentType = service.getCustomArgument(arrayType);
      if (customArgumentType == null) {
        return null;
      }
      inner = CustomArgumentTypeTransformer.transform(customArgumentType);
    }

    ArgumentType<?> finalInner = inner;
    return new ArgumentType<>() {
      final List<T> list = new ArrayList<>();
      final ArgumentType<?> inner = finalInner;
      @Override
      public T[] parse(StringReader reader) throws CommandSyntaxException {
        list.clear();

        String s = reader.getRemaining();
        int start = reader.getCursor();
        int cursor = reader.getCursor();

        String[] parts = s.split(" ");

        for (String part: parts) {
          T t;
          try {
            t = (T) inner.parse(new StringReader(part));
          } catch (CommandSyntaxException e) {
            if (start == cursor) {
              throw e;
            }
            break;
          }
          cursor += part.length() + 1;
          list.add(t);
        }
        reader.setCursor(cursor - 1);
        return (T[]) list.toArray();
      }
    };
  }

}
