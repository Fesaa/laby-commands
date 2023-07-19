package art.ameliah.laby.addons.library.commands.core.utils;

public class Item<T> {

  protected Item<T> parent;

  protected T self;

  public Item(T self, Item<T> parent) {
    this.self = self;
    this.parent = parent;
  }

  public boolean hasParent() {
    return this.parent == null;
  }

  public Item<T> getParent() {
    return parent;
  }

  public void setParent(Item<T> parent) {
    this.parent = parent;
  }

  public void updateSelf(T self) {
    this.self = self;
  }

  public T getSelf() {
    return self;
  }

}
