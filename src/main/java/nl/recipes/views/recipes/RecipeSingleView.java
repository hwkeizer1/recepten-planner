package nl.recipes.views.recipes;

import static nl.recipes.views.ViewConstants.CSS_DROP_SHADOW;
import static nl.recipes.views.ViewConstants.CSS_INGREDIENT_TABLE;
import static nl.recipes.views.ViewConstants.CSS_RECIPE_TITLE;
import static nl.recipes.views.ViewConstants.CSS_TITLE;
import static nl.recipes.views.ViewConstants.CSS_WIDGET;
import java.io.IOException;
import java.util.Optional;
import org.springframework.stereotype.Component;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Recipe;
import nl.recipes.services.ImageService;
import nl.recipes.services.PlanningService;
import nl.recipes.services.RecipeService;
import nl.recipes.views.components.pane.bootstrap.BootstrapColumn;
import nl.recipes.views.components.pane.bootstrap.BootstrapPane;
import nl.recipes.views.components.pane.bootstrap.BootstrapRow;
import nl.recipes.views.components.pane.bootstrap.Breakpoint;
import nl.recipes.views.root.RootView;

@Slf4j
@Component
public class RecipeSingleView {

  private final RecipeService recipeService;
  private final PlanningService planningService;
  private final ImageService imageService;

  private RootView rootView;

  private Recipe selectedRecipe;

  ScrollPane scrollPane;

  BootstrapPane root;

  ImageView imageView = new ImageView();

  Label recipeName;

  Label tagString = new Label();

  Label recipeType = new Label();

  Label preparationTime = new Label();

  Label cookTime = new Label();

  Label servings = new Label();

  Label timesServed = new Label();

  Label lastTimeServed = new Label();

  Label rating = new Label();

  TableView<Ingredient> ingredientTableView = new TableView<>();

  TableColumn<Ingredient, Number> amountColumn = new TableColumn<>();

  TableColumn<Ingredient, String> measureUnitColumn = new TableColumn<>();

  TableColumn<Ingredient, String> ingredientNameColumn = new TableColumn<>();

  TableColumn<Ingredient, Boolean> stockColumn = new TableColumn<>();

  Label notes = new Label();

  Label preparations = new Label();

  Label directions = new Label();

  public RecipeSingleView(RecipeService recipeService, PlanningService planningService, ImageService imageService) {
    this.recipeService = recipeService;
    this.planningService = planningService;
    this.imageService = imageService;

    root = makeView();

    scrollPane = new ScrollPane(root);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
  }

  public void setRootView(RootView rootView) {
    this.rootView = rootView;
  }

  public Node getRecipeSingleViewPanel(Recipe recipe) {
    selectedRecipe = recipe;
    recipeName.setText(recipe.getName());

    tagString.setText(recipe.getTagString());
    recipeType.setText(recipe.getRecipeType().getDisplayName());
    preparationTime.setText(recipe.getPreparationTime() == null ? "-" : recipe.getPreparationTime().toString());
    cookTime.setText(recipe.getCookTime() == null ? "-" : recipe.getCookTime().toString());
    servings.setText(recipe.getServings() == null ? "-" : recipe.getServings().toString());
    timesServed.setText(recipe.getTimesServed() == null ? "-" : recipe.getTimesServed().toString());
    lastTimeServed.setText(recipe.getLastServed() == null ? "-" : recipe.getLastServed().toString());
    rating.setText(recipe.getRating() == null ? "-" : recipe.getRating().toString());
    imageView = imageService.loadRecipeImage(imageView, recipe);

    ingredientTableView.setItems(recipeService.getReadonlyIngredientList(recipe.getId()));
    ingredientTableView.setFixedCellSize(25);
    ingredientTableView.prefHeightProperty()
        .bind(Bindings.size(ingredientTableView.getItems()).multiply(ingredientTableView.getFixedCellSize()).add(4));
    ingredientTableView.minHeightProperty().bind(ingredientTableView.prefHeightProperty());
    ingredientTableView.maxHeightProperty().bind(ingredientTableView.prefHeightProperty());

    notes.setText(recipe.getNotes());
    preparations.setText(recipe.getPreparations());
    directions.setText(recipe.getDirections());

    return scrollPane;
  }



