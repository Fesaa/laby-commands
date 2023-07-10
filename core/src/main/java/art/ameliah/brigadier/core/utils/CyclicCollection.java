package art.ameliah.brigadier.core.utils;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CyclicCollection<T> implements Collection<T> {

  private final List<T> inner = new ArrayList<>();

  @SafeVarargs
  public static <T> CyclicCollection<T> of(T... elements) {
    CyclicCollection<T> collection = new CyclicCollection<T>();
    collection.addAll(List.of(elements));
    return collection;
  }

  @Override
  public int size() {
    return this.inner.size();
  }

  @Override
  public boolean isEmpty() {
    return this.inner.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return this.inner.contains(o);
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    List<T> inner = this.inner;
    return new Iterator<>() {

      @Override
      public boolean hasNext() {
        return inner.size() != 0;
      }

      @Override
      public T next() {
        T next = inner.get(0);
        inner.remove(0);
        inner.add(next);
        return next;
      }
    };
  }

  @NotNull
  @Override
  public Object @NotNull [] toArray() {
    return new Object[0];
  }

  @Override
  public boolean add(T o) {
    return this.inner.add(o);
  }

  @Override
  public boolean remove(Object o) {
    return this.inner.remove(o);
  }

  @Override
  public boolean addAll(@NotNull Collection collection) {
    return this.inner.addAll(collection);
  }

  @Override
  public void clear() {
    this.inner.clear();
  }

  @Override
  public boolean retainAll(@NotNull Collection collection) {
    return this.inner.retainAll(collection);
  }

  @Override
  public boolean removeAll(@NotNull Collection collection) {
    return this.inner.removeAll(collection);
  }

  @Override
  public boolean containsAll(@NotNull Collection collection) {
    return new HashSet<>(this.inner).containsAll(collection);
  }

  @NotNull
  @Override
  public Object @NotNull [] toArray(@NotNull Object @NotNull [] objects) {
    return this.inner.toArray();
  }
}
