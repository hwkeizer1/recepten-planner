package nl.recipes.views.recipes;

import static nl.recipes.views.ViewConstants.CSS_VALIDATION;
import static nl.recipes.views.ViewConstants.CSS_WIDGET;
import static nl.recipes.views.ViewMessages.IMAGE_FOLDER;
import static nl.recipes.views.ViewMessages.SERVINGS_MUST_BE_MORE_THEN_NULL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import org.springframework.stereotype.Component;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.ConfigService;
import nl.recipes.services.ImageService;
import nl.recipes.services.RecipeService;
import nl.recipes.services.TagService;
import nl.recipes.views.root.RootView;

@Slf4j
@Component
public class RecipeEditView {

	private final RecipeService recipeService;
	private final TagService tagService;
	private final ImageService imageService;
	private final ConfigService configService;

	private final IngredientEditView ingredientEditView;

	private RootView rootView;

	private Recipe selectedRecipe;

	ScrollPane scrollPanel;

	Button createButton;

	Button updateButton;

	// Column 1 and 2
	ImageView imageView = new ImageView();

	TextField recipeName;
	Label recipeNameError;

	TextField preparationTime;

	TextField cookTime;

	TextField servings;
	Label servingsError;

	TextArea preparations;

	TextArea notes;

	List<CheckBox> tagCheckBoxes;

	ComboBox<RecipeType> recipeTypeComboBox;

	TextField rating;

	TextArea directions;

	public RecipeEditView(RecipeService recipeService, TagService tagService, IngredientEditView ingredientEditView,
			ImageService imageService, ConfigService configService) {
		this.recipeService = recipeService;
		this.tagService = tagService;
		this.imageService = imageService;
		this.configService = configService;
		this.ingredientEditView = ingredientEditView;

		GridPane recipeForm = createRecipeForm();
		initialiseFormControls(recipeForm);

		VBox panel = new VBox();
		panel.getStyleClass().add(CSS_WIDGET);
		panel.getChildren().addAll(recipeForm, initialiseButtons());

		scrollPanel = new ScrollPane(panel);
		scrollPanel.setFitToWidth(true);
	}

	public void setRootView(RootView rootView) {
		this.rootView = rootView;
	}

	public Node getRecipeUpdateViewPanel(Recipe recipe) {
		selectedRecipe = recipe;
		imageView = imageService.loadRecipeImage(imageView, selectedRecipe);
		selectedRecipeToFormFields();

		createButton.setVisible(false);
		updateButton.setVisible(true);

		return scrollPanel;
	}

	public Node getRecipeCreateViewPanel() {
		selectedRecipe = new Recipe.RecipeBuilder().build();
		imageView = imageService.loadRecipeImage(imageView, selectedRecipe);
		selectedRecipeToFormFields();

		createButton.setVisible(true);
		updateButton.setVisible(false);

		return scrollPanel;
	}

	private GridPane createRecipeForm() {

		GridPane recipeForm = new GridPane();
		recipeForm.setPadding(new Insets(10, 0, 0, 0));
		recipeForm.setHgap(20);
		recipeForm.setVgap(10);

		ColumnConstraints column0 = new ColumnConstraints();
		column0.setPercentWidth(10);
		column0.setHalignment(HPos.RIGHT);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(30);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(10);
		column2.setHalignment(HPos.RIGHT);
		ColumnConstraints column3 = new ColumnConstraints();
		column3.setPercentWidth(30);
		recipeForm.getColumnConstraints().addAll(column0, column1, column2, column3);

		return recipeForm;
	}

