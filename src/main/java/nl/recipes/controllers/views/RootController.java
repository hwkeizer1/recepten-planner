package nl.recipes.controllers.views;


import org.springframework.stereotype.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Slf4j
@Controller
@FxmlView("root.fxml")
public class RootController {

	private final FxWeaver fxWeaver;
	private final TagListViewController tagListViewController;
	private final MeasureUnitTableViewPanelController measureUnitTableViewPanelController;


	public RootController(FxWeaver fxWeaver, 
			TagListViewController tagListViewController, 
			MeasureUnitTableViewPanelController measureUnitTableViewPanelController) {
		this.fxWeaver = fxWeaver;
		this.tagListViewController = tagListViewController;
		this.measureUnitTableViewPanelController = measureUnitTableViewPanelController;
	}


	@FXML
	private BorderPane rootWindow;
	
	public void initialize() {
		tagListViewController.setRootController(this);
		measureUnitTableViewPanelController.setRootController(this);
	}
	
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
	public void showTagListView(ActionEvent actionEvent) {
		fxWeaver.loadController(TagListViewController.class);
		rootWindow.setLeft(tagListViewController.getTagListView());
	}
	
	@FXML
	public void showMeasureUnitTableViewPanel(ActionEvent actionEvent) {
		fxWeaver.loadController(MeasureUnitTableViewPanelController.class);
		rootWindow.setLeft(measureUnitTableViewPanelController.getMeasureUnitTableViewPanel());
	}
	
	@FXML
	public void showIngredientNameListView(ActionEvent actionEvent) {
		log.debug("IngredientNameList");
	}

	
	@FXML
	public void closeProgram(ActionEvent actionEvent) {
		Platform.exit();
	}
}
