package art.ameliah.brigadier.core.utils;

public interface Item<T> {

  boolean hasParent();

  Item<T> getParent();

  void setParent(Item<T> parent);

  void updateSelf(T item);

  T getSelf();

}
