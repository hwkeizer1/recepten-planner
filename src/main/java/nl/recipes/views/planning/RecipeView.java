package nl.recipes.views.planning;

import static nl.recipes.views.ViewConstants.CSS_DROP_SHADOW;
import static nl.recipes.views.ViewConstants.CSS_WIDGET;

import org.springframework.stereotype.Component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import nl.recipes.domain.Recipe;

@Component
public class RecipeView {

  VBox recipeBox;

  public VBox getRecipeView(Recipe recipe) {
    recipeBox = new VBox();
    recipeBox.getStyleClass().addAll(CSS_DROP_SHADOW, CSS_WIDGET);
    recipeBox.setPadding(new Insets(15));

    Label nameLabel = new Label(recipe.getRecipeType() + ": " + recipe.getName());

    recipeBox.getChildren().add(nameLabel);
    return recipeBox;
  }

}
