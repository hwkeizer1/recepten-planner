package nl.recipes.services;

import static nl.recipes.views.ViewConstants.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;

@Slf4j
@Service
public class BackupService {

  private static final String FOUT_BIJ_HET_VERWERKEN_VAN_DE_BACKUP_FILE =
      "Fout bij het verwerken van de backup file ";

  private static final String FOUT_BIJ_HET_MAKEN_VAN_DE_BACKUP_FILE =
      "Fout bij het maken van de backup file ";

  private static final String FOUT_BIJ_HET_LEZEN_VAN_DE_BACKUP_FILE =
      "Fout bij het lezen van de backup file ";

  private static final String FOUT_BIJ_HET_SCHRIJVEN_VAN_DE_BACKUP_FILE =
      "Fout bij het schrijven van de backup file ";

  private static final String TAGS_PLAN = "tags.plan";

  private static final String INGREDIENT_NAMES_PLAN = "ingredientnames.plan";

  private static final String MEASURE_UNITS_PLAN = "measureunits.plan";

  private static final String RECIPES_PLAN = "recipes.plan";
  
  private static final String SHOPPINGITEMS_PLAN = "shoppingitems.plan";

  private final TagService tagService;

  private final IngredientNameService ingredientNameService;

  private final MeasureUnitService measureUnitService;

  private final RecipeService recipeService;
  
  private final ShoppingItemService shoppingItemService;

  private final ConfigService configService;

  private final ObjectMapper objectMapper;

  public BackupService(TagService tagService, IngredientNameService ingredientNameService,
      MeasureUnitService measureUnitService, RecipeService recipeService,
      ConfigService configService, ShoppingItemService shoppingItemService) {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    this.tagService = tagService;
    this.ingredientNameService = ingredientNameService;
    this.measureUnitService = measureUnitService;
    this.recipeService = recipeService;
    this.shoppingItemService = shoppingItemService;
    this.configService = configService;
  }

  public void restore(String directoryPath) {
    String tags = readTagsFromFile(directoryPath);
    if (tags != null) {
      restoreTags(tags);
    }
    String ingredientNames = readIngredientNamesFromFile(directoryPath);
    if (ingredientNames != null) {
      restoreIngredientNames(ingredientNames);
    }
    String measureUnits = readMeasureUnitsFromFile(directoryPath);
    if (measureUnits != null) {
      restoreMeasureUnits(measureUnits);
    }
    String recipes = readRecipesFromFile(directoryPath);
    if (recipes != null) {
      restoreRecipes(recipes);
    }
    
    String shoppingItems = readShoppingItemsFromFile(directoryPath);
    if (shoppingItems != null) {
      restoreShoppingItems(shoppingItems);
    }
  }

  public void backup(String directoryPath) {
    Path backupDirectory = Path.of(directoryPath, LocalDate.now().toString());
    if (!Files.exists(backupDirectory) || !Files.isDirectory(backupDirectory)) {
      try {
        Files.createDirectory(backupDirectory);
      } catch (IOException e) {
        log.error("Could not create backup directory: {}", e.getMessage());
      }
    }

    removeOldBackups(directoryPath);

    String tags = backupTags();
    if (tags != null) {
      writeTagsToFile(backupDirectory.toString(), tags);
    }
    String ingredientNames = backupIngredientNames();
    if (ingredientNames != null) {
      writeIngredientNamesToFile(backupDirectory.toString(), ingredientNames);
    }
    String measureUnits = backupMeasureUnits();
    if (measureUnits != null) {
      writeMeasureUnitsToFile(backupDirectory.toString(), measureUnits);
    }
    String recipes = backupRecipes();
    if (recipes != null) {
      writeRecipesToFile(backupDirectory.toString(), recipes);
    }
    
    String shoppingItems = backupShoppingItems();
    if (shoppingItems != null) {
      writeShoppingItemsToFile(backupDirectory.toString(), shoppingItems);
    }
  }

  private String backupTags() {
    List<Tag> tagList = tagService.getReadonlyTagList();
    try {
      return objectMapper.writeValueAsString(tagList);
    } catch (JsonProcessingException e) {
      log.error(FOUT_BIJ_HET_MAKEN_VAN_DE_BACKUP_FILE + TAGS_PLAN);
    }
    return "";
  }

