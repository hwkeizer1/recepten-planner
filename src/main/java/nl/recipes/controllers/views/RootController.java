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
import nl.recipes.views.configurations.ConfigurationView;
import nl.recipes.views.recipes.RecipeListView;
import nl.recipes.views.recipes.SingelRecipeView;

@Controller
@FxmlView("root.fxml")
public class RootController implements Initializable {

	private final FxWeaver fxWeaver;
	private final ConfigurationView configurationView;
	private final RecipeListView recipeListView;
	private final SingelRecipeView singleRecipeView;

	@FXML
	private BorderPane rootWindow;

	public RootController(FxWeaver fxWeaver,
			RecipeListView recipeListView,
			SingelRecipeView singleRecipeView, 
			ConfigurationView configurationView) {
		this.fxWeaver = fxWeaver;
		this.configurationView = configurationView;
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
	}
	
	@FXML
	public void showMeasureUnitPanel(ActionEvent actionEvent) {
	}
	
	@FXML
	public void showIngredientNamePanel(ActionEvent actionEvent) {
	}
	
	@FXML
	public void showRecipeListPanel(ActionEvent actionEvent) {
		rootWindow.setCenter(recipeListView.getRecipeListPanel());
	}
	
	public void showSingleViewRecipePanel(Recipe recipe) {
		rootWindow.setCenter(singleRecipeView.getSingleRecipeViewPanel(recipe));
	}
	
	public void showConfigurationPanel() {
		rootWindow.setCenter(configurationView.getConfigurationViewPanel());
	}
	
	@FXML
	public void closeProgram(ActionEvent actionEvent) {
		Platform.exit();
	}
}
