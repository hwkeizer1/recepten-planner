package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javafx.collections.FXCollections;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
import nl.recipes.repositories.PlanningRepository;
import nl.recipes.util.testdata.MockIngredients;
import nl.recipes.util.testdata.MockPlannings;
import nl.recipes.util.testdata.MockRecipes;

@Slf4j
class PlanningServiceTest {

  @Mock
  PlanningRepository planningRepository;
  
  @Mock
  RecipeService recipeService;
  
  @InjectMocks
  PlanningService planningService;

  MockIngredients mockIngredients;
  MockRecipes mockRecipes;
  MockPlannings mockPlannings;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockIngredients = new MockIngredients();
    mockRecipes = new MockRecipes();
    mockPlannings = new MockPlannings();
    planningService.setMockObservablePlanningList(FXCollections.observableArrayList(mockPlannings.getPlanningList()));
//    planningService.setMockObservableRecipeList(FXCollections.observableArrayList(mockRecipes.getRecipeListBasic()));
  }

  @Test 
  void testGetPlanningList() {
    when(planningRepository.findAll()).thenReturn(Collections.emptyList());
    
    assertEquals(mockPlannings.getPlanningList(), planningService.getPlanningList());
    assertEquals(10, planningService.getPlanningList().size());
  }
  
  @Test
  void testMoveRecipeToPlanning() {
    for (Recipe recipe: mockRecipes.getRecipeListBasic()) {
      planningService.addRecipeToPlanning(recipe);
    }
    assertEquals(mockRecipes.getRecipeListBasic(), planningService.getRecipeList());
    
    when(recipeService.findById(Long.valueOf("1"))).thenReturn(Optional.of(mockRecipes.getRecipeListBasic().get(0)));
    planningService.moveRecipeToPlanning(mockPlannings.getPlanningList().get(0), "1");
    assertEquals(1, planningService.getMockObservablePlanningList().get(0).getRecipes().size());
    assertEquals(3, planningService.getRecipeList().size());
  }
  
  @Test
  void testRemoveRecipeFromPlanning() {
    for (Recipe recipe: mockRecipes.getRecipeListBasic()) {
      planningService.addRecipeToPlanning(recipe);
    }
    assertEquals(mockRecipes.getRecipeListBasic(), planningService.getRecipeList());
    
    assertEquals(4, planningService.getRecipeList().size());
    planningService.removeRecipeFromPlanning(mockRecipes.getRecipeListBasic().get(2));
    assertEquals(3, planningService.getRecipeList().size());
  }
  
  @Test
  void testClearPlanning() {
    for (Recipe recipe: mockRecipes.getRecipeListBasic()) {
      planningService.addRecipeToPlanning(recipe);
    }
    when(recipeService.findById(Long.valueOf("1"))).thenReturn(Optional.of(mockRecipes.getRecipeListBasic().get(0)));
    planningService.moveRecipeToPlanning(mockPlannings.getPlanningList().get(0), "1");
    
    // Recipe moved from RecipeList to PlanningList
    assertEquals(3, planningService.getRecipeList().size());
    assertEquals(1, planningService.getMockObservablePlanningList().get(0).getRecipes().size());
    
    planningService.clearPlanning(planningService.getMockObservablePlanningList().get(0));
    
    // Recipe moved back from PlanningList to RecipeList
    assertEquals(4, planningService.getRecipeList().size());
    assertNull(planningService.getMockObservablePlanningList().get(0).getRecipes());
  }
  
  @Test
  void testUpdatePlanning() {
    Planning planning = mockPlannings.getPlanningList().get(3);
    
    assertEquals(true, planningService.getMockObservablePlanningList().get(3).isOnShoppingList());
    planning.setOnShoppingList(false);
    
    planningService.updatePlanning(planning);
    assertEquals(false, planningService.getMockObservablePlanningList().get(3).isOnShoppingList());
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
