package nl.recipes.views.recipes;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.services.RecipeService;
import nl.recipes.services.TagService;
import nl.recipes.views.root.RootView;

import static nl.recipes.views.ViewConstants.*;

@Component
public class RecipeEditView {

  private final RecipeService recipeService;

  private final TagService tagService;

  private final IngredientEditView ingredientEditView;

  private RootView rootView;

  private Recipe selectedRecipe;

  ScrollPane scrollPanel;

  Button createButton;

  Button updateButton;

  // Column 1 and 2
  TextField recipeName;

  Label recipeNameError;

  TextField preparationTime;

  TextField cookTime;

  TextField servings;

  TextArea preparations;

  TextArea notes;

  List<CheckBox> tagCheckBoxes;

  ComboBox<RecipeType> recipeTypeComboBox;

  TextField rating;

  TextArea directions;

  public RecipeEditView(RecipeService recipeService, TagService tagService,
      IngredientEditView ingredientEditView) {
    this.recipeService = recipeService;
    this.tagService = tagService;
    this.ingredientEditView = ingredientEditView;

    GridPane recipeForm = createRecipeForm();
    initialiseFormControls(recipeForm);

    VBox panel = new VBox();
    panel.getStyleClass().add(WIDGET);
    panel.getChildren().addAll(recipeForm, initialiseButtons());

    scrollPanel = new ScrollPane(panel);
    scrollPanel.setFitToWidth(true);
  }

  public void setRootView(RootView rootView) {
    this.rootView = rootView;
  }

  public Node getRecipeEditViewPanel(Recipe recipe) {
    selectedRecipe = recipe;
    selectedRecipeToFormFields();

    createButton.setVisible(false);
    updateButton.setVisible(true);

    return scrollPanel;
  }

  public Node getNewRecipeEditViewPanel() {
    selectedRecipe = new Recipe();
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
    // recipeForm.setGridLinesVisible(true);

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
    recipeName.setOnKeyReleased(this::handleKeyReleasedAction);
    recipeNameError.getStyleClass().add(VALIDATION);

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
    servings = new TextField();
    servings.setMaxWidth(100);
    servings.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.matches("\\d*"))
        return;
      servings.setText(newValue.replaceAll("[^\\d]", ""));
    });
    recipeForm.add(servingsLabel, 0, 3);
    recipeForm.add(servings, 1, 3);

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
    TextFormatter<Integer> formatter =
        new TextFormatter<>(new IntegerStringConverter(), 0, ratingTextFilter);
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
    for (Tag tag : tagService.getReadonlyTagList()) {
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
    formFieldsToSelectedRecipe();

    try {
      Recipe createdRecipe = recipeService.create(selectedRecipe);
      rootView.showRecipeSingleViewPanel(createdRecipe);
    } catch (AlreadyExistsException e) {
      recipeNameError.setText(e.getMessage());
    }
  }

  private void updateRecipe(ActionEvent event) {
    formFieldsToSelectedRecipe();
    Recipe updatedRecipe = recipeService.update(selectedRecipe);
    rootView.showRecipeSingleViewPanel(updatedRecipe);
  }

  private void handleKeyReleasedAction(KeyEvent keyEvent) {
    recipeNameError.setText(null);
  }

  private void formFieldsToSelectedRecipe() {

    // Column 1 and 2
    selectedRecipe.setName(recipeName.getText());
    selectedRecipe.setPreparationTime(
        (preparationTime.getText().isEmpty()) ? null : Integer.valueOf(preparationTime.getText()));
    selectedRecipe
        .setCookTime((cookTime.getText().isEmpty()) ? null : Integer.valueOf(cookTime.getText()));
    selectedRecipe
        .setServings((servings.getText().isEmpty()) ? null : Integer.valueOf(servings.getText()));
    selectedRecipe.setPreparations(preparations.getText());
    selectedRecipe.setDirections(directions.getText());

    Set<Ingredient> ingredientSet = new HashSet<>(ingredientEditView.getIngredientList());
    selectedRecipe.setIngredients(ingredientSet);

    // Column 3 and 4
    Set<Tag> tags = new HashSet<>();
    for (CheckBox checkBox : tagCheckBoxes) {
      if (checkBox.isSelected()) {
        Optional<Tag> optionalTag = tagService.findByName(checkBox.getId());
        if (optionalTag.isPresent()) {
          tags.add(optionalTag.get());
        }
      }
    }
    selectedRecipe.setTags(tags);
    selectedRecipe.setRecipeType(recipeTypeComboBox.getValue());
    selectedRecipe
        .setRating((rating.getText().isEmpty()) ? null : Integer.valueOf(rating.getText()));
    selectedRecipe.setNotes(notes.getText());
  }

  private void selectedRecipeToFormFields() {

    // Column 1 and 2
    recipeName.setText(selectedRecipe.getName());
    preparationTime.setText((selectedRecipe.getPreparationTime() == null) ? ""
        : selectedRecipe.getPreparationTime().toString());
    cookTime.setText(
        (selectedRecipe.getCookTime() == null) ? "" : selectedRecipe.getCookTime().toString());
    servings.setText(
        (selectedRecipe.getServings() == null) ? "" : selectedRecipe.getServings().toString());
    preparations.setText(selectedRecipe.getPreparations());
    directions.setText(selectedRecipe.getDirections());
    ingredientEditView
        .setIngredientList(recipeService.getEditableIngredientList(selectedRecipe.getId()));

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
    rating
        .setText((selectedRecipe.getRating() == null) ? "" : selectedRecipe.getRating().toString());
    notes.setText(selectedRecipe.getNotes());

  }

}
