package nl.recipes.services;

import static nl.recipes.views.ViewMessages.INGREDIENT_NAME_;
import static nl.recipes.views.ViewMessages.INGREDIENT_NAME_NAME_CANNOT_BE_EMPTY;
import static nl.recipes.views.ViewMessages._ALREADY_EXISTS;
import static nl.recipes.views.ViewMessages._NOT_FOUND;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.IngredientName;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.IngredientNameRepository;

@Service
public class IngredientNameService extends ListService<IngredientName> {

  public IngredientNameService(IngredientNameRepository ingredientNameRepository) {
    repository = ingredientNameRepository;
    Sort sort = Sort.by("name").ascending();
    observableList = FXCollections.observableList(repository.findAll(sort));
    comparator = (t1, t2) -> t1.getName().compareTo(t2.getName());
  }

  public Optional<IngredientName> findByName(String name) {
    return observableList.stream().filter(ingredientName -> name.equals(ingredientName.getName()))
        .findAny();
  }

  public Optional<IngredientName> findById(Long id) {
    return observableList.stream().filter(ingredientName -> id.equals(ingredientName.getId()))
        .findAny();
  }

  public IngredientName create(IngredientName ingredientName)
      throws AlreadyExistsException, IllegalValueException {
    if (ingredientName == null || ingredientName.getName() == null
        || ingredientName.getName().isEmpty()) {
      throw new IllegalValueException(INGREDIENT_NAME_NAME_CANNOT_BE_EMPTY);
    }
    if (ingredientNameExists(ingredientName)) {
      throw new AlreadyExistsException(INGREDIENT_NAME_ + ingredientName.getName()
          + getMeasureUnitSuffix(ingredientName) + _ALREADY_EXISTS);
    }
    return save(ingredientName);
  }

  public IngredientName update(IngredientName ingredientName, IngredientName update)
      throws NotFoundException, AlreadyExistsException {
    if (!findById(ingredientName.getId()).isPresent()) {
      throw new NotFoundException(INGREDIENT_NAME_ + ingredientName.getName()
          + getMeasureUnitSuffix(ingredientName) + _NOT_FOUND);
    }
    if (!nameAndMeasureUnitIsEqual(ingredientName, update) && ingredientNameExists(update)) {
      throw new AlreadyExistsException(
          INGREDIENT_NAME_ + update.getName() + getMeasureUnitSuffix(update) + _ALREADY_EXISTS);
    }

    update.setId(ingredientName.getId());
    return update(update);
  }

  public void remove(IngredientName ingredientName) throws NotFoundException {
    if (!findById(ingredientName.getId()).isPresent()) {
      throw new NotFoundException(INGREDIENT_NAME_ + ingredientName.getName() + _NOT_FOUND);
    }
    delete(ingredientName);
  }

  public List<IngredientName> findAllStockItems() {
    return getList().stream().filter(IngredientName::isStock).toList();
  }

  boolean nameAndMeasureUnitIsEqual(IngredientName x, IngredientName y) {
    if (x.getMeasureUnit() == null) {
      return (x.getName().equals(y.getName()) && y.getMeasureUnit() == null);
    }
    if (y.getMeasureUnit() == null)
      return false;
    return (x.getName().equals(y.getName())
        && x.getMeasureUnit().getName().equals(y.getMeasureUnit().getName()));
  }

  boolean ingredientNameExists(IngredientName ingredientName) {
    Optional<IngredientName> opt = findByName(ingredientName.getName());
    boolean exists = true;
    if (opt.isPresent()) {
      IngredientName i = opt.get();
      if (i.getMeasureUnit() == null) {
        exists &= ingredientName.getMeasureUnit() == null;
      } else {
        exists &= i.getMeasureUnit().equals(ingredientName.getMeasureUnit());
      }
    } else
      exists = false;
    return exists;

  }

  private String getMeasureUnitSuffix(IngredientName ingredientName) {
    if (ingredientName.getMeasureUnit() == null)
      return "";
    return " (" + ingredientName.getMeasureUnit().getName() + ")";
  }

  /**
   * Setter for JUnit testing only!
   * 
   * @param observableList
   */
  void setObservableList(ObservableList<IngredientName> observableList) {
    this.observableList = observableList;
  }
}
