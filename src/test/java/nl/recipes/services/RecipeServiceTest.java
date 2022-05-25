package nl.recipes.services;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.repositories.RecipeRepository;
import nl.recipes.util.testdata.MockRecipes;

@Slf4j
class RecipeServiceTest {

  @Mock 
  RecipeRepository recipeRepository;
  
  @InjectMocks
  RecipeService recipeService;
  
  MockRecipes mockRecipes;
  
  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockRecipes = new MockRecipes();
//    recipeService.setObservableRecipeList(testData.getRecipeList());
  }
  
  @Test
  void testGetReadonlyRecipeList() {
//    List<Recipe> expectedList = testData.getRecipeList();
    

//    assertEquals(expectedList, recipeService.getReadonlyRecipeList());
  }
  
  @Test
  void testGetReadonlyIngredientList() {
    List<Ingredient> ingredientList = recipeService.getReadonlyIngredientList(1L);
  }


}
