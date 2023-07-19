package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;

class IngredientServiceTest {

  @InjectMocks
  IngredientService ingredientService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

  }

  @Test
  void testConsolidateEqualIngredients() {
    assertEquals(IngredientListFactory.equalIngredientsResult,
        ingredientService.getConsolidatedIngredients(IngredientListFactory.equalIngredients));
  }

  @Test
  void testNotConsolidateIngredientsWithDifferentMeasureUnits() {
    assertEquals(IngredientListFactory.ingredientsWithDifferentMeasureUnits, ingredientService
        .getConsolidatedIngredients(IngredientListFactory.ingredientsWithDifferentMeasureUnits));
  }

  @Test
  void testNotConsolidateIngredientsWithDifferentNames() {
    assertEquals(IngredientListFactory.ingredientsWithDifferentNames, ingredientService
        .getConsolidatedIngredients(IngredientListFactory.ingredientsWithDifferentNames));
  }

  @Test
  void testConsolidateEqualIngredientsWithoutMeasureUnit() {
    assertEquals(IngredientListFactory.equalIngredientsWithoutMeasureUnitResult, ingredientService
        .getConsolidatedIngredients(IngredientListFactory.equalIngredientsWithoutMeasureUnit));
  }

  @Test
  void testNotConsolidateEqualIngredientsWithFirstMeasureUnitNull() {
    assertEquals(IngredientListFactory.equalIngredientsWithFirstMeasureUnitNull, ingredientService
        .getConsolidatedIngredients(IngredientListFactory.equalIngredientsWithFirstMeasureUnitNull));
  }

  @Test
  void testNotConsolidateEqualIngredientsWithSecondMeasureUnitNull() {
    assertEquals(IngredientListFactory.equalIngredientsWithSecondMeasureUnitNull, ingredientService
        .getConsolidatedIngredients(IngredientListFactory.equalIngredientsWithSecondMeasureUnitNull));
  }

  @Test
  void testNotConsolidateEqualIngredientsFirstWithoutAmount() {
    assertEquals(IngredientListFactory.equalIngredientsWithFirstWithoutAmount, ingredientService
        .getConsolidatedIngredients(IngredientListFactory.equalIngredientsWithFirstWithoutAmount));
  }

  @Test
  void testNotConsolidateEqualIngredientsSecondWithoutAmount() {
    assertEquals(IngredientListFactory.equalIngredientsWithSecondWithoutAmount, ingredientService
        .getConsolidatedIngredients(IngredientListFactory.equalIngredientsWithSecondWithoutAmount));
  }
}
