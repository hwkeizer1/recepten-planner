package nl.recipes.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
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
