package nl.recipes.controllers.views;


import org.springframework.stereotype.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Controller
@FxmlView("root.fxml")
public class RootController {

	private final FxWeaver fxWeaver;
	private final TagPanelController tagPanelController;
	private final MeasureUnitPanelController measureUnitPanelController;
	private final IngredientNamePanelController ingredientNamePanelController;
	private final RecipeListPanelController recipeListPanelController;


	public RootController(FxWeaver fxWeaver, 
			TagPanelController tagPanelController, 
			MeasureUnitPanelController measureUnitPanelController, IngredientNamePanelController ingredientNamePanelController, RecipeListPanelController recipeListPanelController) {
		this.fxWeaver = fxWeaver;
		this.tagPanelController = tagPanelController;
		this.measureUnitPanelController = measureUnitPanelController;
		this.ingredientNamePanelController = ingredientNamePanelController;
		this.recipeListPanelController = recipeListPanelController;
	}


	@FXML
	private BorderPane rootWindow;
	
	@FXML
	public void showCreateBackupDialog(ActionEvent actionEvent) {
		// TO be implemented
	}
	
	@FXML
	public void showRestoreBackupDialog(ActionEvent actionEvent) {
		fxWeaver.loadController(RestoreBackupDialogController.class).show();
	}
	
	@FXML
	public void closeLeftSidePanel() {
		rootWindow.setLeft(null);
	}
	
	@FXML
	public void closeCenterPanel() {
		rootWindow.setCenter(null);
	}
	
	@FXML
	public void showTagPanel(ActionEvent actionEvent) {
		fxWeaver.loadController(TagPanelController.class);
		rootWindow.setCenter(tagPanelController.getTagPanel());
	}
	
	@FXML
	public void showMeasureUnitPanel(ActionEvent actionEvent) {
		fxWeaver.loadController(MeasureUnitPanelController.class);
		rootWindow.setCenter(measureUnitPanelController.getMeasureUnitPanel());
	}
	
	@FXML
	public void showIngredientNamePanel(ActionEvent actionEvent) {
		fxWeaver.loadController(IngredientNamePanelController.class);
		rootWindow.setCenter(ingredientNamePanelController.getIngredientNamePanel());
	}
	
	@FXML
	public void showRecipeListPanel(ActionEvent actionEvent) {
		fxWeaver.loadController(RecipeListPanelController.class);
		rootWindow.setCenter(recipeListPanelController.getRecipeListPanel());
	}
	
	@FXML
	public void closeProgram(ActionEvent actionEvent) {
		Platform.exit();
	}
}