  private void writeTagsToFile(String directoryPath, String tags) {
    File tagFile = new File(directoryPath, TAGS_PLAN);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tagFile))) {
      writer.write(tags);
    } catch (IOException ex) {
      log.error(FOUT_BIJ_HET_SCHRIJVEN_VAN_DE_BACKUP_FILE + TAGS_PLAN);
    }
  }

  private String readTagsFromFile(String directoryPath) {
    File tagFile = new File(directoryPath, TAGS_PLAN);
    if (tagFile.exists()) {
      try (BufferedReader reader = new BufferedReader(new FileReader(tagFile))) {
        return reader.readLine();
      } catch (IOException ex) {
        log.error(FOUT_BIJ_HET_LEZEN_VAN_DE_BACKUP_FILE + TAGS_PLAN);
      }
    }
    return null;
  }

  private void restoreTags(String tags) {
    try {
      List<Tag> tagList = objectMapper.readValue(tags, new TypeReference<List<Tag>>() {});
      for (Tag tag : tagList) {
        tag.setId(null);
        createTag(tag);
      }
    } catch (JsonProcessingException ex) {
      log.error(FOUT_BIJ_HET_VERWERKEN_VAN_DE_BACKUP_FILE + TAGS_PLAN);
    }
  }

  private void createTag(Tag tag) {
    try {
      tagService.create(tag);
    } catch (AlreadyExistsException | IllegalValueException ex) {
      log.error("Tag {} already exists", tag.getName());
    }
  }

  private String backupIngredientNames() {
    List<IngredientName> ingredientNameList = ingredientNameService.getReadonlyIngredientNameList();
    try {
      return objectMapper.writeValueAsString(ingredientNameList);
    } catch (JsonProcessingException e) {
      log.error(FOUT_BIJ_HET_MAKEN_VAN_DE_BACKUP_FILE + INGREDIENT_NAMES_PLAN);
    }
    return "";
  }

  private void writeIngredientNamesToFile(String directoryPath, String ingredientNames) {
    File ingredientNameFile = new File(directoryPath, INGREDIENT_NAMES_PLAN);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ingredientNameFile))) {
      writer.write(ingredientNames);
    } catch (IOException ex) {
      log.error(FOUT_BIJ_HET_SCHRIJVEN_VAN_DE_BACKUP_FILE + INGREDIENT_NAMES_PLAN);
    }
  }

  private String readIngredientNamesFromFile(String directoryPath) {
    File ingredientNameFile = new File(directoryPath, INGREDIENT_NAMES_PLAN);
    if (ingredientNameFile.exists()) {
      try (BufferedReader reader = new BufferedReader(new FileReader(ingredientNameFile))) {
        return reader.readLine();
      } catch (IOException ex) {
        log.error(FOUT_BIJ_HET_LEZEN_VAN_DE_BACKUP_FILE + INGREDIENT_NAMES_PLAN);
      }
    }
    return null;
  }

  private void restoreIngredientNames(String ingredientNames) {
    try {
      List<IngredientName> ingredientNameList =
          objectMapper.readValue(ingredientNames, new TypeReference<List<IngredientName>>() {});
      for (IngredientName ingredientName : ingredientNameList) {
        ingredientName.setId(null);
        createIngredientName(ingredientName);
      }
    } catch (JsonProcessingException ex) {
      log.error(FOUT_BIJ_HET_VERWERKEN_VAN_DE_BACKUP_FILE + INGREDIENT_NAMES_PLAN);
    }
  }

  private void createIngredientName(IngredientName ingredientName) {
    try {
      ingredientNameService.create(ingredientName);
    } catch (AlreadyExistsException | IllegalValueException ex) {
      log.error("IngredientName {} already exists", ingredientName.getName());
    }
  }

  private String backupMeasureUnits() {
    List<MeasureUnit> measureUnitList = measureUnitService.getReadonlyMeasureUnitList();
    try {
      return objectMapper.writeValueAsString(measureUnitList);
    } catch (JsonProcessingException e) {
      log.error(FOUT_BIJ_HET_MAKEN_VAN_DE_BACKUP_FILE + MEASURE_UNITS_PLAN);
    }
    return "";
  }

  private void writeMeasureUnitsToFile(String directoryPath, String measureUnits) {
    File measureUnitFile = new File(directoryPath, MEASURE_UNITS_PLAN);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(measureUnitFile))) {
      writer.write(measureUnits);
    } catch (IOException ex) {
      log.error(FOUT_BIJ_HET_SCHRIJVEN_VAN_DE_BACKUP_FILE + MEASURE_UNITS_PLAN);
    }
  }

  private String readMeasureUnitsFromFile(String directoryPath) {
    File measureUnitFile = new File(directoryPath, MEASURE_UNITS_PLAN);
    if (measureUnitFile.exists()) {
      try (BufferedReader reader = new BufferedReader(new FileReader(measureUnitFile))) {
        return reader.readLine();
      } catch (IOException ex) {
        log.error(FOUT_BIJ_HET_LEZEN_VAN_DE_BACKUP_FILE + MEASURE_UNITS_PLAN);
      }
    }
    return null;
  }

  private void restoreMeasureUnits(String measureUnits) {
    try {
      List<MeasureUnit> measureUnitNameList =
          objectMapper.readValue(measureUnits, new TypeReference<List<MeasureUnit>>() {});
      for (MeasureUnit measureUnit : measureUnitNameList) {
        measureUnit.setId(null);
        createMeasureUnit(measureUnit);
      }
    } catch (JsonProcessingException ex) {
      log.error(FOUT_BIJ_HET_VERWERKEN_VAN_DE_BACKUP_FILE + MEASURE_UNITS_PLAN);
    }
  }

  private void createMeasureUnit(MeasureUnit measureUnit) {
    try {
      measureUnitService.create(measureUnit);
    } catch (AlreadyExistsException | IllegalValueException ex) {
      log.error("MeasureUnit {} already exists", measureUnit.getName());
    }
  }

  private String backupRecipes() {
    List<Recipe> recipeList = recipeService.getReadonlyRecipeList();
    try {
      return objectMapper.writeValueAsString(recipeList);
    } catch (JsonProcessingException e) {
      log.error(FOUT_BIJ_HET_MAKEN_VAN_DE_BACKUP_FILE + RECIPES_PLAN);
    }
    return "";
  }

  private void writeRecipesToFile(String directoryPath, String recipes) {
    File recipeFile = new File(directoryPath, RECIPES_PLAN);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(recipeFile))) {
      writer.write(recipes);
    } catch (IOException ex) {
      log.error(FOUT_BIJ_HET_SCHRIJVEN_VAN_DE_BACKUP_FILE + RECIPES_PLAN);
    }
  }

  private String readRecipesFromFile(String directoryPath) {
    File recipeFile = new File(directoryPath, RECIPES_PLAN);
    if (recipeFile.exists()) {
      try (BufferedReader reader = new BufferedReader(new FileReader(recipeFile))) {
        return reader.readLine();
      } catch (IOException ex) {
        log.error(FOUT_BIJ_HET_LEZEN_VAN_DE_BACKUP_FILE + RECIPES_PLAN);
      }
    }
    return null;
  }

  private void restoreRecipes(String recipes) {
    try {
      List<Recipe> recipeList =
          objectMapper.readValue(recipes, new TypeReference<List<Recipe>>() {});
      for (Recipe recipe : recipeList) {
        recipe.setId(null);
        recipe.setTags(createNewTagSet(recipe));
        recipe.setIngredients(createNewIngredientSet(recipe));
        createRecipe(recipe);
      }
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
      log.error(FOUT_BIJ_HET_VERWERKEN_VAN_DE_BACKUP_FILE + RECIPES_PLAN);
    }
  }

  private Set<Tag> createNewTagSet(Recipe recipe) {
    Set<Tag> tagList = new HashSet<>();

    for (Tag tag : recipe.getTags()) {
      Optional<Tag> optionalTag = tagService.findByName(tag.getName());
      if (optionalTag.isPresent()) {
        tagList.add(optionalTag.get());
      }
    }
    return tagList;
  }

  private Set<Ingredient> createNewIngredientSet(Recipe recipe) {
    Set<Ingredient> ingredientList = new HashSet<>();

    for (Ingredient ingredient : recipe.getIngredients()) {
      if (ingredient.getIngredientName().getMeasureUnit() != null) {
        Optional<MeasureUnit> optionalMeasureUnit =
            measureUnitService.findByName(ingredient.getIngredientName().getMeasureUnit().getName());
        if (optionalMeasureUnit.isPresent()) {
          ingredient.getIngredientName().setMeasureUnit(optionalMeasureUnit.get());
        }
      }

      Optional<IngredientName> optionalIngredientName =
          ingredientNameService.findByName(ingredient.getIngredientName().getName());
      if (optionalIngredientName.isPresent()) {
        ingredient.setIngredientName(optionalIngredientName.get());
      }
      ingredient.setId(null);
      ingredientList.add(ingredient);
    }
    return ingredientList;
  }

  private Recipe createRecipe(Recipe recipe) {
    try {
      return recipeService.create(recipe);
    } catch (AlreadyExistsException ex) {
      log.error("Recipe {} already exists", recipe.getName());
      return null;
    }
  }
  
  private String backupShoppingItems() {
    List<ShoppingItem> shoppingItemList = shoppingItemService.getShoppingItemList();
    try {
      return objectMapper.writeValueAsString(shoppingItemList);
    } catch (JsonProcessingException e) {
      log.error(FOUT_BIJ_HET_MAKEN_VAN_DE_BACKUP_FILE + SHOPPINGITEMS_PLAN);
    }
    return "";
  }

  private void writeShoppingItemsToFile(String directoryPath, String shoppingItems) {
    File shoppingItemFile = new File(directoryPath, SHOPPINGITEMS_PLAN);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(shoppingItemFile))) {
      writer.write(shoppingItems);
    } catch (IOException ex) {
      log.error(FOUT_BIJ_HET_SCHRIJVEN_VAN_DE_BACKUP_FILE + SHOPPINGITEMS_PLAN);
    }
  }

  private String readShoppingItemsFromFile(String directoryPath) {
    File shoppingItemFile = new File(directoryPath, SHOPPINGITEMS_PLAN);
    if (shoppingItemFile.exists()) {
      try (BufferedReader reader = new BufferedReader(new FileReader(shoppingItemFile))) {
        return reader.readLine();
      } catch (IOException ex) {
        log.error(FOUT_BIJ_HET_LEZEN_VAN_DE_BACKUP_FILE + SHOPPINGITEMS_PLAN);
      }
    }
    return null;
  }

  private void restoreShoppingItems(String shoppingItems) {
    try {
      List<ShoppingItem> shoppingItemList = objectMapper.readValue(shoppingItems, new TypeReference<List<ShoppingItem>>() {});
      for (ShoppingItem shoppingItem : shoppingItemList) {
        shoppingItem.setId(null);
        createShoppingItem(shoppingItem);
      }
    } catch (JsonProcessingException ex) {
      log.error(FOUT_BIJ_HET_VERWERKEN_VAN_DE_BACKUP_FILE + SHOPPINGITEMS_PLAN);
    }
  }

  private void createShoppingItem(ShoppingItem shoppingItem) {
    try {
      shoppingItemService.create(shoppingItem);
    } catch (AlreadyExistsException | IllegalValueException ex) {
      log.error("Tag {} already exists", shoppingItem.getIngredientName().getName());
    }
  }

  private void removeOldBackups(String directoryPath) {
    Integer backupsToKeep = Integer.valueOf(configService.getConfigProperty(BACKUPS_TO_KEEP));
    try (Stream<Path> folders = Files.walk(Path.of(directoryPath))) {
      folders.filter(Files::isDirectory)
          .filter(p -> p.toString().length() != directoryPath.length())
          .sorted(Comparator.reverseOrder()).skip(backupsToKeep).forEach(this::deleteFolder);

    } catch (IOException ex) {
      log.error("Error during removal of expired backups{}", ex.getMessage());
    }
  }

  private void deleteFolder(Path path) {
    File directory = new File(path.toString());
    try {
      FileUtils.deleteDirectory(directory);
    } catch (IOException ex) {
      log.error("Error while deleting expired backups{}", ex.getMessage());
    }
  }

}
