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
	private final TagListViewPanelController tagListViewController;
	private final MeasureUnitTableViewPanelController measureUnitTableViewPanelController;
	private final IngredientNameTableViewPanelController ingredientNameTableViewPanelController;


	public RootController(FxWeaver fxWeaver, 
			TagListViewPanelController tagListViewController, 
			MeasureUnitTableViewPanelController measureUnitTableViewPanelController, IngredientNameTableViewPanelController ingredientNameTableViewPanelController) {
		this.fxWeaver = fxWeaver;
		this.tagListViewController = tagListViewController;
		this.measureUnitTableViewPanelController = measureUnitTableViewPanelController;
		this.ingredientNameTableViewPanelController = ingredientNameTableViewPanelController;
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
	public void showTagListViewPanel(ActionEvent actionEvent) {
		fxWeaver.loadController(TagListViewPanelController.class);
		rootWindow.setCenter(tagListViewController.getTagListView());
	}
	
	@FXML
	public void showMeasureUnitTableViewPanel(ActionEvent actionEvent) {
		fxWeaver.loadController(MeasureUnitTableViewPanelController.class);
		rootWindow.setCenter(measureUnitTableViewPanelController.getMeasureUnitTableViewPanel());
	}
	
	@FXML
	public void showIngredientNameTableViewPanel(ActionEvent actionEvent) {
		fxWeaver.loadController(IngredientNameTableViewPanelController.class);
		rootWindow.setCenter(ingredientNameTableViewPanelController.getIngredientNameTableViewPanel());
	}

	
	@FXML
	public void closeProgram(ActionEvent actionEvent) {
		Platform.exit();
	}
}
