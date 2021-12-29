package nl.recipes.controllers.views;

import java.io.File;

import org.springframework.stereotype.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.services.BackupService;

@Controller
@FxmlView("restoreBackupDialog.fxml")
public class RestoreBackupDialogController {

	private final BackupService backupService;
	
	private Stage stage;
	
	@FXML
	private AnchorPane anchorPane;
	
	@FXML
	private TextField backupFolderPathTextField;

	
	public RestoreBackupDialogController(BackupService backupService) {
		this.backupService = backupService;
	}

	public void initialize() {
		this.stage = new Stage();
		stage.setScene(new Scene(anchorPane));
		stage.setTitle("Backup terugzetten");
	}
	
	public void show() {
		stage.show();
	}
	
	@FXML
	public void selectBackupFolder(ActionEvent actionEvent) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File directory = directoryChooser.showDialog(stage);
		
		if (directory != null) {
			backupFolderPathTextField.setText(directory.getAbsolutePath());
		}
	}
	
	@FXML
	public void restore(ActionEvent actionEvent) {
		backupService.restore(backupFolderPathTextField.getText());
		stage.close();
	}
	
	@FXML
	public void cancelRestore(ActionEvent actionEvent) {
		stage.close();
	}
}
