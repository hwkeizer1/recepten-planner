package nl.recipes.views.backup;

import static nl.recipes.views.ViewConstants.*;

import org.springframework.stereotype.Component;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import nl.recipes.services.BackupService;
import nl.recipes.services.ConfigService;

@Component
public class CreateBackupDialog {

  private final BackupService backupService;

  private final ConfigService configService;

  public CreateBackupDialog(BackupService backupService, ConfigService configService) {
    this.backupService = backupService;
    this.configService = configService;
  }

  public void createBackup() {
    if (configService.getConfigProperty(BACKUP_FOLDER) != null) {
      backupService.backup(configService.getConfigProperty(BACKUP_FOLDER));
    } else {
      showNoBackupLocationError();
    }
  }

  private void showNoBackupLocationError() {

    Alert noBackupLocationError = new Alert(AlertType.ERROR);
    noBackupLocationError.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    noBackupLocationError.initModality(Modality.WINDOW_MODAL);
    noBackupLocationError.setTitle("Kan geen backup maken!");
    noBackupLocationError.setHeaderText("Backup lokatie is niet ingesteld");
    noBackupLocationError.setContentText(
        "Er is nog geen backup lokatie ingesteld. Stel deze eerst in via 'Instellingen  wijzigen'");
    noBackupLocationError.show();

  }

}