	private void initialiseFormControls(GridPane recipeForm) {

		// Column 1 and 2
		Label nameLabel = new Label("Recept naam:");
		nameLabel.setPadding(new Insets(10, 0, 0, 0));
		GridPane.setValignment(nameLabel, VPos.TOP);
		VBox recipeNameWithValidation = new VBox();
		recipeNameWithValidation.setPadding(new Insets(10, 0, 0, 0));
		recipeName = new TextField();
		recipeNameError = new Label();
		recipeNameWithValidation.getChildren().addAll(recipeName, recipeNameError);
		recipeForm.add(nameLabel, 0, 0);
		recipeForm.add(recipeNameWithValidation, 1, 0);
		recipeName.setOnKeyReleased(this::handleRecipeNameKeyReleasedAction);
		recipeNameError.getStyleClass().add(CSS_VALIDATION);

		Label preparationTimeLabel = new Label("Voorbereidingstijd:");
		preparationTime = new TextField();
		preparationTime.setMaxWidth(100);
		preparationTime.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.matches("\\d*"))
				return;
			preparationTime.setText(newValue.replaceAll("[^\\d]", ""));
		});
		recipeForm.add(preparationTimeLabel, 0, 1);
		recipeForm.add(preparationTime, 1, 1);

		Label cookTimeLabel = new Label("Bereidingstijd:");
		cookTime = new TextField();
		cookTime.setMaxWidth(100);
		cookTime.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.matches("\\d*"))
				return;
			cookTime.setText(newValue.replaceAll("[^\\d]", ""));
		});
		recipeForm.add(cookTimeLabel, 0, 2);
		recipeForm.add(cookTime, 1, 2);

		Label servingsLabel = new Label("Aantal personen:");
		GridPane.setValignment(servingsLabel, VPos.TOP);
		VBox servingsWithValidation = new VBox();
		servings = new TextField();
		servingsError = new Label();
		servingsError.getStyleClass().add(CSS_VALIDATION);
		servings.setMaxWidth(100);
		servings.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.matches("\\d*")) {
				servingsError.setText(null);
				return;
			}
			servings.setText(newValue.replaceAll("[^\\d]", ""));
		});
		servingsWithValidation.getChildren().addAll(servings, servingsError);
		recipeForm.add(servingsLabel, 0, 3);
		recipeForm.add(servingsWithValidation, 1, 3);

		Label preparationsLabel = new Label("Voorbereiding:");
		GridPane.setValignment(preparationsLabel, VPos.TOP);
		preparations = new TextArea();
		recipeForm.add(preparationsLabel, 0, 4);
		recipeForm.add(preparations, 1, 4);

		Label directionsLabel = new Label("Bereiding:");
		GridPane.setValignment(directionsLabel, VPos.TOP);
		directions = new TextArea();
		recipeForm.add(directionsLabel, 0, 5);
		recipeForm.add(directions, 1, 5);

		recipeForm.add(imageView, 1, 6);
		Label recipeImageLabel = new Label("Afbeelding:");
		recipeForm.add(recipeImageLabel, 0, 7);

		Button selectImageButton = new Button("selecteer een afbeelding");
		selectImageButton.setOnAction(this::selectImage);
		recipeForm.add(selectImageButton, 1, 8);

		// Column 2 and 3
		Label tagsLabel = new Label("Categorieën:");
		tagsLabel.setPadding(new Insets(10, 0, 0, 0));
		recipeForm.add(tagsLabel, 2, 0);
		recipeForm.add(createTagForm(), 3, 0);
		GridPane.setValignment(tagsLabel, VPos.TOP);

		Label recipeTypeLabel = new Label("Recept type:");
		recipeTypeComboBox = new ComboBox<>();
		recipeTypeComboBox.getItems().setAll(RecipeType.values());
		recipeTypeComboBox.setMinWidth(150);
		recipeForm.add(recipeTypeLabel, 2, 1);
		recipeForm.add(recipeTypeComboBox, 3, 1);

		Label ratingLabel = new Label("Waardering (1-5):");
		rating = new TextField();
		rating.setMaxWidth(100);
		UnaryOperator<Change> ratingTextFilter = c -> {
			if (c.getText().matches("[1-5]")) {
				c.setRange(0, rating.getText().length());
				return c;
			} else if (c.getText().isEmpty()) {
				return c;
			}
			return null;
		};
		TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter(), 0, ratingTextFilter);
		rating.setTextFormatter(formatter);
		recipeForm.add(ratingLabel, 2, 2);
		recipeForm.add(rating, 3, 2);

		Label notesLabel = new Label("Notities:");
		GridPane.setValignment(notesLabel, VPos.TOP);
		notes = new TextArea();
		recipeForm.add(notesLabel, 2, 4);
		recipeForm.add(notes, 3, 4);

		Label ingredientLabel = new Label("Ingrediënten:");
		GridPane.setValignment(ingredientLabel, VPos.TOP);
		recipeForm.add(ingredientLabel, 2, 5);
		recipeForm.add(ingredientEditView.getIngredientPanel(), 3, 5);
	}

	private void selectImage(ActionEvent actionEvent) {
		if (selectedRecipe.getName() == null || selectedRecipe.getName().isEmpty()) {
			showRecipeNameRequiredForImageSelection();
			return;
		}
		Optional<Recipe> recipe = recipeService.findByName(selectedRecipe.getName());
		if (recipe.isPresent() && createButton.isVisible()) {
			showRecipeNameInvalidForImageSelection();
			return;
		}

		FileChooser fileChooser = new FileChooser();
		if (configService.getConfigProperty(IMAGE_FOLDER) != null
				&& !configService.getConfigProperty(IMAGE_FOLDER).isBlank()) {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		}
		Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
		File newImageFile = fileChooser.showOpenDialog(stage);

		if (newImageFile != null) {
			if (newImageFile.getParent().equals(configService.getConfigProperty(IMAGE_FOLDER))) {
				showSelectFromImageFolderError();
			} else {
				try {
					selectedRecipe.setImage(
							imageService.selectImage(selectedRecipe.getName(), newImageFile.getAbsolutePath()));
					imageService.loadRecipeImage(imageView, selectedRecipe);
				} catch (IOException e) {
					log.error("Could not select the image, need further error handling");
				}

			}
		}
	}

	private Node initialiseButtons() {
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		buttonBar.setPadding(new Insets(40, 40, 0, 0));

		updateButton = new Button("Wijzigingen opslaan");
		updateButton.setOnAction(this::updateRecipe);

		createButton = new Button("Nieuw recept opslaan");
		createButton.setOnAction(this::createRecipe);

		buttonBar.getButtons().addAll(createButton, updateButton);
		return buttonBar;
	}

	private Node createTagForm() {
		GridPane tagForm = new GridPane();
		tagForm.setHgap(10);
		tagForm.setVgap(10);

		int tagsOnRow = 5;
		int tagCol = 0;
		int tagRow = 0;
		tagCheckBoxes = new ArrayList<>();
		for (Tag tag : tagService.getList()) {
			CheckBox checkBox = new CheckBox(tag.getName());
			checkBox.setId(tag.getName());
			tagCheckBoxes.add(checkBox);
			if (tagCol % (tagsOnRow + 1) != 0) {
				tagForm.add(checkBox, tagCol, tagRow);
				tagCol++;
			} else {
				tagRow++;
				tagCol = 1;
				tagForm.add(checkBox, tagCol, tagRow);
				tagCol++;
			}
		}
		return tagForm;
	}

	private void createRecipe(ActionEvent event) {
		selectedRecipe = formFieldsToRecipe();
		if (!imageService.validateImageName(selectedRecipe)) {
			String newFileName = imageService.renameImageFileName(selectedRecipe.getImage(), selectedRecipe.getName());
			selectedRecipe.setImage(newFileName);
		}
		if (isLocalValid(selectedRecipe)) {
			try {
				Recipe createdRecipe = recipeService.create(selectedRecipe);
				rootView.showRecipeSingleViewPanel(createdRecipe);
			} catch (AlreadyExistsException e) {
				recipeNameError.setText(e.getMessage());
			}
		}
	}

	private void updateRecipe(ActionEvent event) {
		Recipe update = formFieldsToRecipe();
		if (!imageService.validateImageName(selectedRecipe)) {
			String newFileName = imageService.renameImageFileName(selectedRecipe.getImage(), selectedRecipe.getName());
			update.setImage(newFileName);
		}
		if (isLocalValid(update)) {

			try {
				Recipe updatedRecipe = recipeService.update(selectedRecipe, update);
				rootView.showRecipeSingleViewPanel(updatedRecipe);
			} catch (NotFoundException | AlreadyExistsException e) {
				recipeNameError.setText(e.getMessage());
			}
		}
	}

	private boolean isLocalValid(Recipe recipe) {
		boolean valid = true;
		if (recipe.getServings() == null || recipe.getServings() <= 0) {
			servingsError.setText(SERVINGS_MUST_BE_MORE_THEN_NULL);
			valid = false;
		}
		return valid;
	}

	private void handleRecipeNameKeyReleasedAction(KeyEvent keyEvent) {
		recipeNameError.setText(null);
	}

	private Recipe formFieldsToRecipe() {
		return new Recipe.RecipeBuilder().withName(recipeName.getText())
				.withPreparationTime(
						(preparationTime.getText().isEmpty()) ? null : Integer.valueOf(preparationTime.getText()))
				.withCookTime((cookTime.getText().isEmpty()) ? null : Integer.valueOf(cookTime.getText()))
				.withServings((servings.getText().isEmpty()) ? null : Integer.valueOf(servings.getText()))
				.withPreparations(preparations.getText()).withDirections(directions.getText())
				.withIngredients(new HashSet<>(ingredientEditView.getIngredientList())).withTags(getSelectedTags())
				.withRecipeType(recipeTypeComboBox.getValue())
				.withRating((rating.getText().isEmpty()) ? null : Integer.valueOf(rating.getText()))
				.withImage(selectedRecipe.getImage()).withNotes(notes.getText()).build();
	}

	private Set<Tag> getSelectedTags() {
		Set<Tag> tags = new HashSet<>();
		for (CheckBox checkBox : tagCheckBoxes) {
			if (checkBox.isSelected()) {
				Optional<Tag> optionalTag = tagService.findByName(checkBox.getId());
				if (optionalTag.isPresent()) {
					tags.add(optionalTag.get());
				}
			}
		}
		return tags;
	}

	private void selectedRecipeToFormFields() {

		// Column 1 and 2
		recipeName.setText(selectedRecipe.getName());
		preparationTime.setText(
				(selectedRecipe.getPreparationTime() == null) ? "" : selectedRecipe.getPreparationTime().toString());
		cookTime.setText((selectedRecipe.getCookTime() == null) ? "" : selectedRecipe.getCookTime().toString());
		servings.setText((selectedRecipe.getServings() == null) ? "" : selectedRecipe.getServings().toString());
		preparations.setText(selectedRecipe.getPreparations());
		directions.setText(selectedRecipe.getDirections());
		ingredientEditView.setIngredientList(recipeService.getEditableIngredientList(selectedRecipe.getId()));

		// column 3 and 4
		for (CheckBox checkBox : tagCheckBoxes) {
			boolean select = false;
			for (Tag tag : selectedRecipe.getTags()) {
				if (tag.getName().equals(checkBox.getId())) {
					select = true;
				}
			}
			checkBox.setSelected(select);
		}

		recipeTypeComboBox.setValue(selectedRecipe.getRecipeType());
		rating.setText((selectedRecipe.getRating() == null) ? "" : selectedRecipe.getRating().toString());
		notes.setText(selectedRecipe.getNotes());

	}

	private void showSelectFromImageFolderError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.setTitle("Fout: Deze afbeelding kan niet worden geselecteerd.");
		alert.setHeaderText("Afbeeldingen uit '" + configService.getConfigProperty(IMAGE_FOLDER) + "' zijn al in \n"
				+ "gebruik en kunnen helaas niet worden geselecteerd als nieuwe afbeelding.\n\n"
				+ "Selecteer astublieft een afbeelding van een andere lokatie.");
		alert.initOwner(scrollPanel.getScene().getWindow());
		alert.showAndWait();
	}

	private void showRecipeNameRequiredForImageSelection() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.setTitle("Fout: Kan geen afbeelding selecteren.");
		alert.setHeaderText(
				"Het recept dient eerst een naam te hebben voordat een afbeelding kan worden geselecteerd.");
		alert.initOwner(scrollPanel.getScene().getWindow());
		alert.showAndWait();
	}

	private void showRecipeNameInvalidForImageSelection() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.setTitle("Fout: Kan geen afbeelding selecteren.");
		alert.setHeaderText(
				"De receptnaam die je gebruikt bestaat al. Kies eerst een unieke naam voor het recept voordat\n"
						+ "je een afbeelding selecteerd.");
		alert.initOwner(scrollPanel.getScene().getWindow());
		alert.showAndWait();
	}
}
