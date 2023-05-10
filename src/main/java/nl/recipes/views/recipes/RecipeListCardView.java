package nl.recipes.views.recipes;

import org.springframework.stereotype.Component;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Recipe;
import nl.recipes.services.RecipeService;
import nl.recipes.views.root.RootView;

@Slf4j
@Component
public class RecipeListCardView {
  
  private final RecipeService recipeService;
  
  private RootView rootView;
  
  FilteredList<Recipe> recipeList;
  
  ScrollPane scrollPane;
  TilePane tilePane;
  
  public RecipeListCardView(RecipeService recipeService) {
    this.recipeService = recipeService;
    
    tilePane = new TilePane();
    tilePane.setHgap(10);
    tilePane.setVgap(10);
    tilePane.setPadding(new Insets(10));
    scrollPane = new ScrollPane(tilePane);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    
  }

  public void setRootView(RootView rootView) {
    this.rootView = rootView;
  }
  
  public ScrollPane getRecipeListCardPanel() {
    recipeList = new FilteredList<>(recipeService.getList());
    
    for (Recipe recipe : recipeList) {
      Pane pane = new Pane();
      pane.setPadding(new Insets(20));
      pane.setStyle("-fx-border: 1px; -fx-border-color: red;");
      Label label = new Label(recipe.getName());
      pane.getChildren().add(label);
      
      pane.setOnMousePressed(this::onPress);
      Integer index = recipe.getId() == null ? null : Math.toIntExact(recipe.getId());
      GridPane.setRowIndex(pane, index);
      tilePane.getChildren().add(pane);
    }
    return scrollPane;
  }
  
  private void onPress(MouseEvent event) {
    int recipeId = GridPane.getRowIndex((Node) event.getSource());
    System.out.println(String.format("Node clicked at: row=%d", recipeId));
}
}
