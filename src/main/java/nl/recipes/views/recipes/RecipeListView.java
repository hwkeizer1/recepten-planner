package nl.recipes.views.recipes;

import static nl.recipes.views.ViewConstants.CSS_BASIC_TABLE;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.services.PlanningService;
import nl.recipes.services.RecipeService;
import nl.recipes.views.root.RootView;

@Component
public class RecipeListView {

  private final RecipeService recipeService;

  private final PlanningService planningService;

  private RootView rootView;

  private Recipe selectedRecipe;

  FilteredList<Recipe> recipeList;

  AnchorPane recipeListPane;

  VBox recipeListBox;

  TableView<Recipe> recipeListTableView;

  TextField searchFilter;

  Alert removeAlert;

  public RecipeListView(RecipeService recipeService, PlanningService planningService) {
    this.recipeService = recipeService;
    this.planningService = planningService;

    recipeListPane = new AnchorPane();
    recipeListBox = new VBox();
    AnchorPane.setTopAnchor(recipeListBox, 0.0);
    AnchorPane.setBottomAnchor(recipeListBox, 0.0);
    AnchorPane.setRightAnchor(recipeListBox, 0.0);
    AnchorPane.setLeftAnchor(recipeListBox, 0.0);

    recipeListBox.getChildren().add(createButtonAndSearchBar());
    recipeListBox.getChildren().add(createRecipeListTable());

    recipeListPane.getChildren().add(recipeListBox);
  }

  public void setRootView(RootView rootView) {
    this.rootView = rootView;
  }

  public AnchorPane getRecipeListPanel() {
    recipeList = new FilteredList<>(recipeService.getList());
    recipeListTableView.setItems(recipeList);
    recipeListTableView.setFixedCellSize(25);
    recipeListTableView.prefHeightProperty().bind(recipeListTableView.fixedCellSizeProperty()
        .multiply(Bindings.size(recipeListTableView.getItems())).add(30));
    return recipeListPane;
  }

  private TableView<Recipe> createRecipeListTable() {
    recipeListTableView = new TableView<>();
    TableColumn<Recipe, String> nameColumn = new TableColumn<>("Naam");
    TableColumn<Recipe, RecipeType> typeColumn = new TableColumn<>("Type");
    TableColumn<Recipe, String> tagColumn = new TableColumn<>("Trefwoorden");
    TableColumn<Recipe, LocalDate> lastServedColumn = new TableColumn<>("Laatst gegegeten");
    TableColumn<Recipe, Integer> timesServedColumn = new TableColumn<>("Aantal keren gegeten");

    recipeListTableView.getColumns().add(nameColumn);
    recipeListTableView.getColumns().add(typeColumn);
    recipeListTableView.getColumns().add(tagColumn);
    recipeListTableView.getColumns().add(lastServedColumn);
    recipeListTableView.getColumns().add(timesServedColumn);
    recipeListTableView.getStyleClass().add(CSS_BASIC_TABLE);

    ChangeListener<Recipe> recipeChangeListener =
        (observable, oldValue, newValue) -> selectedRecipe = newValue;
    recipeListTableView.getSelectionModel().selectedItemProperty()
        .addListener(recipeChangeListener);

    nameColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.35));
    typeColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.15));
    tagColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.2));
    lastServedColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.15));
    timesServedColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.15));

    recipeListTableView.setRowFactory(tv -> {
      TableRow<Recipe> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2 && (!row.isEmpty())) {
          Recipe selection = row.getItem();
          showRecipeSingleView(selection);
        }
      });
      return row;
    });

    nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
    typeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getRecipeType()));
    tagColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTagString()));
    lastServedColumn
        .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLastServed()));
    timesServedColumn
        .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTimesServed()));

    return recipeListTableView;
  }

  private HBox createButtonAndSearchBar() {
    HBox buttonBox = new HBox();
    buttonBox.setPadding(new Insets(10));
    buttonBox.setSpacing(30);

    Button newRecipe = new Button("Nieuw recept toevoegen");
    newRecipe.setOnAction(this::showNewRecipeEditView);
    buttonBox.getChildren().add(newRecipe);

    Button removeRecipeButton = new Button("Recept verwijderen");
    removeRecipeButton.setOnAction(this::showRemoveRecipeAlert);
    buttonBox.getChildren().add(removeRecipeButton);

    buttonBox.getChildren().add(createSearchFilter());

    Button planRecipeButton = new Button("Recept inplannen");
    planRecipeButton.setOnAction(this::planRecipe);
    buttonBox.getChildren().add(planRecipeButton);

    return buttonBox;
  }

  private HBox createSearchFilter() {
    HBox searchFilterBox = new HBox();
    searchFilterBox.setMaxHeight(25);
    searchFilterBox.setSpacing(2);

    StackPane imagePane = new StackPane();
    imagePane.setPadding(new Insets(0, 2, 0, 0));
    Image searchIcon = new Image("/icons/search.png", 15, 15, true, false);
    ImageView searchIconView = new ImageView(searchIcon);
    imagePane.getChildren().add(searchIconView);
    searchFilterBox.getChildren().add(imagePane);

    searchFilter = new TextField();
    searchFilterBox.getChildren().add(searchFilter);
    searchFilter.textProperty().addListener(
        (observable, oldValue, newValue) -> recipeList.setPredicate(createPredicate(newValue)));

    Image clearIcon = new Image("/icons/clear.png", 21, 21, true, false);
    ImageView clearIconView = new ImageView(clearIcon);
    Button clear = new Button();
    clear.setPadding(new Insets(1));
    clear.setGraphic(clearIconView);
    clear.setOnAction(this::clearSearch);
    searchFilterBox.getChildren().add(clear);

    return searchFilterBox;
  }

  private boolean searchFindRecipe(Recipe recipe, String searchText) {
    return (recipe.getName().toLowerCase().contains(searchText.toLowerCase()))
        || (recipe.getRecipeType().getDisplayName().toLowerCase()
            .contains(searchText.toLowerCase()))
        || (recipe.getTagString().toLowerCase().contains(searchText.toLowerCase()));
  }

  private Predicate<Recipe> createPredicate(String searchText) {
    return recipe -> {
      if (searchText == null || searchText.isEmpty())
        return true;
      return searchFindRecipe(recipe, searchText);
    };
  }

  private void showRecipeSingleView(Recipe recipe) {
    if (rootView != null) {
      rootView.showRecipeSingleViewPanel(recipe);
    }
  }

  private void showNewRecipeEditView(ActionEvent event) {
    if (rootView != null) {
      rootView.showNewRecipeEditViewPanel();
    }
  }

  private void clearSearch(ActionEvent event) {
    searchFilter.clear();
  }

  private void showRemoveRecipeAlert(ActionEvent event) {
    if (selectedRecipe != null) {
      removeAlert =
          new Alert(AlertType.CONFIRMATION, "Weet u zeker dat u het geselecteerde recept '"
              + selectedRecipe.getName() + "'  wilt verwijderen?");
      removeAlert.initModality(Modality.WINDOW_MODAL);
      removeAlert.showAndWait().filter(response -> response == ButtonType.OK)
          .ifPresent(response -> removeRecipe());
    }
  }

  private void removeRecipe() {
    Optional<Recipe> optionalRecipe = recipeService.findById(selectedRecipe.getId());
    if (optionalRecipe.isPresent()) {
      recipeService.remove(optionalRecipe.get());
    }
  }

  private void planRecipe(ActionEvent event) {
    if (selectedRecipe != null) {
      planningService.addRecipeToPlanning(selectedRecipe);
    }
  }

}
