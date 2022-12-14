package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.ShopType;

public class MockIngredients {

  public Set<Ingredient> getIngredientSet() {
    return new HashSet<Ingredient>(getIngredientList());
  }

  public List<Ingredient> getIngredientList() {
    List<Ingredient> ingredientList = new ArrayList<>();
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(2F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("bakje")
                .withPluralName("bakjes").build(1L))
            .withName("ui").withPluralName("uien").withStock(true).withShopType(ShopType.EKO)
            .withIngredientType(IngredientType.GROENTE).build(1L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 1").build(1L)).build(1L));
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(1F)
        .withIngredientName(new IngredientName.IngredientNameBuilder().withMeasureUnit(null)
            .withName("prei").withPluralName("preien").withStock(false).withShopType(ShopType.DEKA)
            .withIngredientType(IngredientType.GROENTE).build(3L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 1").build(1L)).build(2L));
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(2.3F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("bakje")
                .withPluralName("bakjes").build(1L))
            .withName("ui").withPluralName("uien").withStock(true).withShopType(ShopType.EKO)
            .withIngredientType(IngredientType.GROENTE).build(1L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 2").build(2L)).build(3L));
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(1F)
        .withIngredientName(new IngredientName.IngredientNameBuilder().withMeasureUnit(null)
            .withName("prei").withPluralName("preien").withStock(false).withShopType(ShopType.DEKA)
            .withIngredientType(IngredientType.GROENTE).build(3L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 2").build(2L)).build(4L));
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(1F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("theelepel")
                .withPluralName("theelepels").build(4L))
            .withName("water").withPluralName("water").withStock(true).withShopType(null)
            .withIngredientType(IngredientType.OVERIG).build(2L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 1").build(1L)).build(5L));
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(1F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("eetlepel")
                .withPluralName("eetlepels").build(4L))
            .withName("water").withPluralName("water").withStock(true).withShopType(null)
            .withIngredientType(IngredientType.OVERIG).build(2L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 2").build(2L)).build(6L));
    return ingredientList;
  }

  public List<Ingredient> getConsolidatedIngredientList() {
    List<Ingredient> ingredientList = new ArrayList<>();

    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(1F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("theelepel")
                .withPluralName("theelepels").build(4L))
            .withName("water").withPluralName("water").withStock(true).withShopType(null)
            .withIngredientType(IngredientType.OVERIG).build(2L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 1").build(1L)).build());
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(1F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("eetlepel")
                .withPluralName("eetlepels").build(4L))
            .withName("water").withPluralName("water").withStock(true).withShopType(null)
            .withIngredientType(IngredientType.OVERIG).build(2L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 2").build(2L)).build());
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(2F)
        .withIngredientName(new IngredientName.IngredientNameBuilder().withMeasureUnit(null)
            .withName("prei").withPluralName("preien").withStock(false).withShopType(ShopType.DEKA)
            .withIngredientType(IngredientType.GROENTE).build(3L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 1").build(1L)).build());
    ingredientList.add(new Ingredient.IngredientBuilder().withAmount(4.3F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("bakje")
                .withPluralName("bakjes").build(1L))
            .withName("ui").withPluralName("uien").withStock(true).withShopType(ShopType.EKO)
            .withIngredientType(IngredientType.GROENTE).build(1L))
        .withRecipe(new Recipe.RecipeBuilder().withName("Recipe 1").build(1L)).build());
    return ingredientList;
  }
}
