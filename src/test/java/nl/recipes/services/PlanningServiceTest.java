package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.util.testdata.MockIngredients;

class PlanningServiceTest {

  @InjectMocks
  PlanningService planningService;

  MockIngredients mockIngredients;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockIngredients = new MockIngredients();
  }

  @Test
  void testCanConsolidate_BothEqual() {
    assertEquals(true,
        planningService
            .canConsolidate(
                new Ingredient.IngredientBuilder()
                    .withIngredientName(new IngredientName.IngredientNameBuilder()
                        .withMeasureUnit(
                            new MeasureUnit.MeasureUnitBuilder().withName("zak").build())
                        .withName("ui").build())
                    .build(),
                new Ingredient.IngredientBuilder()
                    .withIngredientName(new IngredientName.IngredientNameBuilder()
                        .withMeasureUnit(
                            new MeasureUnit.MeasureUnitBuilder().withName("zak").build())
                        .withName("ui").build())
                    .build()));
  }

  @Test
  void testCanConsolidate_MeasureUnitsDifferent() {
    assertEquals(false,
        planningService
            .canConsolidate(
                new Ingredient.IngredientBuilder()
                    .withIngredientName(new IngredientName.IngredientNameBuilder()
                        .withMeasureUnit(
                            new MeasureUnit.MeasureUnitBuilder().withName("bak").build())
                        .withName("ui").build())
                    .build(),
                new Ingredient.IngredientBuilder()
                    .withIngredientName(new IngredientName.IngredientNameBuilder()
                        .withMeasureUnit(
                            new MeasureUnit.MeasureUnitBuilder().withName("zak").build())
                        .withName("ui").build())
                    .build()));
  }

  @Test
  void testCanConsolidate_IngredientNamesDifferent() {
    assertEquals(false,
        planningService
            .canConsolidate(
                new Ingredient.IngredientBuilder().withIngredientName(
                    new IngredientName.IngredientNameBuilder()
                        .withMeasureUnit(
                            new MeasureUnit.MeasureUnitBuilder().withName("zak").build())
                        .withName("prei").build())
                    .build(),
                new Ingredient.IngredientBuilder()
                    .withIngredientName(new IngredientName.IngredientNameBuilder()
                        .withMeasureUnit(
                            new MeasureUnit.MeasureUnitBuilder().withName("zak").build())
                        .withName("ui").build())
                    .build()));
  }

  @Test
  void testCanConsolidate_NoMeasureUnits() {
    assertEquals(true, planningService.canConsolidate(
        new Ingredient.IngredientBuilder()
            .withIngredientName(new IngredientName.IngredientNameBuilder().withName("ui").build())
            .build(),
        new Ingredient.IngredientBuilder()
            .withIngredientName(new IngredientName.IngredientNameBuilder().withName("ui").build())
            .build()));
  }

  @Test
  void testCanConsolidate_OneMeasureUnit() {
    assertEquals(false,
        planningService.canConsolidate(
            new Ingredient.IngredientBuilder().withIngredientName(
                new IngredientName.IngredientNameBuilder().withName("ui").build()).build(),
            new Ingredient.IngredientBuilder()
                .withIngredientName(new IngredientName.IngredientNameBuilder()
                    .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("zak").build())
                    .withName("ui").build())
                .build()));
  }

  @Test
  void testConsolidateIngredients() {
    assertEquals(mockIngredients.getConsolidatedIngredientList(),
        planningService.consolidateIngredients(mockIngredients.getIngredientList()));
  }

}
