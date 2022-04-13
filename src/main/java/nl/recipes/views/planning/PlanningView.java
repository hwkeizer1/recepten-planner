package nl.recipes.views.planning;

import static nl.recipes.views.ViewConstants.*;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Planning;
import nl.recipes.domain.Recipe;
import nl.recipes.views.root.RootView;

@Slf4j
@Component
public class PlanningView {
	
	private RootView rootView;
	
	VBox planningBox;
	
	public void setRootView(RootView rootView) {
		this.rootView = rootView;
	}
	
	public VBox getPlanningView(Planning planning) {
		planningBox = new VBox();
		planningBox.getStyleClass().addAll(DROP_SHADOW, WIDGET);
//		planningBox.setPadding(new Insets(10));
		
		DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("cccc dd MMMM yyyy");
		Label dateLabel = new Label(customFormatter.format(planning.getDate()));
		dateLabel.getStyleClass().add(PLANNING_DATE);
		planningBox.getChildren().add(dateLabel);
		
		GridPane recipeList = new GridPane();
		
		ColumnConstraints column0 = new ColumnConstraints();
		column0.setPercentWidth(15);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(85);
		
		recipeList.getColumnConstraints().addAll(column0, column1);
		
		int row = 0;
		for (Recipe recipe : planning.getRecipesOrderedByType()) {
			Label recipeTypeLabel = new Label(recipe.getRecipeType().getDisplayName() + ":");
			recipeList.add(recipeTypeLabel, 0, row);
			Label recipeNameLabel = new Label(recipe.getName());
			recipeNameLabel.getStyleClass().add(PLANNING_RECIPE_LINK);
			recipeNameLabel.setOnMouseClicked(event -> showRecipeSingleView(recipe));
			recipeList.add(recipeNameLabel, 1, row);
			row++;
		}
		
		planningBox.getChildren().add(recipeList);
		return planningBox;
	}
	
	private void showRecipeSingleView(Recipe recipe) {
		if (rootView != null) {
			rootView.showRecipeSingleViewPanel(recipe);
		}
	}
}
