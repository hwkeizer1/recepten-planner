package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;

public class MockRecipes {

  public ObservableList<Recipe> getRecipeListBasic() {
    List<Recipe> recipeList = new ArrayList<>();
    recipeList.add(getRecipe(1L, "First recipe", RecipeType.HOOFDGERECHT));
    recipeList.add(getRecipe(2L, "Second recipe", RecipeType.AMUSE));
    recipeList.add(getRecipe(3L, "Third recipe", RecipeType.NAGERECHT));
    recipeList.add(getRecipe(4L, "Fourth recipe", RecipeType.VOORGERECHT));
    return FXCollections.observableList(recipeList);
  }
  
  public Recipe getRecipe(Long id, String name, RecipeType recipeType) {
    Recipe recipe = new Recipe(name);
    recipe.setId(id);
    recipe.setRecipeType(recipeType);
    return recipe;
  }
  

}
