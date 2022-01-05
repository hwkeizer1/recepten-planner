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
	


	public RootController(FxWeaver fxWeaver, TagListViewController tagListViewController) {
		this.fxWeaver = fxWeaver;
		this.tagListViewController = tagListViewController;
	}


	@FXML
	private BorderPane rootWindow;
	
	public void initialize() {
		tagListViewController.setRootController(this);
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
		rootWindow.setLeft(tagListViewController.tagListViewPanel);
	}
	
	@FXML
	public void showMeasureUnitListTableView(ActionEvent actionEvent) {
		log.debug("MeasureUnitList");
	}
	
	@FXML
	public void showIngredientNameListTableView(ActionEvent actionEvent) {
		log.debug("IngredientNameList");
	}

	
	@FXML
	public void closeProgram(ActionEvent actionEvent) {
		Platform.exit();
	}
}
