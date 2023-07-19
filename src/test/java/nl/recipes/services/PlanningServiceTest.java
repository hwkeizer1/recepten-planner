package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javafx.collections.FXCollections;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.repositories.PlanningRepository;
import nl.recipes.util.testdata.MockIngredients;
import nl.recipes.util.testdata.MockPlannings;
import nl.recipes.util.testdata.MockRecipes;

class PlanningServiceTest {

  @Mock
  PlanningRepository planningRepository;

  @Mock
  RecipeService recipeService;
  
  @Mock
  IngredientService ingredientService;

  @Mock
  PlanningService mockPlanningService;

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
    planningService.setMockObservablePlanningList(
        FXCollections.observableArrayList(mockPlannings.getPlanningList()));
  }

  @Test
	void testGetPlanningList() {
		when(planningRepository.findAll()).thenReturn(Collections.emptyList());

		assertEquals(mockPlannings.getPlanningList(), planningService.getPlanningList());
		assertEquals(10, planningService.getPlanningList().size());
	}

  @Test
  void testMoveRecipeToPlanning() {
    for (Recipe recipe : mockRecipes.getRecipeListBasic()) {
      planningService.addRecipeToRecipeList(recipe);
    }
    assertEquals(mockRecipes.getRecipeListBasic(), planningService.getRecipeList());

    when(recipeService.findById(Long.valueOf("1")))
        .thenReturn(Optional.of(mockRecipes.getRecipeListBasic().get(0)));
    planningService.moveRecipeToPlanning(mockPlannings.getPlanningList().get(0), "1");
    assertEquals(1, planningService.getMockObservablePlanningList().get(0).getRecipes().size());
    assertEquals(3, planningService.getRecipeList().size());
  }

  @Test
  void testRemoveRecipeFromPlanning() {
    for (Recipe recipe : mockRecipes.getRecipeListBasic()) {
      planningService.addRecipeToRecipeList(recipe);
    }
    assertEquals(mockRecipes.getRecipeListBasic(), planningService.getRecipeList());

    assertEquals(4, planningService.getRecipeList().size());
    planningService.removeRecipeFromRecipeList(mockRecipes.getRecipeListBasic().get(2));
    assertEquals(3, planningService.getRecipeList().size());
  }

  @Test
  void testClearPlanning() {
    for (Recipe recipe : mockRecipes.getRecipeListBasic()) {
      planningService.addRecipeToRecipeList(recipe);
    }
    when(recipeService.findById(Long.valueOf("1")))
        .thenReturn(Optional.of(mockRecipes.getRecipeListBasic().get(0)));
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
  void testRegisterCompletedPlanning_NothingExpired() {
    List<Planning> plannings = mockPlannings.getPlanningList();

    assertEquals(mockPlannings.getPlanningList(),
        planningService.removeExpiredPlannings(plannings));
  }

  @Test
  void testRegisterCompletedPlanning_2DaysAgo_NoRecipes() {
    List<Planning> plannings = mockPlannings.getPlanningListFrom2DaysAgo();

    List<Planning> expectedPlannings = plannings.subList(2, plannings.size());

    assertEquals(expectedPlannings, planningService.removeExpiredPlannings(plannings));
  }

  @Test
  void testRegisterCompletedPlanning_2DaysAgo_WithRecipe() {
    List<Planning> plannings = mockPlannings.getPlanningListFrom2DaysAgo();
    Recipe recipe = new Recipe.RecipeBuilder().withName("newRecipe")
        .withRecipeType(RecipeType.HOOFDGERECHT).build(1L);
    Planning planning = plannings.get(0);
    planning.addRecipe(recipe);

    List<Planning> expectedPlannings = plannings.subList(2, plannings.size());

    assertEquals(expectedPlannings, planningService.removeExpiredPlannings(plannings));
  }
}
