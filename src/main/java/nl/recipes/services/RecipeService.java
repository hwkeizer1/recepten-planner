package nl.recipes.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Recipe;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.RecipeRepository;

@Service
public class RecipeService {

  private final RecipeRepository recipeRepository;

  private ObservableList<Recipe> observableRecipeList;

  private ObservableList<Ingredient> observableIngredientList;

  public RecipeService(RecipeRepository recipeRepository) {
    this.recipeRepository = recipeRepository;
    observableRecipeList = FXCollections.observableList(recipeRepository.findByOrderByNameAsc());
  }

  public FilteredList<Recipe> getReadonlyRecipeList() {
    return new FilteredList<>(FXCollections.unmodifiableObservableList(observableRecipeList));
  }

  public ObservableList<Ingredient> getReadonlyIngredientList(Long recipeId) {
    return allIngredients(recipeId, true);
  }

  public ObservableList<Ingredient> getEditableIngredientList(Long recipeId) {
    return allIngredients(recipeId, false);
  }

  private ObservableList<Ingredient> allIngredients(Long recipeId, boolean readonly) {
    if (recipeId == null)
      return FXCollections.emptyObservableList();
    Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
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
    if (recipeRepository.findByName(recipe.getName()).isPresent()) {
      throw new AlreadyExistsException("Recept " + recipe.getName() + " bestaat al");
    }
    Recipe createdRecipe = recipeRepository.save(recipe);
    observableRecipeList.add(createdRecipe);
    return createdRecipe;
  }

  public Recipe update(Recipe recipe) {
    Recipe updatedRecipe = recipeRepository.save(recipe);
    observableRecipeList.set(observableRecipeList.lastIndexOf(recipe), updatedRecipe);
    return updatedRecipe;
  }

  public void remove(Recipe recipe) throws NotFoundException {
    // TODO add check for removing recipe that is in use (planning)
    if (!findById(recipe.getId()).isPresent()) {
      throw new NotFoundException("Recept " + recipe.getName() + " niet gevonden");
    }
    recipeRepository.delete(recipe);
    observableRecipeList.remove(recipe);
  }

  public Optional<Recipe> findByName(String name) {
    return observableRecipeList.stream().filter(recipe -> name.equals(recipe.getName())).findAny();
  }

  public Optional<Recipe> findById(Long id) {
    return observableRecipeList.stream().filter(recipe -> id.equals(recipe.getId())).findAny();
  }

  public void addRecipeListListener(ListChangeListener<Recipe> listener) {
    observableRecipeList.addListener(listener);
  }

  public void removeRecipeListListener(ListChangeListener<Recipe> listener) {
    observableRecipeList.removeListener(listener);
  }

  public void addIngredientListListener(ListChangeListener<Ingredient> listener) {
    observableIngredientList.addListener(listener);
  }

  public void removeIngredientListListener(ListChangeListener<Ingredient> listener) {
    observableIngredientList.removeListener(listener);
  }

}
