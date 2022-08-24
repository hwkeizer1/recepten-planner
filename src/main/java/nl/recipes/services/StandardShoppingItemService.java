package nl.recipes.services;

import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.ShoppingItemRepository;

@Service
public class StandardShoppingItemService {

  private final ShoppingItemRepository shoppingItemRepository;

  private ObservableList<ShoppingItem> standardShoppingItemList;

  public StandardShoppingItemService(ShoppingItemRepository shoppingItemRepository) {
    this.shoppingItemRepository = shoppingItemRepository;
    standardShoppingItemList = getShoppingItemList();
  }

  public ObservableList<ShoppingItem> getReadonlyShoppingItemList() {
    return FXCollections.unmodifiableObservableList(standardShoppingItemList);
  }

  public ShoppingItem create(ShoppingItem shoppingItem)
      throws AlreadyExistsException, IllegalValueException {
    if (shoppingItem == null || shoppingItem.getName() == null) {
      throw new IllegalValueException("Naam mag niet leeg zijn");
    }
    if (findByName(shoppingItem.getName()).isPresent()) {
      throw new AlreadyExistsException(
          "Naam " + shoppingItem.getName() + " bestaat al");
    }
    shoppingItem.setStandard(true);
    ShoppingItem createdShoppingItem = shoppingItemRepository.save(shoppingItem);
    standardShoppingItemList.add(createdShoppingItem);
    return createdShoppingItem;
  }

  public ShoppingItem update(ShoppingItem shoppingItem, ShoppingItem update)
      throws NotFoundException, AlreadyExistsException {
    if (!findById(shoppingItem.getId()).isPresent()) {
      throw new NotFoundException(
          "Naam " + shoppingItem.getName() + " niet gevonden");
    }
    if (!shoppingItem.getName().equals(update.getName())
        && findByName(update.getName()).isPresent()) {
      throw new AlreadyExistsException(
          "Naam " + update.getName() + " bestaat al");
    }
    shoppingItem.setAmount(update.getAmount());
    shoppingItem.setMeasureUnit(update.getMeasureUnit());
    shoppingItem.setName(update.getName());
    shoppingItem.setShopType(update.getShopType());
    shoppingItem.setIngredientType(update.getIngredientType());

    ShoppingItem updatedShoppingItem = shoppingItemRepository.save(shoppingItem);
    standardShoppingItemList.set(standardShoppingItemList.lastIndexOf(shoppingItem),
        updatedShoppingItem);
    return updatedShoppingItem;
  }

  public void remove(ShoppingItem shoppingItem) throws NotFoundException {
    if (!findById(shoppingItem.getId()).isPresent()) {
      throw new NotFoundException(
          "Naam " + shoppingItem.getName() + " niet gevonden");
    }
    shoppingItemRepository.delete(shoppingItem);
    standardShoppingItemList.remove(shoppingItem);
  }

  public Optional<ShoppingItem> findByName(String name) {
    return standardShoppingItemList.stream()
        .filter(shoppingItem -> name.equals(shoppingItem.getName())).findAny();
  }

  public Optional<ShoppingItem> findById(Long id) {
    return standardShoppingItemList.stream()
        .filter(shoppingItem -> id.equals(shoppingItem.getId())).findAny();
  }

  public void addListener(ListChangeListener<ShoppingItem> listener) {
    standardShoppingItemList.addListener(listener);
  }

  public void removeChangeListener(ListChangeListener<ShoppingItem> listener) {
    standardShoppingItemList.removeListener(listener);
  }
  
  private ObservableList<ShoppingItem> getShoppingItemList() {
    return FXCollections.observableList(shoppingItemRepository.findAll().stream()
        .filter(s -> s.isStandard())
        .collect(Collectors.toList()));
  }

  // Setter for JUnit testing only
  void setStandardShoppingItemList(ObservableList<ShoppingItem> standardShoppingItemList) {
    this.standardShoppingItemList = standardShoppingItemList;
  }

}
