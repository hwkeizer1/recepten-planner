package nl.recipes.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;

@Slf4j
@Service
public class IngredientService {
  
  public List<Ingredient> getConsolidatedIngredientsFromPlanningList(List<Planning> planningList) {
    List<Ingredient> result = new ArrayList<>();
    for (Planning planning : planningList) {
      if (planning.isOnShoppingList()) {
        for (Recipe recipe : planning.getRecipes()) {
          for (Ingredient ingredient : recipe.getIngredients()) {
            result.add(updateIngredientsForServings(cloneIngredient(ingredient),
                recipe.getServings(), planning.getServings()));
          }
        }
      }
    }
    return getConsolidatedIngredients(result);
  }
  
  
  public List<Ingredient> getConsolidatedIngredients(List<Ingredient> ingredients) {
    return consolidateIngredients(ingredients);
  }
  
  private Ingredient updateIngredientsForServings(Ingredient ingredient, int recipeServings, int planningServings) {
//    log.debug("{}", planningServings);
    ingredient.setAmount(ingredient.getAmount() * ((float)planningServings/recipeServings));
    return ingredient;
  }
  
  private List<Ingredient> consolidateIngredients(List<Ingredient> ingredients) {
    List<Ingredient> ingredientList = new ArrayList<>();
    for (Ingredient ingredient : ingredients) {

      boolean exists = false;
      for (Ingredient resultIngredient : ingredientList) {
        if (canMerge(ingredient, resultIngredient)
            && (ingredient.getAmount() != null && resultIngredient.getAmount() != null)) {
          exists = true;
          resultIngredient.setAmount(ingredient.getAmount() + resultIngredient.getAmount());
        }
      }
      if (!exists) {
        Ingredient clonedIngredient = cloneIngredient(ingredient);
        ingredientList.add(clonedIngredient);
      }
    }
    return ingredientList;
  }
  
  private boolean canMerge(Ingredient x, Ingredient y) {
    if (x.getIngredientName().getMeasureUnit() == null) {
      return (x.getIngredientName().getName().equals(y.getIngredientName().getName())
          && y.getIngredientName().getMeasureUnit() == null);
    }
    if (y.getIngredientName().getMeasureUnit() == null)
      return false;
    return (x.getIngredientName().getName().equals(y.getIngredientName().getName())
        && x.getIngredientName().getMeasureUnit().getName()
            .equals(y.getIngredientName().getMeasureUnit().getName()));
  }
  
  private Ingredient cloneIngredient(Ingredient ingredient) {
    return new Ingredient.IngredientBuilder()
        .withAmount(ingredient.getAmount()).withIngredientName(ingredient.getIngredientName())
        .withOnList(ingredient.isOnList()).withRecipe(ingredient.getRecipe()).build();
  }
}
