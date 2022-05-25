package nl.recipes.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.IngredientName;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.IngredientNameRepository;

@Slf4j
@Service
public class IngredientNameService {

  private static final String INGREDIENT_NAAM = "Ingrediënt naam ";

  private final IngredientNameRepository ingredientNameRepository;

  private ObservableList<IngredientName> observableIngredientNameList;

  public IngredientNameService(IngredientNameRepository ingredientNameRepository) {
    this.ingredientNameRepository = ingredientNameRepository;
    observableIngredientNameList =
        FXCollections.observableList(ingredientNameRepository.findByOrderByNameAsc());
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
    if (findByName(ingredientName.getName()).isPresent()) {
      throw new AlreadyExistsException(INGREDIENT_NAAM + ingredientName.getName() + " bestaat al");
    }
    IngredientName createdIngredientName = ingredientNameRepository.save(ingredientName);
    observableIngredientNameList.add(createdIngredientName);
    return createdIngredientName;
  }

  public IngredientName update(IngredientName ingredientName, IngredientName update)
      throws NotFoundException, AlreadyExistsException {
    if (!findById(ingredientName.getId()).isPresent()) {
      throw new NotFoundException(INGREDIENT_NAAM + ingredientName.getName() + " niet gevonden");
    }
    if (!ingredientName.getName().equals(update.getName())
        && findByName(update.getName()).isPresent()) {
      throw new AlreadyExistsException(INGREDIENT_NAAM + update.getName() + " bestaat al");
    }
    ingredientName.setName(update.getName());
    ingredientName.setPluralName(update.getPluralName());
    ingredientName.setStock(update.isStock());
    ingredientName.setShopType(update.getShopType());
    ingredientName.setIngredientType(update.getIngredientType());
    ingredientName.setMeasureUnit(update.getMeasureUnit());

    log.debug("{}", ingredientName);
    IngredientName updatedIngredientName = ingredientNameRepository.save(ingredientName);

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
    return observableIngredientNameList.stream()
        .filter(ingredientName -> name.equals(ingredientName.getName())).findAny();
  }

  public Optional<IngredientName> findById(Long id) {
    return observableIngredientNameList.stream()
        .filter(ingredientname -> id.equals(ingredientname.getId())).findAny();
  }

  public void addListener(ListChangeListener<IngredientName> listener) {
    observableIngredientNameList.addListener(listener);
  }

  public void removeChangeListener(ListChangeListener<IngredientName> listener) {
    observableIngredientNameList.removeListener(listener);
  }

  // Setter for JUnit testing only
  void setObservableIngredientNameList(
      ObservableList<IngredientName> observableIngredientNameList) {
    this.observableIngredientNameList = observableIngredientNameList;
  }

}
