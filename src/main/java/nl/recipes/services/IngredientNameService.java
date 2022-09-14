package nl.recipes.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import nl.recipes.domain.IngredientName;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.IngredientNameRepository;

@Service
public class IngredientNameService {

  private static final String INGREDIENT_NAAM = "Ingrediënt ";

  private final IngredientNameRepository ingredientNameRepository;
  private final StandardShoppingItemService standardShoppingItemService;

  private ObservableList<IngredientName> observableIngredientNameList;

  public IngredientNameService(IngredientNameRepository ingredientNameRepository,
      StandardShoppingItemService standardShoppingItemService) {
    this.ingredientNameRepository = ingredientNameRepository;
    this.standardShoppingItemService = standardShoppingItemService;
    observableIngredientNameList = FXCollections.observableList(ingredientNameRepository.findByOrderByNameAsc());
  }

  public ObservableList<IngredientName> getReadonlyIngredientNameList() {
    return FXCollections.unmodifiableObservableList(observableIngredientNameList);
  }

  public IngredientName create(IngredientName ingredientName)
      throws AlreadyExistsException, IllegalValueException {
    if (ingredientName == null || ingredientName.getName() == null
        || ingredientName.getName().isEmpty()) {
      throw new IllegalValueException("Ingrediënt naam mag niet leeg zijn");
    }
    if (ingredientNameExists(ingredientName)) {
      throw new AlreadyExistsException(INGREDIENT_NAAM + ingredientName.getName()
          + getMeasureUnitSuffix(ingredientName) + " bestaat al");
    }
    if (standardShoppingItemService.isShoppingItemName(ingredientName.getName())) {
      throw new AlreadyExistsException(ingredientName.getName() + " bestaat al als standaard boodschap");
    }
    IngredientName createdIngredientName = ingredientNameRepository.save(ingredientName);
    observableIngredientNameList.add(createdIngredientName);
    return createdIngredientName;
  }

  public IngredientName update(IngredientName ingredientName, IngredientName update)
      throws NotFoundException, AlreadyExistsException {
    if (!findById(ingredientName.getId()).isPresent()) {
      throw new NotFoundException(INGREDIENT_NAAM + ingredientName.getName()
          + getMeasureUnitSuffix(ingredientName) + " niet gevonden");
    }
    if (!(ingredientName.equals(update)) && ingredientNameExists(update)) {
      throw new AlreadyExistsException(INGREDIENT_NAAM + update.getName()
          + getMeasureUnitSuffix(update) + " bestaat al");
    }
    if (standardShoppingItemService.isShoppingItemName(update.getName())) {
      throw new AlreadyExistsException(update.getName() + " bestaat al als standaard boodschap");
    }

    update.setId(ingredientName.getId());
    IngredientName updatedIngredientName = ingredientNameRepository.save(update);

    observableIngredientNameList.set(observableIngredientNameList.lastIndexOf(ingredientName),
        updatedIngredientName);
    return updatedIngredientName;
  }

  public void remove(IngredientName ingredientName) throws NotFoundException {
    // TODO add check for removing measureUnits that are in use
    if (!findById(ingredientName.getId()).isPresent()) {
      throw new NotFoundException(INGREDIENT_NAAM + ingredientName.getName() + " niet gevonden");
    }
    ingredientNameRepository.delete(ingredientName);
    observableIngredientNameList.remove(ingredientName);
  }

  public Optional<IngredientName> findByName(String name) {
    return ingredientNameRepository.findByName(name);
  }

  public Optional<IngredientName> findById(Long id) {
    return ingredientNameRepository.findById(id);
  }
  
  public List<IngredientName> findAllStockItems() {
    return getReadonlyIngredientNameList().stream().filter(IngredientName::isStock).toList();
  }

  public void addListener(ListChangeListener<IngredientName> listener) {
    observableIngredientNameList.addListener(listener);
  }

  public void removeChangeListener(ListChangeListener<IngredientName> listener) {
    observableIngredientNameList.removeListener(listener);
  }

  boolean ingredientNameExists(IngredientName ingredientName) {
    return observableIngredientNameList.contains(ingredientName);
  }
  
  private String getMeasureUnitSuffix(IngredientName ingredientName) {
    if (ingredientName.getMeasureUnit() == null) return "";
    return " (" + ingredientName.getMeasureUnit().getName() +")";
  }
  
  // Setter for JUnit testing only
  void setObservableIngredientNameList(
      ObservableList<IngredientName> observableIngredientNameList) {
    this.observableIngredientNameList = observableIngredientNameList;
  }
}
