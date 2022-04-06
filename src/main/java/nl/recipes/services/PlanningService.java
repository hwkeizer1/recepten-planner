package nl.recipes.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Recipe;

@Slf4j
@Service
public class PlanningService {
	
	private ObservableList<Recipe> observablePlannedRecipesList;
	
	
	
	public PlanningService() {
		List<Recipe> recipeList = new ArrayList<>();
		observablePlannedRecipesList = FXCollections.observableArrayList(recipeList);
	}

	public ObservableList<Recipe> getReadonlyRecipeList() {
		return new FilteredList<>(FXCollections.unmodifiableObservableList(observablePlannedRecipesList));
	}
	
	public void addRecipeToPlanning(Recipe recipe) {
		log.debug("{}", recipe);
		observablePlannedRecipesList.add(recipe);
	}
	
	public void removeRecipeFromPlanning(Recipe recipe) {
		observablePlannedRecipesList.remove(observablePlannedRecipesList.indexOf(recipe));
	}
}
