package nl.recipes.views.backup;

import static nl.recipes.views.ViewMessages.*;

import java.io.File;

import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import nl.recipes.services.BackupService;
import nl.recipes.services.ConfigService;

@Component
public class RestoreBackupDialog {

  private final BackupService backupService;

  private final ConfigService configService;

  private Stage dialog;

  private TextField restoreFolderPathTextField = new TextField();

  public RestoreBackupDialog(BackupService backupService, ConfigService configService) {
    this.backupService = backupService;
    this.configService = configService;
  }

  public void showRestoreBackupDialog() {
    dialog = new Stage();
    dialog.setTitle("Backup terugzetten");

    Button ok = new Button("OK");
    ok.setDefaultButton(true);
    Button cancel = new Button("Cancel");
    cancel.setCancelButton(true);

    // add action handlers for the dialog buttons.
    ok.setOnAction(this::restore);
    cancel.setOnAction(e -> dialog.close());

    // layout the dialog.
    HBox buttons = new HBox();
    buttons.getChildren().addAll(cancel, ok);
    buttons.setSpacing(30);
    buttons.setPadding(new Insets(20, 40, 10, 0));
    buttons.setAlignment(Pos.CENTER_RIGHT);

    VBox layout = new VBox(10);
    layout.getChildren().addAll(initializeForm(), buttons);
    layout.setPadding(new Insets(5));

    dialog.setScene(new Scene(layout, 600, 150));
    dialog.centerOnScreen();
    dialog.showAndWait();
  }

  private Node initializeForm() {
    Button select = new Button("Selecteer de folder");
    select.setOnAction(this::selectRestoreFolder);

    GridPane inputForm = new GridPane();
    inputForm.setPadding(new Insets(20, 20, 20, 20));
    inputForm.setHgap(20);
    inputForm.setVgap(20);

    ColumnConstraints column0 = new ColumnConstraints();
    column0.setPercentWidth(15);
    column0.setHalignment(HPos.RIGHT);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(50);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(35);

    inputForm.getColumnConstraints().addAll(column0, column1, column2);

    Label nameLabel = new Label("Folder:");
    inputForm.add(nameLabel, 0, 0);
    inputForm.add(restoreFolderPathTextField, 1, 0);
    inputForm.add(select, 2, 0);

    GridPane.setHgrow(nameLabel, Priority.ALWAYS);
    GridPane.setHgrow(restoreFolderPathTextField, Priority.ALWAYS);

    return inputForm;
  }

  public void selectRestoreFolder(ActionEvent actionEvent) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setInitialDirectory(new File(configService.getConfigProperty(BACKUP_FOLDER)));
    File directory = directoryChooser.showDialog(dialog);

    if (directory != null) {
      restoreFolderPathTextField.setText(directory.getAbsolutePath());
    }
  }

  public void restore(ActionEvent actionEvent) {
    backupService.restore(restoreFolderPathTextField.getText());
    dialog.close();
  }

}
