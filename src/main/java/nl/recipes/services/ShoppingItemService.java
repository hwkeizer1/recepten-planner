package nl.recipes.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.ShoppingItemRepository;

@Service
public class ShoppingItemService {

  private final ShoppingItemRepository shoppingItemRepository;

  private ObservableList<ShoppingItem> observableShoppingItemList;

  public ShoppingItemService(ShoppingItemRepository shoppingItemRepository) {
    this.shoppingItemRepository = shoppingItemRepository;
    observableShoppingItemList = FXCollections.observableList(shoppingItemRepository.findAll());
  }

  public ObservableList<ShoppingItem> getShoppingItemList() {
    return FXCollections.observableList(observableShoppingItemList);
  }

  public ShoppingItem create(ShoppingItem shoppingItem)
      throws AlreadyExistsException, IllegalValueException {
    if (shoppingItem == null || shoppingItem.getIngredientName() == null) {
      throw new IllegalValueException("Artikel naam mag niet leeg zijn");
    }
    if (findByName(shoppingItem.getIngredientName()).isPresent()) {
      throw new AlreadyExistsException(
          "Artikel " + shoppingItem.getIngredientName().getName() + " bestaat al");
    }
    shoppingItem.setStandard(true);
    shoppingItem.setShopType(shoppingItem.getIngredientName().getShopType());
    shoppingItem.setIngredientType(shoppingItem.getIngredientName().getIngredientType());
    ShoppingItem createdShoppingItem = shoppingItemRepository.save(shoppingItem);
    observableShoppingItemList.add(createdShoppingItem);
    return createdShoppingItem;
  }

  public ShoppingItem update(ShoppingItem shoppingItem, ShoppingItem update)
      throws NotFoundException, AlreadyExistsException {
    if (!findById(shoppingItem.getId()).isPresent()) {
      throw new NotFoundException(
          "Artikel " + shoppingItem.getIngredientName().getName() + " niet gevonden");
    }
    if (!shoppingItem.getIngredientName().equals(update.getIngredientName())
        && findByName(update.getIngredientName()).isPresent()) {
      throw new AlreadyExistsException(
          "Artikel " + update.getIngredientName().getName() + " bestaat al");
    }
    shoppingItem.setIngredientName(update.getIngredientName());

    ShoppingItem updatedShoppingItem = shoppingItemRepository.save(shoppingItem);
    observableShoppingItemList.set(observableShoppingItemList.lastIndexOf(shoppingItem),
        updatedShoppingItem);
    return updatedShoppingItem;
  }

  public void remove(ShoppingItem shoppingItem) throws NotFoundException {
    // TODO add check for removing shoppingItems that are in use
    if (!findById(shoppingItem.getId()).isPresent()) {
      throw new NotFoundException(
          "Artikel " + shoppingItem.getIngredientName().getName() + " niet gevonden");
    }
    shoppingItemRepository.delete(shoppingItem);
    observableShoppingItemList.remove(shoppingItem);
  }

  public Optional<ShoppingItem> findByName(IngredientName ingredientName) {
    return observableShoppingItemList.stream()
        .filter(shoppingItem -> ingredientName.equals(shoppingItem.getIngredientName())).findAny();
  }

  public Optional<ShoppingItem> findById(Long id) {
    return observableShoppingItemList.stream()
        .filter(shoppingItem -> id.equals(shoppingItem.getId())).findAny();
  }

  public void addListener(ListChangeListener<ShoppingItem> listener) {
    observableShoppingItemList.addListener(listener);
  }

  public void removeChangeListener(ListChangeListener<ShoppingItem> listener) {
    observableShoppingItemList.removeListener(listener);
  }

  // Setter for JUnit testing only
  void setObservableShoppingItemList(ObservableList<ShoppingItem> observableShoppingItemList) {
    this.observableShoppingItemList = observableShoppingItemList;
  }

}
