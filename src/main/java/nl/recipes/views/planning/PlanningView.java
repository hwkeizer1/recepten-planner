package nl.recipes.views.planning;

import static nl.recipes.views.ViewConstants.DROP_SHADOW;
import static nl.recipes.views.ViewConstants.WIDGET;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;


@Component
public class PlanningView {
	
	VBox planningBox;
	
	public VBox getPlanningView(Planning planning) {
		planningBox = new VBox();
		planningBox.getStyleClass().addAll(DROP_SHADOW, WIDGET);
		planningBox.setPadding(new Insets(15));
		
		DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("cccc dd MMMM yyyy");
		Label dateLabel = new Label(customFormatter.format(planning.getDate()));
		planningBox.getChildren().add(dateLabel);
		for (Recipe recipe : planning.getRecipesOrderedByType()) {
			Label recipeLabel = new Label(recipe.getRecipeType() + ": " + recipe.getName());
			recipeLabel.setPadding(new Insets(0,0,0,10));
			planningBox.getChildren().add(recipeLabel);
		}
		return planningBox;
	}
}
