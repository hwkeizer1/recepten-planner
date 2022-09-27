package nl.recipes.services;

import static nl.recipes.views.ViewMessages.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Recipe;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.RecipeRepository;

@Service
public class RecipeService extends ListService<Recipe> {

  private ObservableList<Ingredient> observableIngredientList;

  public RecipeService(RecipeRepository recipeRepository) {
    repository = recipeRepository;
    Sort sort = Sort.by("name").ascending();
    observableList = FXCollections.observableList(repository.findAll(sort));
    comparator = (t1, t2)-> t1.getName().compareTo(t2.getName());
  }

	public Optional<Recipe> findByName(String name) {
		return observableList.stream().filter(tag -> name.equals(tag.getName())).findAny();
	}

	public Optional<Recipe> findById(Long id) {
		return observableList.stream().filter(tag -> id.equals(tag.getId())).findAny();
	}
	  
  public ObservableList<Ingredient> getReadonlyIngredientList(Long recipeId) {
    return getAllIngredients(recipeId, true);
  }

  public ObservableList<Ingredient> getEditableIngredientList(Long recipeId) {
    return getAllIngredients(recipeId, false);
  }

  private ObservableList<Ingredient> getAllIngredients(Long recipeId, boolean readonly) {
    if (recipeId == null)
      return FXCollections.emptyObservableList();
    Optional<Recipe> optionalRecipe = findById(recipeId);
    if (optionalRecipe.isPresent()) {
      List<Ingredient> ingredientList = new ArrayList<>(optionalRecipe.get().getIngredients());
      observableIngredientList = FXCollections.observableList(ingredientList);
      if (readonly) {
        return FXCollections.unmodifiableObservableList(observableIngredientList);
      } else {
        return FXCollections.observableList(observableIngredientList);
      }
    }
    return FXCollections.emptyObservableList();
  }

  public Recipe create(Recipe recipe) throws AlreadyExistsException {
    if (findByName(recipe.getName()).isPresent()) {
      throw new AlreadyExistsException(RECIPE_ + recipe.getName() + _ALREADY_EXISTS);
    }
    return save(recipe);
  }

  public Recipe update(Recipe recipe, Recipe update)
      throws NotFoundException, AlreadyExistsException {
    if (!findById(recipe.getId()).isPresent()) {
      throw new NotFoundException(RECIPE_ + recipe.getName() + _NOT_FOUND);
    }
    if (!recipe.getName().equals(update.getName()) && findByName(update.getName()).isPresent()) {
      throw new AlreadyExistsException(RECIPE_ + update.getName() + _ALREADY_EXISTS);
    }
    
    update.setId(recipe.getId());
    return update(update);
  }

  public void remove(Recipe recipe) throws NotFoundException {
    if (!findById(recipe.getId()).isPresent()) {
      throw new NotFoundException(RECIPE_ + recipe.getName() + _NOT_FOUND);
    }
    
    delete(recipe);
  }

  public void addIngredientListListener(ListChangeListener<Ingredient> listener) {
    observableIngredientList.addListener(listener);
  }

  public void removeIngredientListListener(ListChangeListener<Ingredient> listener) {
    observableIngredientList.removeListener(listener);
  }

  // Setter for JUnit testing only
  void setObservableRecipeList(ObservableList<Recipe> observableList) {
	  this.observableList = observableList;
  }
}