  private BootstrapPane makeView() {
    BootstrapPane bootstrapPane = new BootstrapPane();
    bootstrapPane.setPadding(new Insets(15));
    bootstrapPane.getStyleClass().add("background");
    bootstrapPane.setVgap(25);
    bootstrapPane.setHgap(25);

    BootstrapRow row = new BootstrapRow();
    row.addColumn(createTitleColumn());
    row.addColumn(createColumn(createFeaturesWidget("Kenmerken")));
    row.addColumn(createColumn(createIngredientTableWidget("Ingrediënten")));
    row.addColumn(createColumn(createTextWidget("Notities", notes)));
    row.addColumn(createLargeColumn(createTextWidget("Voorbereiding", preparations)));
    row.addColumn(createLargeColumn(createTextWidget("Bereiding", directions)));

    bootstrapPane.addRow(row);

    return bootstrapPane;
  }

  private BootstrapColumn createTitleColumn() {
    HBox header = new HBox();
    header.setSpacing(40);
    header.setPadding(new Insets(5, 0, 0, 5));

    recipeName = new Label();
    recipeName.setWrapText(true);
    recipeName.getStyleClass().addAll(CSS_RECIPE_TITLE, CSS_DROP_SHADOW);

    VBox buttonPanel = new VBox();
    buttonPanel.setSpacing(20);
    Button edit = new Button("Recept wijzigen");
    edit.setOnAction(this::showRecipeEditView);
    edit.setMinWidth(150);
    Button remove = new Button("Recept verwijderen");
    remove.setOnAction(this::showRemoveRecipeConfirmation);
    remove.setMinWidth(150);
    Button plan = new Button("Recept plannen");
    plan.setOnAction(this::planRecipe);
    plan.setMinWidth(150);
    Button list = new Button("Terug naar lijst");
    list.setOnAction(this::showRecipeListView);
    list.setMinWidth(150);
    Button planning = new Button("Terug naar planning");
    planning.setOnAction(this::showPlanning);
    planning.setMinWidth(150);
    buttonPanel.getChildren().addAll(edit, remove, plan, list, planning);

    Region buffer = new Region();
    HBox.setHgrow(buffer, Priority.ALWAYS);

    header.getChildren().add(imageView);
    header.getChildren().add(recipeName);
    header.getChildren().add(buffer);
    header.getChildren().add(buttonPanel);

    BootstrapColumn titleColumn = new BootstrapColumn(header);
    titleColumn.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);

