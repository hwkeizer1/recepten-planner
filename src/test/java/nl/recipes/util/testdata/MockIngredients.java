package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShopType;

public class MockIngredients {
  
  MockMeasureUnits measureUnitData = new MockMeasureUnits();
  MockIngredientNames ingredientNameData = new MockIngredientNames();
  
  public List<Ingredient> getIngredientList() {
    List<Ingredient> ingredientList = new ArrayList<>();
    ingredientList.add(getIngredient(1L, 2F, getMeasureUnit(1L, "bakje", "bakjes"), 
        getIngredientName(2L, null, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(2L, 1F, null, 
        getIngredientName(3L, null, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(3L, 2.3F, getMeasureUnit(1L, "bakje", "bakjes"), 
        getIngredientName(2L, null, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(4L, 1F, null, 
        getIngredientName(3L, null, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(5L, 1F, getMeasureUnit(4L, "theelepel", "theelepels"), 
        getIngredientName(1L, null, "water", "water", true, null, IngredientType.OVERIG)));
    ingredientList.add(getIngredient(6L, 1F, getMeasureUnit(2L, "eetlepel", "eetlepels"), 
        getIngredientName(1L, null, "water", "water", true, null, IngredientType.OVERIG)));
    return ingredientList;
  }
  
  public List<Ingredient> getConsolidatedList() {
    List<Ingredient> ingredientList = new ArrayList<>();
    ingredientList.add(getIngredient(1L, 4.3F, getMeasureUnit(1L, "bakje", "bakjes"), 
        getIngredientName(2L, null, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(2L, 2F, null, 
        getIngredientName(3L, null, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE)));
    ingredientList.add(getIngredient(5L, 1F, getMeasureUnit(4L, "theelepel", "theelepels"), 
        getIngredientName(1L, null, "water", "water", true, null, IngredientType.OVERIG)));
    ingredientList.add(getIngredient(6L, 1F, getMeasureUnit(2L, "eetlepel", "eetlepels"), 
        getIngredientName(1L, null, "water", "water", true, null, IngredientType.OVERIG)));
    return ingredientList;
  }
   
  public Ingredient getIngredient(Long id, Float amount, MeasureUnit measureUnit, IngredientName ingredientName) {
    Ingredient ingredient =  new Ingredient(null, amount, measureUnit, ingredientName);
    ingredient.setId(id);
    return ingredient;
  }
  
  private MeasureUnit getMeasureUnit(Long id, String name, String pluralName) {
    return measureUnitData.getMeasureUnit(id, name, pluralName);
  }
  
  private IngredientName getIngredientName(Long id, MeasureUnit measureUnit, String name,
      String pluralName, boolean stock, ShopType shoptype, IngredientType ingredientType) {
    return ingredientNameData.getIngredientName(id, measureUnit, name, pluralName, stock, shoptype,
        ingredientType);
  }
}
