package nl.recipes.services;

import static nl.recipes.views.ViewMessages.SHOPPING_ITEM_NAME_;
import static nl.recipes.views.ViewMessages.SHOPPING_ITEM_NAME_CANNOT_BE_EMPTY;
import static nl.recipes.views.ViewMessages._ALREADY_EXISTS;
import static nl.recipes.views.ViewMessages._NOT_FOUND;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.ShoppingItemRepository;

@Service
public class StandardShoppingItemService extends ListService<ShoppingItem> {

  public StandardShoppingItemService(ShoppingItemRepository shoppingItemRepository) {
    repository = shoppingItemRepository;
    Sort sort = Sort.by("name").ascending();
    observableList = FXCollections.observableList(repository.findAll(sort).stream()
        .filter(ShoppingItem::isStandard).collect(Collectors.toList()));
    comparator = (t1, t2) -> t1.getName().compareTo(t2.getName());
  }

  public Optional<ShoppingItem> findByName(String name) {
    return observableList.stream().filter(shoppingItem -> name.equals(shoppingItem.getName()))
        .findAny();
  }

  public Optional<ShoppingItem> findById(Long id) {
    return observableList.stream().filter(shoppingItem -> id.equals(shoppingItem.getId()))
        .findAny();
  }

  public ShoppingItem create(ShoppingItem shoppingItem)
      throws AlreadyExistsException, IllegalValueException {
    if (shoppingItem == null || shoppingItem.getName() == null) {
      throw new IllegalValueException(SHOPPING_ITEM_NAME_CANNOT_BE_EMPTY);
    }
    if (findByName(shoppingItem.getName()).isPresent()) {
      throw new AlreadyExistsException(SHOPPING_ITEM_NAME_ + shoppingItem.getName() + _ALREADY_EXISTS);
    }

    shoppingItem.setStandard(true);
    return save(shoppingItem);
  }

  public ShoppingItem edit(ShoppingItem shoppingItem, ShoppingItem update)
      throws NotFoundException, AlreadyExistsException {
    if (!findById(shoppingItem.getId()).isPresent()) {
      throw new NotFoundException(SHOPPING_ITEM_NAME_ + shoppingItem.getName() + _NOT_FOUND);
    }
    if (!shoppingItem.getName().equals(update.getName())
        && findByName(update.getName()).isPresent()) {
      throw new AlreadyExistsException(SHOPPING_ITEM_NAME_ + update.getName() + _ALREADY_EXISTS);
    }

    update.setId(shoppingItem.getId());
    return update(update);
  }

  public void remove(ShoppingItem shoppingItem) throws NotFoundException {
    if (!findById(shoppingItem.getId()).isPresent()) {
      throw new NotFoundException(SHOPPING_ITEM_NAME_ + shoppingItem.getName() + _NOT_FOUND);
    }
    delete(shoppingItem);
  }

  public boolean isShoppingItemName(String name) {
    return observableList.stream()
        .anyMatch(i -> name.equals(i.getName()) || name.equals(i.getPluralName()));
  }

  /**
   * Setter for JUnit testing only!
   * 
   * @param observableList
   */
  void setObservableList(ObservableList<ShoppingItem> observableList) {
    this.observableList = observableList;
  }
}
