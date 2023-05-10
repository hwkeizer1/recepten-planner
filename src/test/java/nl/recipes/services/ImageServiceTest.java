package nl.recipes.services;

import static nl.recipes.views.ViewMessages.IMAGE_FOLDER;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.ShopType;

class ImageServiceTest {
  
  @Mock
  ConfigService configService;
  
  @InjectMocks
  ImageService imageService;
  
  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    
  }
  
  @Test
  void testFilenameAlreadyExists() {
    Set<String> existingFiles = new HashSet<>();
    existingFiles.add("test.png");
    existingFiles.add("test2.png");
    existingFiles.add("test3.png");
    
    when(configService.getConfigProperty(IMAGE_FOLDER)).thenReturn("/home/henk/temp");
    
    assertEquals(true, imageService.filenameAlreadyExists("test2.png"));
    assertEquals(true, imageService.filenameAlreadyExists("test.png"));
    assertEquals(true, imageService.filenameAlreadyExists("test.jpg"));
    assertEquals(false, imageService.filenameAlreadyExists("tes.png"));
    assertEquals(true, imageService.filenameAlreadyExists("test"));
  }
  
  @Test
  void testValidateImageName() {
    Recipe recipe = new Recipe.RecipeBuilder().withName("recipeName").withImage("recipeName.txt").build();
    assertEquals(true, imageService.validateImageName(recipe));
    
    recipe = new Recipe.RecipeBuilder().withName("recipename").withImage("recipeName.txt").build();
    assertEquals(false, imageService.validateImageName(recipe));
    
    recipe = new Recipe.RecipeBuilder().withName("recipe name").withImage("recipe name.txt").build();
    assertEquals(true, imageService.validateImageName(recipe));
    
    recipe = new Recipe.RecipeBuilder().withName("recipe name").withImage("recipe name.").build();
    assertEquals(true, imageService.validateImageName(recipe));
    
    recipe = new Recipe.RecipeBuilder().withName("recipe name").withImage("recipe name").build();
    assertEquals(true, imageService.validateImageName(recipe));
    
    recipe = new Recipe.RecipeBuilder().withName("recipe.name").withImage("recipe.name.txt").build();
    assertEquals(true, imageService.validateImageName(recipe));
    
    recipe = new Recipe.RecipeBuilder().withName("recipe.name").withImage("recipe.name").build();
    assertEquals(false, imageService.validateImageName(recipe));
    
    recipe = new Recipe.RecipeBuilder().withName("recipe.name").build();
    assertEquals(true, imageService.validateImageName(recipe));
        
  }

}
