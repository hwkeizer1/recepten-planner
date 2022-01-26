package nl.recipes.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Recipe;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.repositories.RecipeRepository;

@Service
public class RecipeService {

	private final RecipeRepository recipeRepository;
	
	private ObservableList<Recipe> observableRecipeList;

	public RecipeService(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
		observableRecipeList = FXCollections.observableList(recipeRepository.findAll());
	}
	
	public ObservableList<Recipe> getReadonlyRecipeList() {
		return FXCollections.unmodifiableObservableList(observableRecipeList);
	}
	
	public ObservableList<Ingredient> getReadonlyIngredientList(Long recipeId) {
		Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
		if (optionalRecipe.isPresent()) {
			List<Ingredient> ingredientList = new ArrayList<>(optionalRecipe.get().getIngredients());
			ObservableList<Ingredient> observableIngredientList = FXCollections.observableList(ingredientList);
			return FXCollections.unmodifiableObservableList(observableIngredientList);
		}
		return FXCollections.emptyObservableList();
	}
	
	public Recipe create(Recipe recipe) throws AlreadyExistsException {
		if (recipeRepository.findByName(recipe.getName()).isPresent()) {	
			throw new AlreadyExistsException("Recept " + recipe.getName() + " bestaat al");
		}
		return recipeRepository.save(recipe);
	}
}
