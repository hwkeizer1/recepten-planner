package nl.recipes.views.recipes;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.function.Predicate;
import org.girod.javafx.svgimage.SVGImage;
import org.springframework.stereotype.Component;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
import nl.recipes.views.components.utils.ToolBarFactory;
import nl.recipes.views.root.RootView;

@Slf4j
@Component
public class RecipeListCardView {

  private final RecipeService recipeService;
  private final ImageService imageService;
  private final PlanningService planningService;

  private RootView rootView;

  DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);

  FilteredList<Recipe> recipeList;
  TextField searchFilter;
  TextField searchIngredientFilter;

  ScrollPane scrollPane;
  VBox view;
  VBox content;
  BootstrapPane recipeListCardPane;

  public RecipeListCardView(RecipeService recipeService, ImageService imageService, PlanningService planningService) {
    this.recipeService = recipeService;
    this.imageService = imageService;
    this.planningService = planningService;

    scrollPane = new ScrollPane();
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
  }

  public void setRootView(RootView rootView) {
    this.rootView = rootView;
  }

  public Node getView() {
    recipeList = new FilteredList<>(recipeService.getList());

    recipeList.addListener((ListChangeListener.Change<? extends Recipe> c) -> {
      content.getChildren().remove(0);
      content.getChildren().add(createRecipeListCardPane());
      scrollPane.setContent(content);
    });

    view = new VBox();
    content = new VBox();
    content.getChildren().add(createRecipeListCardPane());
    scrollPane.setContent(content);
    view.getChildren().addAll(createToolBar(view), scrollPane);
    return view;
  }

  private BootstrapPane createRecipeListCardPane() {
    recipeListCardPane = new BootstrapPane();
    recipeListCardPane.setPadding(new Insets(15));
    recipeListCardPane.getStyleClass().add("background");
    recipeListCardPane.setVgap(25);
    recipeListCardPane.setHgap(25);

    BootstrapRow row = new BootstrapRow();
    for (Recipe recipe : recipeList) {
      boolean isPlanned = planningService.getRecipeList().stream().anyMatch(plannedRecipe -> plannedRecipe.getId().equals(recipe.getId()));
      Node recipeCard = createRecipeCard(recipe, isPlanned);
      row.addColumn(createColumn(recipeCard));
    }
    recipeListCardPane.addRow(row);
    return recipeListCardPane;
  }

  private BootstrapColumn createColumn(Node recipeCard) {
    BootstrapColumn column = new BootstrapColumn(recipeCard);
    column.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
    column.setBreakpointColumnWidth(Breakpoint.SMALL, 12);
    column.setBreakpointColumnWidth(Breakpoint.MEDIUM, 6);
    column.setBreakpointColumnWidth(Breakpoint.LARGE, 4);
    column.setBreakpointColumnWidth(Breakpoint.XLARGE, 3);
    return column;
  }

  private Node createRecipeCard(Recipe recipe, boolean isPlanned) {
    RecipeCard recipeCard = new RecipeCard(recipe, isPlanned);
    recipeCard.setOnPlanningCheckBoxAction(event -> onPlanningCheckBoxClicked(event, recipe));
    recipeCard.setOnRecipeCardClicked(event -> onMouseClicked(event, recipe));
    ImageView imageView = new ImageView();
    recipeCard.setImage(imageService.loadRecipeImage(imageView, recipe, 150d));
    return recipeCard;
  }

  private void onMouseClicked(MouseEvent event, Recipe recipe) {
    if (rootView != null) {
      rootView.showRecipeSingleViewPanel(recipe);
    }
  }

  private void onPlanningCheckBoxClicked(ActionEvent event, Recipe recipe) {
    if (((CheckBox) event.getSource()).isSelected()) {
      planRecipe(event, recipe);
    } else {
      planningService.removeRecipeFromRecipeList(recipe);
    }
  }

  private void planRecipe(ActionEvent event, Recipe recipe) {
    if (recipe != null) {
      planningService.addRecipeToRecipeList(recipe);
    }
  }

  // ToolBar
  private ToolBar createToolBar(VBox view) {

    final Pane rightSpacer = new Pane();
    HBox.setHgrow(rightSpacer, Priority.SOMETIMES);

    ToolBar toolBar = new ToolBar();
    toolBar.prefWidthProperty().bind(view.widthProperty());

    Button newRecipeButton = ToolBarFactory.createToolBarButton("/icons/add.svg", 30, "Nieuw recept toevoegen");
    newRecipeButton.setOnAction(this::showNewRecipeEditView);
    toolBar.getItems().add(newRecipeButton);

    toolBar.getItems().add(createSearchFilter());

    toolBar.getItems().add(rightSpacer);
    toolBar.getItems().add(createIngredientSearchFilter());

    return toolBar;
  }

  private void showNewRecipeEditView(ActionEvent event) {
    if (rootView != null) {
      rootView.showNewRecipeEditViewPanel();
    }
  }

  private HBox createSearchFilter() {
    int searchFilterHeight = 25;
    HBox searchFilterBox = new HBox();
    Label label = new Label("Filter: ");
    label.setPrefHeight(25);
    searchFilterBox.getChildren().add(label);

    searchFilter = new TextField();
    searchFilter.setMaxHeight(searchFilterHeight);
    searchFilter.setMinHeight(searchFilterHeight);
    searchFilter.setPrefHeight(Region.USE_COMPUTED_SIZE);
    searchFilter.setPrefWidth(Region.USE_COMPUTED_SIZE);
    searchFilterBox.getChildren().add(searchFilter);
    searchFilter.textProperty().addListener((observable, oldValue, newValue) -> recipeList.setPredicate(createRecipePredicate(newValue)));

    Button clear = ToolBarFactory.createToolBarButton("/icons/filter-remove.svg", searchFilterHeight, "Verwijder filter text");
    clear.setOnAction(this::clearSearch);

    searchFilterBox.getChildren().add(clear);
    return searchFilterBox;
  }

  private boolean searchFindRecipe(Recipe recipe, String searchText) {
    return (recipe.getName().toLowerCase().contains(searchText.toLowerCase()))
        || (recipe.getRecipeType().getDisplayName().toLowerCase().contains(searchText.toLowerCase()))
        || (recipe.getTagString().toLowerCase().contains(searchText.toLowerCase()));
  }

  private Predicate<Recipe> createRecipePredicate(String searchText) {
    return recipe -> {
      if (searchText == null || searchText.isEmpty())
        return true;
      return searchFindRecipe(recipe, searchText);
    };
  }

  private HBox createIngredientSearchFilter() {
    int searchFilterHeight = 25;
    HBox searchFilterBox = new HBox();
    Label label = new Label("Filter op ingredient: ");
    label.setPrefHeight(25);
    searchFilterBox.getChildren().add(label);

    searchIngredientFilter = new TextField();
    searchIngredientFilter.setMaxHeight(searchFilterHeight);
    searchIngredientFilter.setMinHeight(searchFilterHeight);
    searchIngredientFilter.setPrefHeight(Region.USE_COMPUTED_SIZE);
    searchIngredientFilter.setPrefWidth(Region.USE_COMPUTED_SIZE);
    searchFilterBox.getChildren().add(searchIngredientFilter);
    searchIngredientFilter.textProperty()
        .addListener((observable, oldValue, newValue) -> recipeList.setPredicate(createIngredientPredicate(newValue)));

    Button clear = ToolBarFactory.createToolBarButton("/icons/filter-remove.svg", searchFilterHeight, "Verwijder filter text");
    clear.setOnAction(this::clearIngredientSearch);

    searchFilterBox.getChildren().add(clear);
    return searchFilterBox;
  }

  private boolean searchFindIngredient(Recipe recipe, String searchText) {
    boolean hasIngredient = false;
    for (Iterator<Ingredient> it = recipe.getIngredients().iterator(); it.hasNext();) {
      Ingredient ingredient = it.next();
      hasIngredient = (ingredient.getIngredientName().getName().toLowerCase().contains(searchText.toLowerCase()));
      if (hasIngredient)
        return hasIngredient;
    }
    return hasIngredient;
  }

  private Predicate<Recipe> createIngredientPredicate(String searchText) {
    return recipe -> {
      if (searchText == null || searchText.isEmpty())
        return true;
      return searchFindIngredient(recipe, searchText);
    };
  }

  private void clearSearch(ActionEvent event) {
    searchFilter.clear();
  }

  private void clearIngredientSearch(ActionEvent event) {
    searchIngredientFilter.clear();
  }



}
