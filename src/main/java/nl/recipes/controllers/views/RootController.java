package nl.recipes.controllers.views;

import java.io.File;

import org.springframework.stereotype.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.views.TagTableView;

@Slf4j
@Controller
@FxmlView("root.fxml")
public class RootController {

	private final FxWeaver fxWeaver;
	private final TagTableView tagTableView;
	
	public RootController(FxWeaver fxWeaver, TagTableView tagTableView) {
		this.fxWeaver = fxWeaver;
		this.tagTableView = tagTableView;
	}

	@FXML
	private BorderPane rootWindow;
	
	@FXML
	public void showCreateBackupDialog(ActionEvent actionEvent) {
		
	}
	
	@FXML
	public void showRestoreBackupDialog(ActionEvent actionEvent) {
		fxWeaver.loadController(RestoreBackupDialogController.class).show();
	}
	
	@FXML
	public void closeSideBar(ActionEvent actionEvent) {
		rootWindow.setLeft(null);
	}
	
	@FXML
	public void showTagListTableView(ActionEvent actionEvent) {
		rootWindow.setLeft(tagTableView.getTableView());
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