    return titleColumn;
  }

  private BootstrapColumn createColumn(Node widget) {
    BootstrapColumn column = new BootstrapColumn(widget);
    column.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
    column.setBreakpointColumnWidth(Breakpoint.SMALL, 9);
    column.setBreakpointColumnWidth(Breakpoint.LARGE, 6);
    column.setBreakpointColumnWidth(Breakpoint.XLARGE, 4);
    return column;
  }

  private BootstrapColumn createLargeColumn(Node widget) {
    BootstrapColumn column = new BootstrapColumn(widget);
    column.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
    column.setBreakpointColumnWidth(Breakpoint.SMALL, 10);
    column.setBreakpointColumnWidth(Breakpoint.LARGE, 8);
    column.setBreakpointColumnWidth(Breakpoint.XLARGE, 6);
    return column;
  }

  private Node createFeaturesWidget(String title) {
    VBox widget = new VBox();
    widget.setSpacing(10);
    widget.getStyleClass().addAll(CSS_WIDGET, CSS_DROP_SHADOW);

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add(CSS_TITLE);
    widget.getChildren().add(titleLabel);

    widget.getChildren().add(new Separator(Orientation.HORIZONTAL));
    widget.getChildren().add(createFeatureItem("Categorieën:", tagString));
    widget.getChildren().add(createFeatureItem("Recept type:", recipeType));
    widget.getChildren().add(createFeatureItem("Voorbereidingstijd:", preparationTime));
    widget.getChildren().add(createFeatureItem("Bereidingstijd:", cookTime));
    widget.getChildren().add(createFeatureItem("Aantal personen:", servings));
    widget.getChildren().add(createFeatureItem("Aantal keren gegeten:", timesServed));
    widget.getChildren().add(createFeatureItem("Laatste keer gegeten:", lastTimeServed));
    widget.getChildren().add(createFeatureItem("Waardering:", rating));
    return widget;
  }

  private Node createIngredientTableWidget(String title) {
    VBox widget = new VBox();
    widget.setSpacing(10);
    widget.getStyleClass().addAll(CSS_WIDGET, CSS_DROP_SHADOW);

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add(CSS_TITLE);
    widget.getChildren().add(titleLabel);

    widget.getChildren().add(new Separator(Orientation.HORIZONTAL));
    widget.getChildren().add(ingredientTableView);

    ingredientTableView.getColumns().add(amountColumn);
    ingredientTableView.getColumns().add(measureUnitColumn);
    ingredientTableView.getColumns().add(ingredientNameColumn);
    ingredientTableView.getColumns().add(stockColumn);

    amountColumn.prefWidthProperty().bind(ingredientTableView.widthProperty().multiply(0.1));
    measureUnitColumn.prefWidthProperty().bind(ingredientTableView.widthProperty().multiply(0.35));
    ingredientNameColumn.prefWidthProperty().bind(ingredientTableView.widthProperty().multiply(0.45));
    stockColumn.prefWidthProperty().bind(ingredientTableView.widthProperty().multiply(0.1));

    ingredientTableView.getStyleClass().add(CSS_INGREDIENT_TABLE);

    amountColumn.setCellValueFactory(c -> (c.getValue().getAmount() != null && (10 * c.getValue().getAmount() % 10) == 0)
        ? new ReadOnlyObjectWrapper<>(Math.round(c.getValue().getAmount()))
        : new ReadOnlyObjectWrapper<>(c.getValue().getAmount()));

    measureUnitColumn.setCellValueFactory(c -> {
      if (c.getValue().getIngredientName().getMeasureUnit() == null) {
        return new ReadOnlyObjectWrapper<>("");
      } else {
        return new ReadOnlyObjectWrapper<>((c.getValue().getAmount() == null || c.getValue().getAmount() <= 1)
            ? c.getValue().getIngredientName().getMeasureUnit().getName()
            : c.getValue().getIngredientName().getMeasureUnit().getPluralName());
      }
    });

    ingredientNameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
        (c.getValue().getAmount() == null || c.getValue().getAmount() <= 1) ? c.getValue().getIngredientName().getName()
            : c.getValue().getIngredientName().getPluralName()));

    stockColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getIngredientName().isStock()));
    stockColumn.setCellFactory(c -> new CheckBoxTableCell<>());

    return widget;
  }

  private Node createTextWidget(String title, Label text) {
    VBox widget = new VBox();
    VBox.setVgrow(widget, Priority.ALWAYS);
    widget.setSpacing(10);
    widget.getStyleClass().addAll(CSS_WIDGET, CSS_DROP_SHADOW);

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add(CSS_TITLE);
    widget.getChildren().add(titleLabel);

    widget.getChildren().add(new Separator(Orientation.HORIZONTAL));

    text.setWrapText(true);
    text.setMinHeight(Region.USE_PREF_SIZE);
    widget.getChildren().add(text);
    return widget;
  }

  private Node createFeatureItem(String featureLabel, Label text) {
    HBox feature = new HBox();
    HBox.setHgrow(feature, Priority.ALWAYS);

    HBox left = new HBox();
    left.setMinWidth(200);
    HBox.setHgrow(left, Priority.NEVER);
    left.getChildren().add(new Label(featureLabel));

    HBox right = new HBox();
    HBox.setHgrow(right, Priority.ALWAYS);
    text.setWrapText(true);
    right.getChildren().add(text);

    feature.getChildren().addAll(left, right);
    return feature;
  }

  private void showRecipeListView(ActionEvent event) {
    if (rootView != null) {
      rootView.showRecipeListPanel(event);
    }
  }

  private void showRecipeEditView(ActionEvent event) {
    if (rootView != null) {
      rootView.showRecipeEditViewPanel(selectedRecipe);
    }
  }

  private void showRemoveRecipeConfirmation(ActionEvent event) {
    if (selectedRecipe != null) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("Bevestig uw keuze");
      alert.setHeaderText("Weet u zeker dat u het geselecteerde recept '" + selectedRecipe.getName() + "'  wilt verwijderen?");
      alert.initOwner(root.getScene().getWindow());
      alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
        removeRecipe();
        showRecipeListView(null);
      });
    }

  }

  private void removeRecipe() {
    Optional<Recipe> optionalRecipe = recipeService.findById(selectedRecipe.getId());
    if (optionalRecipe.isPresent()) {
      try {
        imageService.moveFileToDeleteFolderIfExists(selectedRecipe.getImage());
      } catch (IOException e) {
        log.error("Could not delete image " + selectedRecipe.getImage());
      }
      recipeService.remove(optionalRecipe.get());
    }
  }


  private void showPlanning(ActionEvent event) {
    if (rootView != null) {
      rootView.showPlanningPanel(event);;
    }
  }

  private void planRecipe(ActionEvent event) {
    planningService.addRecipeToRecipeList(selectedRecipe);
  }

}
