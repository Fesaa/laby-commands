package art.ameliah.brigadier.core.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class DequeCollector<T> implements Collector<T, Deque<T>, Deque<T>> {

  @Override
  public Supplier<Deque<T>> supplier() {
    return ArrayDeque::new;
  }

  @Override
  public BiConsumer<Deque<T>, T> accumulator() {
    return Deque::add;
  }

  @Override
  public BinaryOperator<Deque<T>> combiner() {
    return (deque1, deque2) -> {
      deque1.addAll(deque2);
      return deque1;
    };
  }

  @Override
  public Function<Deque<T>, Deque<T>> finisher() {
    return Function.identity();
  }

  @Override
  public Set<Characteristics> characteristics() {
    return EnumSet.of(Characteristics.IDENTITY_FINISH);
  }
}
