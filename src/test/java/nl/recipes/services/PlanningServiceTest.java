package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.util.TestData;

class PlanningServiceTest {

  @InjectMocks
  PlanningService planningService;

  TestData testData;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    testData = new TestData();
  }

  @Test
  void testcanConsolidate() {
    MeasureUnit firstMeasureUnit = testData.getMeasureUnit(1L, "bak", "bakken");
    MeasureUnit secondMeasureUnit = testData.getMeasureUnit(1L, "zak", "zakken");
    IngredientName firstIngredientName =
        testData.getIngredientName(1L, "ui", "uien", false, null, null);
    IngredientName secondIngredientName =
        testData.getIngredientName(1L, "prei", "preien", false, null, null);
    assertEquals(true,
        planningService.canConsolidate(
            testData.getIngredient(1L, 2F, firstMeasureUnit, firstIngredientName),
            testData.getIngredient(1L, 2F, firstMeasureUnit, firstIngredientName)));
    assertEquals(false,
        planningService.canConsolidate(
            testData.getIngredient(1L, 2F, firstMeasureUnit, firstIngredientName),
            testData.getIngredient(1L, 2F, secondMeasureUnit, firstIngredientName)));
    assertEquals(false,
        planningService.canConsolidate(
            testData.getIngredient(1L, 2F, firstMeasureUnit, firstIngredientName),
            testData.getIngredient(1L, 2F, firstMeasureUnit, secondIngredientName)));
    assertEquals(false,
        planningService.canConsolidate(
            testData.getIngredient(1L, 2F, secondMeasureUnit, firstIngredientName),
            testData.getIngredient(1L, 2F, firstMeasureUnit, secondIngredientName)));
    assertEquals(false,
        planningService.canConsolidate(
            testData.getIngredient(1L, 2F, firstMeasureUnit, firstIngredientName),
            testData.getIngredient(1L, 2F, null, firstIngredientName)));
    assertEquals(false,
        planningService.canConsolidate(
            testData.getIngredient(1L, 2F, null, firstIngredientName),
            testData.getIngredient(1L, 2F, secondMeasureUnit, firstIngredientName)));
    assertEquals(true,
        planningService.canConsolidate(
            testData.getIngredient(1L, 2F, null, firstIngredientName),
            testData.getIngredient(1L, 2F, null, firstIngredientName)));
    
  }

  @Test
  void testConsolidateIngredients() {
    assertEquals(testData.getConsolidatedList(),
        planningService.consolidateIngredients(testData.getIngredientList()));
  }

}
