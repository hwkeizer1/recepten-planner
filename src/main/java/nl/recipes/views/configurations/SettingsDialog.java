package nl.recipes.views.configurations;

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
import nl.recipes.services.ConfigService;

@Component
public class SettingsDialog {

  private final ConfigService configService;

  private Stage dialog;

  private TextField backupFolderTextField = new TextField();
  private TextField backupsToKeepTextField = new TextField();
  private TextField googleSpreadsheetIdField = new TextField();
  private TextField shoppingSheetIdField = new TextField();
  private TextField templateSheetIdField = new TextField();
  private TextField imageFolderTextField = new TextField();

  public SettingsDialog(ConfigService configService) {
    this.configService = configService;
  }

  public void showSettingsDialog() {

    dialog = new Stage();
    dialog.setTitle("Instellingen wijzigen");

    Button ok = new Button("OK");
    ok.setDefaultButton(true);
    Button cancel = new Button("Cancel");
    cancel.setCancelButton(true);

    // add action handlers for the dialog buttons.
    ok.setOnAction(this::save);
    cancel.setOnAction(e -> dialog.close());

    // layout the dialog.
    HBox buttons = new HBox();
    buttons.getChildren().addAll(cancel, ok);
    buttons.setSpacing(25);
    buttons.setPadding(new Insets(20, 40, 10, 0));
    buttons.setAlignment(Pos.CENTER_RIGHT);

    VBox layout = new VBox(10);
    layout.getChildren().addAll(initializeForm(), buttons);
    layout.setPadding(new Insets(5));

    dialog.setScene(new Scene(layout, 900, 400));
    dialog.centerOnScreen();
    dialog.showAndWait();
  }

  private Node initializeForm() {
    Button selectBackupFolderButton = new Button("Selecteer de folder");
    selectBackupFolderButton.setOnAction(this::selectBackupFolder);
    
    Button selectImageFolderButton = new Button("Selecteer de folder");
    selectImageFolderButton.setOnAction(this::selectImageFolder);

    GridPane inputForm = new GridPane();
    inputForm.setPadding(new Insets(20, 20, 20, 20));
    inputForm.setHgap(20);
    inputForm.setVgap(20);

    ColumnConstraints column0 = new ColumnConstraints();
    column0.setPercentWidth(25);
    column0.setHalignment(HPos.RIGHT);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(50);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(20);

    inputForm.getColumnConstraints().addAll(column0, column1, column2);

    Label backupFolderLabel = new Label("Backup folder:");
    inputForm.add(backupFolderLabel, 0, 0);
    backupFolderTextField.setText(configService.getConfigProperty(BACKUP_FOLDER));
    inputForm.add(backupFolderTextField, 1, 0);
    inputForm.add(selectBackupFolderButton, 2, 0);

    Label backupsToKeepLabel = new Label("Aantal backups:");
    inputForm.add(backupsToKeepLabel, 0, 1);
    backupsToKeepTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.matches("\\d*"))
        return;
      backupsToKeepTextField.setText(newValue.replaceAll("[^\\d]", ""));
    });
    backupsToKeepTextField.setMaxWidth(50);
    backupsToKeepTextField.setText(configService.getConfigProperty(BACKUPS_TO_KEEP));
    inputForm.add(backupsToKeepTextField, 1, 1);

    Label googleSpreadsheetIdLabel = new Label("Google spreadsheet id:");
    inputForm.add(googleSpreadsheetIdLabel, 0, 2);
    googleSpreadsheetIdField.setText(configService.getConfigProperty(GOOGLE_SPREADSHEET_ID));
    inputForm.add(googleSpreadsheetIdField, 1, 2);

    Label shoppingSheetIdLabel = new Label("Boodschappen sheet id:");
    inputForm.add(shoppingSheetIdLabel, 0, 3);
    shoppingSheetIdField.setText(configService.getConfigProperty(SHOPPING_SHEET_ID));
    inputForm.add(shoppingSheetIdField, 1, 3);

    Label templateSheetIdLabel = new Label("Template sheet id:");
    inputForm.add(templateSheetIdLabel, 0, 4);
    templateSheetIdField.setText(configService.getConfigProperty(TEMPLATE_SHEET_ID));
    inputForm.add(templateSheetIdField, 1, 4);
    
    Label imageFolderLabel = new Label("Folder voor afbeeldingen:");
    inputForm.add(imageFolderLabel, 0, 5);
    imageFolderTextField.setText(configService.getConfigProperty(IMAGE_FOLDER));
    inputForm.add(imageFolderTextField, 1, 5);
    inputForm.add(selectImageFolderButton, 2, 5);

    GridPane.setHgrow(backupFolderLabel, Priority.ALWAYS);
    GridPane.setHgrow(backupFolderTextField, Priority.ALWAYS);

    return inputForm;
  }

  public void selectBackupFolder(ActionEvent actionEvent) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    if (configService.getConfigProperty(BACKUP_FOLDER) != null
        && !configService.getConfigProperty(BACKUP_FOLDER).isBlank()) {
      directoryChooser
          .setInitialDirectory(new File(configService.getConfigProperty(BACKUP_FOLDER)));
    }
    File directory = directoryChooser.showDialog(dialog);

    if (directory != null) {
      backupFolderTextField.setText(directory.getAbsolutePath());
    }
  }
  
  public void selectImageFolder(ActionEvent actionEvent) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    if (configService.getConfigProperty(IMAGE_FOLDER) != null
        && !configService.getConfigProperty(IMAGE_FOLDER).isBlank()) {
      directoryChooser
          .setInitialDirectory(new File(configService.getConfigProperty(IMAGE_FOLDER)));
    }
    File directory = directoryChooser.showDialog(dialog);

    if (directory != null) {
      imageFolderTextField.setText(directory.getAbsolutePath());
    }
  }

  public void save(ActionEvent actionEvent) {
    configService.setConfigProperty(BACKUP_FOLDER, backupFolderTextField.getText());
    configService.setConfigProperty(BACKUPS_TO_KEEP, backupsToKeepTextField.getText());
    configService.setConfigProperty(GOOGLE_SPREADSHEET_ID, googleSpreadsheetIdField.getText());
    configService.setConfigProperty(SHOPPING_SHEET_ID, shoppingSheetIdField.getText());
    configService.setConfigProperty(TEMPLATE_SHEET_ID, templateSheetIdField.getText());
    configService.setConfigProperty(IMAGE_FOLDER, imageFolderTextField.getText());
    dialog.close();
  }

}
