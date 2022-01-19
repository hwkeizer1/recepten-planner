package nl.recipes.services;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	
	public Recipe create(Recipe recipe) throws AlreadyExistsException {
		if (recipeRepository.findByName(recipe.getName()).isPresent()) {	
			throw new AlreadyExistsException("Recept " + recipe.getName() + " bestaat al");
		}
		return recipeRepository.save(recipe);
	}
}
