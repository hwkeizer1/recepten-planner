package nl.recipes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.Tag;

public class TestData {
  
  public ObservableList<Recipe> getRecipeList() {
    List<Recipe> recipeList = new ArrayList<>();
    recipeList.add(getRecipe(1L, "First recipe", RecipeType.HOOFDGERECHT));
    recipeList.add(getRecipe(2L, "Second recipe", RecipeType.AMUSE));
    recipeList.add(getRecipe(3L, "Third recipe", RecipeType.NAGERECHT));
    recipeList.add(getRecipe(4L, "Fourth recipe", RecipeType.VOORGERECHT));
    return FXCollections.observableList(recipeList);
  }
  
  public List<Ingredient> getIngredientList() {
    List<Ingredient> ingredientList = new ArrayList<>();
    ingredientList.add(getIngredient(1L, 2F, getMeasureUnit(1L, "bakje", "bakjes"), 
        getIngredientName(2L, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(2L, 1F, null, 
        getIngredientName(3L, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(3L, 2.3F, getMeasureUnit(1L, "bakje", "bakjes"), 
        getIngredientName(2L, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(4L, 1F, null, 
        getIngredientName(3L, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(5L, 1F, getMeasureUnit(4L, "theelepel", "theelepels"), 
        getIngredientName(1L, "water", "water", true, null, IngredientType.OVERIG)));
    ingredientList.add(getIngredient(6L, 1F, getMeasureUnit(2L, "eetlepel", "eetlepels"), 
        getIngredientName(1L, "water", "water", true, null, IngredientType.OVERIG)));
    return ingredientList;
  }
  
  public List<Ingredient> getConsolidatedList() {
    List<Ingredient> ingredientList = new ArrayList<>();
    ingredientList.add(getIngredient(1L, 4.3F, getMeasureUnit(1L, "bakje", "bakjes"), 
        getIngredientName(2L, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(2L, 2F, null, 
        getIngredientName(3L, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(5L, 1F, getMeasureUnit(4L, "theelepel", "theelepels"), 
        getIngredientName(1L, "water", "water", true, null, IngredientType.OVERIG)));
    ingredientList.add(getIngredient(6L, 1F, getMeasureUnit(2L, "eetlepel", "eetlepels"), 
        getIngredientName(1L, "water", "water", true, null, IngredientType.OVERIG)));
    return ingredientList;
  }
  
  public ObservableList<MeasureUnit> getMeasureUnitList() {
    List<MeasureUnit> measureUnitList = new ArrayList<>();
    measureUnitList.add(getMeasureUnit(1L, "bakje", "bakjes"));
    measureUnitList.add(getMeasureUnit(2L, "eetlepel", "eetlepels"));
    measureUnitList.add(getMeasureUnit(3L, "pot", "potten"));
    measureUnitList.add(getMeasureUnit(4L, "theelepel", "theelepels"));
    return FXCollections.observableList(measureUnitList);
  }

  public ObservableList<IngredientName> getIngredientNameList() {
    List<IngredientName> ingredientnameList = new ArrayList<>();
    ingredientnameList
        .add(getIngredientName(1L, "water", "water", true, null, IngredientType.OVERIG));
    ingredientnameList
        .add(getIngredientName(2L, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE));
    ingredientnameList
        .add(getIngredientName(3L, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE));
    ingredientnameList.add(getIngredientName(4L, "mozzarella", "mozzarella", false, ShopType.DEKA,
        IngredientType.ZUIVEL));
    return FXCollections.observableList(ingredientnameList);
  }
  
  public ObservableList<Tag> getTagList() {
    List<Tag> tagList = new ArrayList<>();
    tagList.add(getTag(1L, "Vegetarisch"));
    tagList.add(getTag(2L, "Makkelijk"));
    tagList.add(getTag(3L, "Feestelijk"));
    tagList.add(getTag(4L, "Pasta"));
    return FXCollections.observableList(tagList);
  }
  
  public Recipe getRecipe(Long id, String name, RecipeType recipeType) {
    Recipe recipe = new Recipe(name);
    recipe.setId(id);
    recipe.setRecipeType(recipeType);
    Set<Ingredient> ingredientList = new HashSet<>();
    ingredientList.addAll(getIngredientList());
    recipe.setIngredients(ingredientList);
    return recipe;
  }
  
  public Ingredient getIngredient(Long id, Float amount, MeasureUnit measureUnit, IngredientName ingredientName) {
    Ingredient ingredient =  new Ingredient(null, amount, measureUnit, ingredientName);
    ingredient.setId(id);
    return ingredient;
  }
  
  public MeasureUnit getMeasureUnit(Long id, String name, String pluralName) {
    return new MeasureUnit(id, name, pluralName);
  }

  public IngredientName getIngredientName(Long id, String name, String pluralName, boolean stock,
      ShopType shopType, IngredientType ingredientType) {
    return new IngredientName(id, null, name, pluralName, stock, shopType, ingredientType);
  }
  
  public Tag getTag(Long id, String name) {
    return new Tag(id, name);
  }
}
