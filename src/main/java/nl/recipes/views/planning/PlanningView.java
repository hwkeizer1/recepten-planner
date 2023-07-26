package nl.recipes.views.planning;

import static nl.recipes.views.ViewConstants.CSS_DROP_SHADOW;
import static nl.recipes.views.ViewConstants.CSS_PLANNING_DATE;
import static nl.recipes.views.ViewConstants.CSS_PLANNING_RECIPE_LINK;
import static nl.recipes.views.ViewConstants.CSS_WIDGET;

import java.time.format.DateTimeFormatter;
import java.util.function.UnaryOperator;
import org.springframework.stereotype.Component;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
import nl.recipes.services.PlanningService;
import nl.recipes.views.root.RootView;

@Component
public class PlanningView {

  private RootView rootView;

  private final PlanningService planningService;

  VBox planningBox;
  Label servingLabel;

  public PlanningView(PlanningService planningService) {
    this.planningService = planningService;
  }

  public void setRootView(RootView rootView) {
    this.rootView = rootView;
  }

  public VBox createPlanningsView(Planning planning) {
    DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("cccc dd MMMM yyyy");
    Label dateLabel = new Label(customFormatter.format(planning.getDate()));
    dateLabel.getStyleClass().add(CSS_PLANNING_DATE);

    Region regionHeader = new Region();
    HBox.setHgrow(regionHeader, Priority.ALWAYS);

    Button clear = new Button("Leeg maken");
    clear.setOnAction(e -> clearPlanning(planning));
    clear.setMinWidth(100);

    HBox headerBox = new HBox();
    headerBox.getChildren().addAll(dateLabel, regionHeader, clear);

    planningBox = new VBox();
    planningBox.setSpacing(10);

    planningBox.getStyleClass().addAll(CSS_DROP_SHADOW, CSS_WIDGET);
    planningBox.getChildren().add(headerBox);

    HBox servingBox = new HBox();
    servingLabel = new Label("Aantal personen: ");
    TextField servingsField = new TextField();
    servingsField.setMaxWidth(50);
    UnaryOperator<Change> integerFilter = change -> {
      String input = change.getText();
      if (input.matches("[1-9]*")) {
        return change;
      }
      return null;
    };
    servingsField.setTextFormatter(new TextFormatter<String>(integerFilter));
    if (planning.getServings() == null) {
      planning.setServings(0);
    }
      servingsField.setText(planning.getServings().toString());
    
    servingsField
        .setOnKeyReleased(e -> planningService.setServings(planning, servingsField.getText()));
    servingBox.getChildren().addAll(servingLabel, servingsField);


    Region regionFooter = new Region();
    HBox.setHgrow(regionFooter, Priority.ALWAYS);

    CheckBox shoppingCheckBox = new CheckBox("Op boodschappenlijst");
    shoppingCheckBox.setSelected(planning.isOnShoppingList());
    shoppingCheckBox.setOnAction(
        e -> planningService.setOnShoppingList(planning, shoppingCheckBox.isSelected()));

    HBox footerBox = new HBox();
    footerBox.getChildren().addAll(servingBox, regionFooter, shoppingCheckBox);

    GridPane recipeList = new GridPane();

    int row = 0;
    if (planning.getRecipes().isEmpty()) {
      shoppingCheckBox.setVisible(false);
      servingBox.setVisible(false);
    }
    for (Recipe recipe : planning.getRecipesOrderedByType()) {
      Label recipeTypeLabel = new Label(recipe.getRecipeType().getDisplayName() + ":");
      recipeTypeLabel.setMinWidth(120);
      recipeList.add(recipeTypeLabel, 0, row);
      Label recipeNameLabel = new Label(recipe.getName());
      recipeNameLabel.getStyleClass().add(CSS_PLANNING_RECIPE_LINK);
      recipeNameLabel.setOnMouseClicked(event -> showRecipeSingleView(recipe));
      recipeList.add(recipeNameLabel, 1, row);
      row++;
    }

    planningBox.getChildren().addAll(recipeList, footerBox);
    return planningBox;
  }

  private void showRecipeSingleView(Recipe recipe) {
    if (rootView != null) {
      rootView.showRecipeSingleViewPanel(recipe);
    }
  }

  private void clearPlanning(Planning planning) {
    planningService.clearPlanning(planning);
    if (rootView != null) {
      rootView.showPlanningPanel(null);
    }
  }
}
