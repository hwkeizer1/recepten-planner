package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.repositories.RecipeRepository;
import nl.recipes.util.testdata.MockIngredients;
import nl.recipes.util.testdata.MockRecipes;

@Slf4j
class RecipeServiceTest {

  @Mock 
  RecipeRepository recipeRepository;
  
  @InjectMocks
  RecipeService recipeService;
  
  MockRecipes mockRecipes;
  MockIngredients mockIngredients;
  
  
  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockRecipes = new MockRecipes();
    mockIngredients = new MockIngredients();
    recipeService.setObservableRecipeList(mockRecipes.getRecipeListBasic());
  }
  
  @Test 
  void testGetReadonlyRecipeList() {
    assertEquals(recipeService.getReadonlyRecipeList(), mockRecipes.getRecipeListBasic());
  }
  
  @Test
  void testCreateRecipe_HappyPath() throws Exception{
    Recipe recipe = new Recipe.RecipeBuilder()
        .withName("Last recipe")
        .withRecipeType(RecipeType.BIJGERECHT)
        .build();
    
    Recipe savedRecipe = new Recipe.RecipeBuilder()
        .withName("Last recipe")
        .withRecipeType(RecipeType.BIJGERECHT)
        .build(5L);
    
    when(recipeRepository.save(recipe)).thenReturn(savedRecipe);
    
    assertEquals(savedRecipe, recipeService.create(recipe));
    assertEquals(5, recipeService.getReadonlyRecipeList().size());
    assertEquals(Optional.of(savedRecipe), recipeService.findByName("Last recipe"));
  }
  
  @Test
  void testCreate_AlreadyExistsException() throws Exception {
    Recipe recipe = new Recipe.RecipeBuilder()
        .withName("First recipe")
        .withRecipeType(RecipeType.HOOFDGERECHT)
        .build();

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      recipeService.create(recipe);
    });

    Assertions.assertEquals("Recept First recipe bestaat al", exception.getMessage());
    assertEquals(4, recipeService.getReadonlyRecipeList().size());
  }
  
  @Test
  void testFindByName() {
    Recipe recipe = new Recipe.RecipeBuilder()
        .withName("Third recipe")
        .withRecipeType(RecipeType.NAGERECHT).build(3L);
    assertEquals(recipeService.findByName("Third recipe"), Optional.of(recipe));
  }
  
  @Test
  void testFindById() {
    Recipe recipe = new Recipe.RecipeBuilder()
        .withName("Fourth recipe")
        .withRecipeType(RecipeType.HOOFDGERECHT).build(4L);
    assertEquals(recipeService.findById(4L), Optional.of(recipe));
  }
  
  @Test
  void testGetEditableIngredientList() {
    
    when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipes.getRecipeListBasic().get(0)));

    List<Ingredient> ingredientList = recipeService.getEditableIngredientList(1L);
    ingredientList.sort(Comparator.comparing(Ingredient::getId));
    assertEquals(mockIngredients.getIngredientList(), ingredientList);
  }
  
  @Test
  void testGetEditableIngredientList_RecipeIdIsNull() {
    
    when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipes.getRecipeListBasic().get(0)));

    List<Ingredient> ingredientList = recipeService.getEditableIngredientList(null);
    ingredientList.sort(Comparator.comparing(Ingredient::getId));
    assertEquals(Collections.EMPTY_LIST, ingredientList);
  }
  
  @Test
  void testGetEditableIngredientList_RecipeIdDoesNotExist() {
    
    when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

    List<Ingredient> ingredientList = recipeService.getEditableIngredientList(6L);
    ingredientList.sort(Comparator.comparing(Ingredient::getId));
    assertEquals(Collections.EMPTY_LIST, ingredientList);
  }
  
  @Test
  void testGetReadOnlyIngredientList() {
    
    when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipes.getRecipeListBasic().get(0)));

    List<Ingredient> ingredientList = new ArrayList<>(recipeService.getReadonlyIngredientList(1L));
    ingredientList.sort(Comparator.comparing(Ingredient::getId));
    assertEquals(mockIngredients.getIngredientList(), ingredientList);
  }


}
