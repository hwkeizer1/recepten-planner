package nl.recipes.views.planning;

import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
import nl.recipes.services.PlanningService;
import nl.recipes.views.ViewMessages;
import nl.recipes.views.root.RootView;

@Component
public class PlanningListView {

  private RootView rootView;

  private final PlanningService planningService;

  private final PlanningView planningView;

  private final RecipeView recipeView;

  ObservableList<Recipe> recipeList;

  ObservableList<Planning> planningList;

  SplitPane planningPanel;

  AnchorPane leftPanel;

  AnchorPane rightPanel;

  TableView<Recipe> recipeListTableView;

  public PlanningListView(PlanningService planningService, PlanningView planningView,
      RecipeView recipeView) {
    this.planningService = planningService;
    this.planningView = planningView;
    this.recipeView = recipeView;

  }

  public void setRootView(RootView rootView) {
    this.rootView = rootView;
    planningView.setRootView(this.rootView);
  }

  public SplitPane getPlanningPanel() {
    recipeList = planningService.getRecipeList();
    planningList = planningService.getPlanningList();

    planningPanel = new SplitPane();
    planningPanel.getItems().addAll(createRecipeListPanel(), createPlanningListPanel());
    return planningPanel;
  }

  private AnchorPane createRecipeListPanel() {
    Label leftPanelTitle = new Label("Geplande recepten");
    leftPanelTitle.getStyleClass().add("header");

    VBox recipeListBox = new VBox();
    recipeListBox.setPadding(new Insets(30));
    recipeListBox.getChildren().addAll(leftPanelTitle, createRecipeList());

    leftPanel = new AnchorPane();
    AnchorPane.setTopAnchor(recipeListBox, 0.0);
    AnchorPane.setBottomAnchor(recipeListBox, 0.0);
    AnchorPane.setRightAnchor(recipeListBox, 0.0);
    AnchorPane.setLeftAnchor(recipeListBox, 0.0);
    leftPanel.getChildren().addAll(recipeListBox);

    return leftPanel;
  }

  private Node createRecipeList() {
    VBox recipeListPanel = new VBox();
    recipeListPanel.setSpacing(20);

    recipeList = planningService.getRecipeList();
    for (Recipe recipe : recipeList) {
      VBox recipeListItem = recipeView.getRecipeView(recipe);
      recipeListItem.setOnDragDetected(event -> {
        Dragboard db = recipeListItem.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(recipe.getId().toString());
        db.setContent(content);
      });
      recipeListPanel.getChildren().add(recipeListItem);
    }

    return recipeListPanel;
  }

  private AnchorPane createPlanningListPanel() {
    Button shoppingList = new Button(ViewMessages.TO_SHOPPING_PAGE);
    shoppingList.setOnAction(this::showShoppingPage);

    Label rightPanelTitle = new Label("Dag planning");
    rightPanelTitle.getStyleClass().add("header");

    VBox planningListBox = new VBox();
    planningListBox.setPadding(new Insets(30));
    planningListBox.getChildren().addAll(shoppingList, rightPanelTitle, createPlanningList());

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    planningListBox.prefWidthProperty().bind(scrollPane.widthProperty());
    scrollPane.setContent(planningListBox);

    rightPanel = new AnchorPane();
    AnchorPane.setTopAnchor(scrollPane, 0.0);
    AnchorPane.setBottomAnchor(scrollPane, 0.0);
    AnchorPane.setRightAnchor(scrollPane, 0.0);
    AnchorPane.setLeftAnchor(scrollPane, 0.0);
    rightPanel.getChildren().add(scrollPane);

    return rightPanel;
  }

  private Node createPlanningList() {
    VBox planningListPanel = new VBox();
    planningListPanel.setSpacing(20);

    planningList = planningService.getPlanningList();
    for (Planning planning : planningList) {
      VBox planningListItem = planningView.getPlanningView(planning);
      planningListItem.setOnDragOver(event -> event.acceptTransferModes(TransferMode.MOVE));
      planningListItem.setOnDragDropped(event -> {
        Dragboard db = event.getDragboard();
        planningService.moveRecipeToPlanning(planning, db.getString());
        refreshPlanningPanel();
      });
      planningListPanel.getChildren().add(planningListItem);
    }

    return planningListPanel;
  }

  private void refreshPlanningPanel() {
    planningPanel.getItems().clear();
    planningPanel.getItems().addAll(createRecipeListPanel(), createPlanningListPanel());
  }

  private void showShoppingPage(ActionEvent event) {
    rootView.showShoppingPage(event);
  }

}
