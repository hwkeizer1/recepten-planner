package nl.recipes.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.PlanningRepository;

@Slf4j
@Service
public class PlanningService {

  private final PlanningRepository planningRepository;

  private final RecipeService recipeService;
  private final IngredientService ingredientService;

  private ObservableList<Recipe> observableRecipesList;

  private ObservableList<Planning> observablePlanningList;

  public PlanningService(PlanningRepository planningRepository, RecipeService recipeService,
      IngredientService ingredientService) {
    this.planningRepository = planningRepository;
    this.recipeService = recipeService;
    this.ingredientService = ingredientService;

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

  public void addRecipeToRecipeList(Recipe recipe) {
    observableRecipesList.add(recipe);
  }

  public void removeRecipeFromRecipeList(Recipe recipe) {
    observableRecipesList.remove(observableRecipesList.indexOf(recipe));
  }

  public void moveRecipeToPlanningList(Planning planning, String recipeId) {
    recipeService.findById(Long.valueOf(recipeId)).ifPresent(recipe -> {
      if (planning.getServings() == 0) {
        planning.setServings(recipe.getServings());
      } else {
        planning.setServings(recipe.getServings() < planning.getServings() ? recipe.getServings()
            : planning.getServings());
      }
      planning.addRecipe(recipe);
      observablePlanningList.set(observablePlanningList.indexOf(planning), planning);
      planningRepository.saveAll(observablePlanningList);
      observableRecipesList.remove(recipe);
    });
  }

  public void clearPlanning(Planning planning) {
    for (Recipe recipe : planning.getRecipes()) {
      observableRecipesList.add(recipe);
    }
    planning.setRecipes(null);
    planning.setServings(0);
    observablePlanningList.set(observablePlanningList.indexOf(planning), planning);
    planningRepository.saveAll(observablePlanningList);
  }

  public void setOnShoppingList(Planning planning, boolean onShoppingList) {
    planning.setOnShoppingList(onShoppingList);
    updatePlanning(planning);
  }

  public void setServings(Planning planning, String servings) {
    if (servings != null && !servings.isEmpty()) {
      planning.setServings(Integer.valueOf(servings));
      updatePlanning(planning);
    }
  }


  public List<Ingredient> getIngredientList() {
    return ingredientService.getConsolidatedIngredientsFromPlanningList(
        FXCollections.unmodifiableObservableList(observablePlanningList));
  }

  private void preparePlanningList() {
    List<Planning> planningList = planningRepository.findAll();

    planningList = removeExpiredPlannings(planningList);

    int size = planningList.size();
    for (int i = size; i < 10; i++) {
      planningList
          .add(new Planning.PlanningBuilder().withDate(LocalDate.now().plusDays(i)).build());
    }

    observablePlanningList = FXCollections.observableArrayList(planningList);
  }

  protected List<Planning> removeExpiredPlannings(List<Planning> planningList) {
    planningList.stream().filter(p -> p.getDate().isBefore(LocalDate.now()))
        .forEach(this::registerCompletedPlanning);

    planningList.stream().filter(p -> p.getDate().isBefore(LocalDate.now()))
        .forEach(this::deletePlanning);

    return planningList.stream().filter(p -> !p.getDate().isBefore(LocalDate.now()))
        .collect(Collectors.toList());
  }

  protected void registerCompletedPlanning(Planning planning) {
    for (Recipe recipe : planning.getRecipes()) {
      recipe.setLastServed(planning.getDate());
      if (recipe.getTimesServed() != null) {
        recipe.setTimesServed(recipe.getTimesServed() + 1);
      } else {
        recipe.setTimesServed(1);
      }
      try {
        recipeService.update(recipe, recipe);
      } catch (NotFoundException | AlreadyExistsException e) {
        e.printStackTrace();
      }
    }
  }

  private void updatePlanning(Planning planning) {
    observablePlanningList.set(observablePlanningList.indexOf(planning), planning);
    planningRepository.saveAll(observablePlanningList);
  }

  private void deletePlanning(Planning planning) {
    planningRepository.delete(planning);
  }

  // Setter for JUnit testing only
  void setMockObservablePlanningList(ObservableList<Planning> observablePlanningList) {
    this.observablePlanningList = observablePlanningList;
  }

  // Getter for JUnit testing only
  ObservableList<Planning> getMockObservablePlanningList() {
    return observablePlanningList;
  }
}
