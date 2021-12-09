package nl.recipes.controllers.views;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;

@Controller
@FxmlView("restoreBackupDialog.fxml")
public class RestoreBackupDialogController implements Initializable {

	private Stage stage;
	
	@FXML
	private AnchorPane anchorPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.stage = new Stage();
		stage.setScene(new Scene(anchorPane));
		stage.setTitle("Backup terugzetten");
	}
	
	public void show() {
		stage.show();
	}
}
