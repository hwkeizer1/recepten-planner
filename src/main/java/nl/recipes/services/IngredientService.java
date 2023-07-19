package nl.recipes.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;

@Slf4j
@Service
public class IngredientService {

  public List<Ingredient> consolidateIngredients(List<Ingredient> ingredients) {
    List<Ingredient> ingredientList = new ArrayList<>();
    for (Ingredient ingredient : ingredients) {

      boolean exists = false;
      for (Ingredient resultIngredient : ingredientList) {
        if (canMerge(ingredient, resultIngredient)) {
          if (ingredient.getAmount() != null && resultIngredient.getAmount() != null) {
            exists = true;
            resultIngredient.setAmount(ingredient.getAmount() + resultIngredient.getAmount());
          }
        }
      }
      if (!exists) {
        Ingredient clonedIngredient = new Ingredient.IngredientBuilder()
            .withAmount(ingredient.getAmount()).withIngredientName(ingredient.getIngredientName())
            .withOnList(ingredient.isOnList()).withRecipe(ingredient.getRecipe()).build();
        ingredientList.add(clonedIngredient);
      }
    }
    return ingredientList;
  }

  boolean canMerge(Ingredient x, Ingredient y) {
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
}
