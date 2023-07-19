package nl.recipes.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;

public class IngredientListFactory {

  public final static List<Ingredient> equalIngredients;
  public final static List<Ingredient> ingredientsWithDifferentMeasureUnits;
  public final static List<Ingredient> ingredientsWithDifferentNames;
  public final static List<Ingredient> equalIngredientsWithoutMeasureUnit;
  public final static List<Ingredient> equalIngredientsWithFirstMeasureUnitNull;
  public final static List<Ingredient> equalIngredientsWithSecondMeasureUnitNull;
  public final static List<Ingredient> equalIngredientsWithFirstWithoutAmount;
  public final static List<Ingredient> equalIngredientsWithSecondWithoutAmount;
  public final static List<Ingredient> equalIngredientsResult;
  public final static List<Ingredient> equalIngredientsWithoutMeasureUnitResult;
  
  private static IngredientName zakUi = new IngredientName.IngredientNameBuilder()
      .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("zak").build())
      .withName("ui").build();
  
  private static IngredientName bakUi = new IngredientName.IngredientNameBuilder()
      .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("bak").build())
      .withName("ui").build();
  
  private static IngredientName zakPrei = new IngredientName.IngredientNameBuilder()
      .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("zak").build())
      .withName("prei").build();
  
  private static IngredientName ui = new IngredientName.IngredientNameBuilder()
      .withName("ui").build();
  
  private static Ingredient oneZakUi = new Ingredient.IngredientBuilder()
      .withIngredientName(zakUi)
      .withAmount(1f)
      .build();
  
  private static Ingredient oneBakUi = new Ingredient.IngredientBuilder()
      .withIngredientName(bakUi)
      .withAmount(1f)
      .build();
  
  private static Ingredient oneZakPrei = new Ingredient.IngredientBuilder()
      .withIngredientName(zakPrei)
      .withAmount(1f)
      .build();
  
  private static Ingredient oneUi = new Ingredient.IngredientBuilder()
      .withIngredientName(ui)
      .withAmount(1f)
      .build();
  
  private static Ingredient uiWithoutAmount = new Ingredient.IngredientBuilder()
      .withIngredientName(ui)
      .build();
  
  private static Ingredient twoZakUi = new Ingredient.IngredientBuilder()
      .withIngredientName(zakUi)
      .withAmount(2f)
      .build();
  
  private static Ingredient twoUi = new Ingredient.IngredientBuilder()
      .withIngredientName(ui)
      .withAmount(2f)
      .build();

  
  static {
    equalIngredients = new ArrayList<>();
    Collections.addAll(equalIngredients, oneZakUi, oneZakUi);

    equalIngredientsResult = new ArrayList<>();
    equalIngredientsResult.add(twoZakUi);
  }
  
  static {
    ingredientsWithDifferentMeasureUnits = new ArrayList<>();
    Collections.addAll(ingredientsWithDifferentMeasureUnits, oneZakUi, oneBakUi);
  }
  
  static {
    ingredientsWithDifferentNames = new ArrayList<>();
    Collections.addAll(ingredientsWithDifferentNames, oneZakUi, oneZakPrei);
  }
  
  static {
    equalIngredientsWithoutMeasureUnit = new ArrayList<>();
    Collections.addAll(equalIngredientsWithoutMeasureUnit, oneUi, oneUi);
    
    equalIngredientsWithoutMeasureUnitResult = new ArrayList<>();
    equalIngredientsWithoutMeasureUnitResult.add(twoUi);
  }
  
  static {
    equalIngredientsWithFirstMeasureUnitNull = new ArrayList<>();
    Collections.addAll(equalIngredientsWithFirstMeasureUnitNull, oneUi, oneZakUi);
  }
  
  static {
    equalIngredientsWithSecondMeasureUnitNull = new ArrayList<>();
    Collections.addAll(equalIngredientsWithSecondMeasureUnitNull, oneZakUi, oneUi );
  }
  
  static {
    equalIngredientsWithFirstWithoutAmount = new ArrayList<>();
    Collections.addAll(equalIngredientsWithFirstWithoutAmount, uiWithoutAmount, oneUi );
  }

  static {
    equalIngredientsWithSecondWithoutAmount = new ArrayList<>();
    Collections.addAll(equalIngredientsWithSecondWithoutAmount, oneUi, uiWithoutAmount );
  }
}
