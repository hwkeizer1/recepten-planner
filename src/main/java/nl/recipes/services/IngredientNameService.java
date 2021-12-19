package nl.recipes.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nl.recipes.domain.IngredientName;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.repositories.IngredientNameRepository;

@Service
public class IngredientNameService {

	private final IngredientNameRepository ingredientNameRepository;

	public IngredientNameService(IngredientNameRepository ingredientNameRepository) {
		this.ingredientNameRepository = ingredientNameRepository;
	}
	
	public List<IngredientName> findAll() {
		return ingredientNameRepository.findAll();
	}
	
	public IngredientName create(IngredientName ingredientName) throws AlreadyExistsException {
		if (ingredientNameRepository.findByName(ingredientName.getName()).isPresent()) {	
			throw new AlreadyExistsException("Ingrediënt naam " + ingredientName.getName() + " bestaat al");
		}
		return ingredientNameRepository.save(ingredientName);
	}
	
	public Optional<IngredientName> findByName(String name) {
		return ingredientNameRepository.findByName(name);
	}
	
}
