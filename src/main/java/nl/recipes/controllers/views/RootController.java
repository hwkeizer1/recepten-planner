package nl.recipes.controllers.views;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.Recipe;
import nl.recipes.views.recipes.RecipeListView;
import nl.recipes.views.recipes.SingelRecipeView;

@Controller
@FxmlView("root.fxml")
public class RootController implements Initializable {

	private final FxWeaver fxWeaver;
	private final TagPanelController tagPanelController;
	private final MeasureUnitPanelController measureUnitPanelController;
	private final IngredientNamePanelController ingredientNamePanelController;
	private final RecipeListView recipeListView;
	private final SingelRecipeView singleRecipeView;

	@FXML
	private BorderPane rootWindow;

	public RootController(FxWeaver fxWeaver, TagPanelController tagPanelController,
			MeasureUnitPanelController measureUnitPanelController,
			IngredientNamePanelController ingredientNamePanelController,
			RecipeListView recipeListView,
			SingelRecipeView singleRecipeView) {
		this.fxWeaver = fxWeaver;
		this.tagPanelController = tagPanelController;
		this.measureUnitPanelController = measureUnitPanelController;
		this.ingredientNamePanelController = ingredientNamePanelController;
		this.recipeListView = recipeListView;
		this.singleRecipeView = singleRecipeView;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rootWindow.getStyleClass().add("background");
		recipeListView.setRootController(this);
		rootWindow.setCenter(recipeListView.getRecipeListPanel());
	}
	
	@FXML
	public void showCreateBackupDialog(ActionEvent actionEvent) {
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
//		fxWeaver.loadController(RecipeListPanelController.class);
		rootWindow.setCenter(recipeListView.getRecipeListPanel());
	}
	
	public void showSingleViewRecipePanel(Recipe recipe) {
		rootWindow.setCenter(singleRecipeView.getSingleRecipeViewPanel(recipe));
	}
	
	@FXML
	public void closeProgram(ActionEvent actionEvent) {
		Platform.exit();
	}
}
