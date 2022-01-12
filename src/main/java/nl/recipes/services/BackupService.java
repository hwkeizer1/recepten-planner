package nl.recipes.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

	private static final String TAGS_PLAN = "tags.plan";
	private static final String INGREDIENT_NAMES_PLAN = "ingredientnames.plan";
	private static final String MEASURE_UNIT_PLAN = "measureunits.plan";
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
		restoreTags(tags);
		String ingredientNames = readIngredientNamesFromFile(directoryPath);
		restoreIngredientNames(ingredientNames);
		String measureUnits = readMeasureUnitsFromFile(directoryPath);
		restoreMeasureUnits(measureUnits);
		String recipes = readRecipesFromFile(directoryPath);
		restoreRecipes(recipes);
	}

	private String readTagsFromFile(String directoryPath) {
		File tagFile = new File(directoryPath, TAGS_PLAN);

		try (BufferedReader reader = new BufferedReader(new FileReader(tagFile))) {
			return reader.readLine();
		} catch (IOException ex) {
			log.error("Fout bij het lezen van de backup file " + TAGS_PLAN);
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
			log.error("Fout bij het terugzetten van de backup file " + TAGS_PLAN);
		}
	}

	private void createTag(Tag tag) {
		try {
			tagService.create(tag);
		} catch (AlreadyExistsException | IllegalValueException ex) {
			log.error("Tag {} already exists", tag.getName());
		}
	}
	
	private String readIngredientNamesFromFile(String directoryPath) {
		File ingredientNameFile = new File(directoryPath, INGREDIENT_NAMES_PLAN);

		try (BufferedReader reader = new BufferedReader(new FileReader(ingredientNameFile))) {
			return reader.readLine();
		} catch (IOException ex) {
			log.error("Fout bij het lezen van de backup file " + INGREDIENT_NAMES_PLAN);
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
			log.error("Fout bij het terugzetten van de backup file " + INGREDIENT_NAMES_PLAN);
		}
	}

	private void createIngredientName(IngredientName ingredientName) {
		try {
			ingredientNameService.create(ingredientName);
		} catch (AlreadyExistsException | IllegalValueException ex) {
			log.error("IngredientName {} already exists", ingredientName.getName());
		}
	}
	
	private String readMeasureUnitsFromFile(String directoryPath) {
		File measureUnitFile = new File(directoryPath, MEASURE_UNIT_PLAN);

		try (BufferedReader reader = new BufferedReader(new FileReader(measureUnitFile))) {
			return reader.readLine();
		} catch (IOException ex) {
			log.error("Fout bij het lezen van de backup file " + MEASURE_UNIT_PLAN);
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
			log.error("Fout bij het terugzetten van de backup file " + MEASURE_UNIT_PLAN);
		}
	}

	private void createMeasureUnit(MeasureUnit measureUnit) {
		try {
			measureUnitService.create(measureUnit);
		} catch (AlreadyExistsException | IllegalValueException ex) {
			log.error("MeasureUnit {} already exists", measureUnit.getName());
		}
	}
	
	private String readRecipesFromFile(String directoryPath) {
		File recipeFile = new File(directoryPath, RECIPES_PLAN);

		try (BufferedReader reader = new BufferedReader(new FileReader(recipeFile))) {
			return reader.readLine();
		} catch (IOException ex) {
			log.error("Fout bij het lezen van de backup file " + RECIPES_PLAN);
		}
		return null;
	}

	private void restoreRecipes(String recipes) {
		try {
			List<Recipe> recipeList = objectMapper.readValue(recipes, new TypeReference<List<Recipe>>() {});
			for (Recipe recipe : recipeList) {
				Set<Ingredient> ingredientList = recipe.getIngredients();
				// Ensure correct measureUnit and ingredientName id's are used when switching from MySQL database with different id's
				for (Ingredient ingredient : ingredientList) {
					Optional<MeasureUnit> optionalMeasureUnit = measureUnitService.findByName(ingredient.getMeasureUnit().getName());
					Optional<IngredientName> optionalIngredientName = ingredientNameService.findByName(ingredient.getIngredientName().getName());
					if (optionalMeasureUnit.isPresent() && optionalIngredientName.isPresent()) {
						ingredient.setMeasureUnit(optionalMeasureUnit.get());
						ingredient.setIngredientName(optionalIngredientName.get());
					}
				}
				createRecipe(recipe);
			}
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
			log.error("Fout bij het terugzetten van de backup file " + RECIPES_PLAN);
		}
	}

	private void createRecipe(Recipe recipe) {
		try {
			recipeService.create(recipe);
		} catch (AlreadyExistsException ex) {
			log.error("Recipe {} already exists", recipe.getName());
		}
	}

}
