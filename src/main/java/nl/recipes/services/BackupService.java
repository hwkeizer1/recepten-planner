package nl.recipes.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;

@Slf4j
@Service
public class BackupService {

	private static final String FOUT_BIJ_HET_VERWERKEN_VAN_DE_BACKUP_FILE = "Fout bij het verwerken van de backup file ";
	private static final String FOUT_BIJ_HET_MAKEN_VAN_DE_BACKUP_FILE = "Fout bij het maken van de backup file ";
	private static final String FOUT_BIJ_HET_LEZEN_VAN_DE_BACKUP_FILE = "Fout bij het lezen van de backup file ";
	private static final String FOUT_BIJ_HET_SCHRIJVEN_VAN_DE_BACKUP_FILE = "Fout bij het schrijven van de backup file ";
	private static final String TAGS_PLAN = "tags.plan";
	private static final String INGREDIENT_NAMES_PLAN = "ingredientnames.plan";
	private static final String MEASURE_UNITS_PLAN = "measureunits.plan";
	private static final String RECIPES_PLAN = "recipes.plan";

	private final TagService tagService;
	private final IngredientNameService ingredientNameService;
	private final MeasureUnitService measureUnitService;
	private final RecipeService recipeService;
	private final ObjectMapper objectMapper;

	public BackupService(TagService tagService, IngredientNameService ingredientNameService, MeasureUnitService measureUnitService, RecipeService recipeService) {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		this.tagService = tagService;
		this.ingredientNameService = ingredientNameService;
		this.measureUnitService = measureUnitService;
		this.recipeService = recipeService;
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
	}
	
	public void backup(String directoryPath) {
		
		String tags = backupTags();
		if (tags != null) {
			writeTagsToFile(directoryPath, tags);
		}
		String ingredientNames = backupIngredientNames();
		if (ingredientNames != null) {
			writeIngredientNamesToFile(directoryPath, ingredientNames);
		}
		String measureUnits = backupMeasureUnits();
		if (measureUnits != null) {
			writeMeasureUnitsToFile(directoryPath, measureUnits);
		}
		String recipes = backupRecipes();
		if (recipes != null) {
			writeRecipesToFile(directoryPath, recipes);
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
	
	private void writeTagsToFile(String directoryPath, String tags)  {
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
	
	private void writeIngredientNamesToFile(String directoryPath, String ingredientNames)  {
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
			List<IngredientName> ingredientNameList = objectMapper.readValue(ingredientNames, new TypeReference<List<IngredientName>>() {});
			for (IngredientName ingredientName : ingredientNameList) {
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
	
	private void writeMeasureUnitsToFile(String directoryPath, String measureUnits)  {
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
			List<MeasureUnit> measureUnitNameList = objectMapper.readValue(measureUnits, new TypeReference<List<MeasureUnit>>() {});
			for (MeasureUnit measureUnit : measureUnitNameList) {
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
	
	private void writeRecipesToFile(String directoryPath, String recipes)  {
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
			List<Recipe> recipeList = objectMapper.readValue(recipes, new TypeReference<List<Recipe>>() {});
			for (Recipe recipe : recipeList) {
				recipe.setIngredients(createNewIngredientSet(recipe));
				createRecipe(recipe);
			}
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
			log.error(FOUT_BIJ_HET_VERWERKEN_VAN_DE_BACKUP_FILE + RECIPES_PLAN);
		}
	}
	
	private Set<Ingredient> createNewIngredientSet(Recipe recipe) {
		Set<Ingredient> ingredientList = new HashSet<>();

		for (Ingredient ingredient : recipe.getIngredients()) {
			Optional<MeasureUnit> optionalMeasureUnit = measureUnitService.findByName(ingredient.getMeasureUnit().getName());
			Optional<IngredientName> optionalIngredientName = ingredientNameService.findByName(ingredient.getIngredientName().getName());
			if (optionalMeasureUnit.isPresent() && optionalIngredientName.isPresent()) {
				ingredient.setMeasureUnit(optionalMeasureUnit.get());
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
}
