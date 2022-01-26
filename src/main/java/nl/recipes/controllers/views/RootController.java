package nl.recipes.controllers.views;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.Recipe;

@Slf4j
@Controller
@FxmlView("root.fxml")
public class RootController implements Initializable {

	private final FxWeaver fxWeaver;
	private final TagPanelController tagPanelController;
	private final MeasureUnitPanelController measureUnitPanelController;
	private final IngredientNamePanelController ingredientNamePanelController;
	private final RecipeListPanelController recipeListPanelController;
	private final SingleRecipeViewController singleRecipeViewController;


	public RootController(FxWeaver fxWeaver, TagPanelController tagPanelController,
			MeasureUnitPanelController measureUnitPanelController,
			IngredientNamePanelController ingredientNamePanelController,
			RecipeListPanelController recipeListPanelController,
			SingleRecipeViewController singleRecipeViewController) {
		this.fxWeaver = fxWeaver;
		this.tagPanelController = tagPanelController;
		this.measureUnitPanelController = measureUnitPanelController;
		this.ingredientNamePanelController = ingredientNamePanelController;
		this.recipeListPanelController = recipeListPanelController;
		this.singleRecipeViewController = singleRecipeViewController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		recipeListPanelController.setRootController(this);
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
	
	public void showSingleViewRecipePanel(Recipe recipe) {
		fxWeaver.loadController(SingleRecipeViewController.class);
		rootWindow.setCenter(singleRecipeViewController.getSingleRecipeViewPanel(recipe));
	}
	
	@FXML
	public void closeProgram(ActionEvent actionEvent) {
		Platform.exit();
	}
}
