package nl.recipes.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.repositories.PlanningRepository;

@Service
public class PlanningService {
	
	private final PlanningRepository planningRepository;
	private final RecipeService recipeService;
	
	private ObservableList<Recipe> observableRecipesList;
	private ObservableList<Planning> observablePlanningList;
	
	public PlanningService(PlanningRepository planningRepository, RecipeService recipeService) {
		this.planningRepository = planningRepository;
		this.recipeService = recipeService;
		
		List<Recipe> recipeList = new ArrayList<>();
		observableRecipesList = FXCollections.observableArrayList(recipeList);
	}

	public ObservableList<Recipe> getRecipeList() {
		return observableRecipesList;
	}
	
	public ObservableList<Planning> getPlanningList() {
		preparePlanningList();
		return observablePlanningList;
	}
	
	public void addRecipeToPlanning(Recipe recipe) {
		observableRecipesList.add(recipe);
	}
	
	public void removeRecipeFromPlanning(Recipe recipe) {
		observableRecipesList.remove(observableRecipesList.indexOf(recipe));
	}
	
	public void moveRecipeToPlanning(Planning planning, String recipeId) {
		Optional<Recipe> optionalRecipe = recipeService.findById(Long.valueOf(recipeId));
		if (optionalRecipe.isPresent()) {
			planning.addRecipe(optionalRecipe.get());
			observablePlanningList.set(observablePlanningList.indexOf(planning), planning);
			planningRepository.saveAll(observablePlanningList);
			observableRecipesList.remove(optionalRecipe.get());
		}
	}
	
	public void clearPlanning(Planning planning) {
		for (Recipe recipe : planning.getRecipes()) {
			observableRecipesList.add(recipe);
		}
		planning.setRecipes(null);
		observablePlanningList.set(observablePlanningList.indexOf(planning), planning);
		planningRepository.saveAll(observablePlanningList);
	}
	
	public void updatePlanning(Planning planning) {
		observablePlanningList.set(observablePlanningList.indexOf(planning), planning);
		planningRepository.saveAll(observablePlanningList);
	}

	public List<ShoppingItem> getShoppingList() {
		List<ShoppingItem> shoppingItems = getShoppingListItems();
		List<ShoppingItem> result = new ArrayList<>();
		for (ShoppingItem shoppingItem : shoppingItems) {
			boolean exists = false;
			for (ShoppingItem resultIngredient : result) {
				if (shoppingItem.getName().equals(resultIngredient.getName())) {
					exists = true;
					if (shoppingItem.getAmount() != null && resultIngredient.getAmount() != null) {
						resultIngredient.setAmount(shoppingItem.getAmount() + resultIngredient.getAmount());	
					}
				}
			}
			if (!exists) {
				result.add(shoppingItem);
			}
		}
		return result;
	}
	
	public List<ShoppingItem> getStandardShoppings() {
		return Collections.emptyList();
	}
	
	private List<ShoppingItem> getShoppingListItems() {
		return observablePlanningList.stream()
				.filter(Planning::isOnShoppingList)
				.map(Planning::getRecipes)
				.flatMap(List::stream)
				.map(Recipe::getIngredients)
				.flatMap(Set::stream)
				.map(i -> {
					ShoppingItem shoppingItem = new ShoppingItem();
					shoppingItem.setAmount(i.getAmount());
					shoppingItem.setMeasureUnit(i.getMeasureUnit());
					shoppingItem.setName(i.getIngredientName());
					shoppingItem.setIngredientType(i.getIngredientName().getIngredientType());
					shoppingItem.setShopType(i.getIngredientName().getShopType());
					shoppingItem.setStandard(false);
					shoppingItem.setOnList(!i.getIngredientName().isStock());
					return shoppingItem;
				})
				.collect(Collectors.toList());
	}
	
	private void preparePlanningList() {
		List<Planning> planningList = planningRepository.findAll();
		
		planningList = removeExpiredPlannings(planningList);
		
		int size = planningList.size();
		for (int i=size; i<10; i++) {
			planningList.add(new Planning(LocalDate.now().plusDays(i)));
		}
		
		observablePlanningList = FXCollections.observableArrayList(planningList);
	}
	
	private List<Planning> removeExpiredPlannings(List<Planning> planningList) {
		planningList.stream()
				.filter(p -> p.getDate().isBefore(LocalDate.now()))
				.forEach(this::registerCompletedPlanning);
		
		return planningList.stream()
				.filter(p -> !p.getDate().isBefore(LocalDate.now())).collect(Collectors.toList());
	}
	
	private void registerCompletedPlanning(Planning planning) {
	}
}
