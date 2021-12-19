package nl.recipes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import nl.recipes.domain.Recipe;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.repositories.RecipeRepository;

@Service
public class RecipeService {

	private final RecipeRepository recipeRepository;

	public RecipeService(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}
	
	public List<Recipe> findAll() {
		return recipeRepository.findAll();
	}
	
	public Recipe create(Recipe recipe) throws AlreadyExistsException {
		if (recipeRepository.findByName(recipe.getName()).isPresent()) {	
			throw new AlreadyExistsException("Recept " + recipe.getName() + " bestaat al");
		}
		return recipeRepository.save(recipe);
	}
}
