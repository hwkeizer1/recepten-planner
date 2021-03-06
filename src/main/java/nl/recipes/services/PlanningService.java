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
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.NotFoundException;
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

  public List<Ingredient> getIngredientList() {
    List<Ingredient> ingredients = getIngredientsFromPlanning();
    return consolidateIngredients(ingredients);
  }

  List<Ingredient> consolidateIngredients(List<Ingredient> ingredients) {
    List<Ingredient> ingredientList = new ArrayList<>();
    for (Ingredient ingredient : ingredients) {
      boolean exists = false;
      for (Ingredient resultIngredient : ingredientList) {
        if (canConsolidate(ingredient, resultIngredient)) {
          exists = true;
          if (ingredient.getAmount() != null && resultIngredient.getAmount() != null) {
            resultIngredient.setAmount(ingredient.getAmount() + resultIngredient.getAmount());
          }
        }
      }
      if (!exists) {
        ingredientList.add(ingredient);
      }
    }
    return ingredientList;
  }

  boolean canConsolidate(Ingredient x, Ingredient y) {
    return (x.getIngredientName().equals(y.getIngredientName()));
  }

  private List<Ingredient> getIngredientsFromPlanning() {
    return observablePlanningList.stream().filter(Planning::isOnShoppingList)
        .map(Planning::getRecipes).flatMap(List::stream).map(Recipe::getIngredients)
        .flatMap(Set::stream).collect(Collectors.toList());
  }

  private void preparePlanningList() {
    List<Planning> planningList = planningRepository.findAll();

    planningList = removeExpiredPlannings(planningList);

    int size = planningList.size();
    for (int i = size; i < 10; i++) {
      planningList.add(new Planning.PlanningBuilder()
          .withDate(LocalDate.now().plusDays(i))
          .build());
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
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
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
