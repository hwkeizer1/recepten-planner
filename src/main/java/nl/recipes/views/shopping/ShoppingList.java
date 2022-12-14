package nl.recipes.views.shopping;

import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import nl.recipes.domain.ShoppingItem;

public abstract class ShoppingList {

  protected ObservableList<ShoppingItem> observableList;
  protected Comparator<ShoppingItem> comparator;

  protected abstract Node view();

  public ObservableList<ShoppingItem> getList() {
    return observableList;
  }

  public Comparator<ShoppingItem> getComparator() {
    return comparator;
  }

  public void setComparator(Comparator<ShoppingItem> comparator) {
    this.comparator = comparator;
  }

  public void addListener(ListChangeListener<ShoppingItem> listener) {
    observableList.addListener(listener);
  }

  public void removeChangeListener(ListChangeListener<ShoppingItem> listener) {
    observableList.removeListener(listener);
  }

  ShoppingItem save(ShoppingItem item) {
    observableList.add(item);
    if (comparator != null) {
      FXCollections.sort(observableList, comparator);
    }
    return item;
  }

  ShoppingItem update(ShoppingItem oldItem, ShoppingItem newItem) {
    observableList.set(observableList.lastIndexOf(oldItem), newItem);
    if (comparator != null) {
      FXCollections.sort(observableList, comparator);
    }
    return newItem;
  }

  void delete(ShoppingItem item) {
    observableList.remove(item);
  }
}
