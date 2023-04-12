package nl.recipes.services;

import static nl.recipes.views.ViewConstants.CSS_DROP_SHADOW;
import static nl.recipes.views.ViewMessages.IMAGE_FOLDER;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Recipe;

@Slf4j
@Service
public class ImageService {

  private static final String EMPTY_STRING = "";

  private final ConfigService configService;

  public ImageService(ConfigService configService) {
    this.configService = configService;
  }

  public String selectImage(String recipeName, String selectedImage) {
    Path selectedImagePath = Paths.get(selectedImage);

    Path newImagePath = Paths.get(configService.getConfigProperty(IMAGE_FOLDER),
        recipeName + getFileExtensionWithDot(selectedImagePath.getFileName().toString()));
   
      try {
        moveExistingFileToDeleteFolder(newImagePath.getFileName().toString());
        Files.copy(selectedImagePath, newImagePath);
      } catch (IOException e) {
        log.error("Could not copy " + selectedImagePath.toString() + " to " + newImagePath.toString());
      }
    return newImagePath.getFileName().toString();
  }

  private void moveExistingFileToDeleteFolder(String fileName) throws IOException {
    if (filenameAlreadyExists(fileName)) {
      Path deletePath = getDeletePath();

      Path existingFile = Paths.get(configService.getConfigProperty(IMAGE_FOLDER), fileName);
      Path deletedFile = Paths.get(deletePath.toString(), fileName);
      try {
        Files.move(existingFile, deletedFile, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        log.error("Could not move " + existingFile.toString() + " to " + deletedFile.toString());
      }
    }
  }

  private Path getDeletePath() throws IOException {
    Path deletePath = Paths.get(configService.getConfigProperty(IMAGE_FOLDER), "deleted");
    if (!Files.exists(deletePath)) {
      Files.createDirectory(deletePath);
    }
    return deletePath;
  }

  public ImageView loadRoundedImage(ImageView imageView, Recipe recipe) {
    Image image = new Image(loadRecipeImageUrl(recipe), 300, 300, true, false);
    imageView.setImage(image);
    imageView.getStyleClass().add(CSS_DROP_SHADOW);

    Rectangle clip = new Rectangle();
    clip.setWidth(300.0);
    clip.setHeight(300.0);

    clip.setArcHeight(20);
    clip.setArcWidth(20);
    clip.setStroke(Color.BLACK);
    imageView.setClip(clip);

    SnapshotParameters parameters = new SnapshotParameters();
    parameters.setFill(Color.TRANSPARENT);
    WritableImage writeableImage = imageView.snapshot(parameters, null);

    imageView.setClip(null);
    imageView.setImage(writeableImage);
    return imageView;
  }

  private String loadRecipeImageUrl(Recipe recipe) {
    if (recipe == null || recipe.getImage() == null) {
      return "file:" + configService.getConfigProperty(IMAGE_FOLDER) + "/" + "no-image.png";
    } else {
      return "file:" + configService.getConfigProperty(IMAGE_FOLDER) + "/" + recipe.getImage();
    }
  }

  boolean filenameAlreadyExists(String filename) {
    return listImagesInImageFolder().stream().anyMatch(f -> getFilenameWithoutExtension(filename).equals(getFilenameWithoutExtension(f)));
  }

  private String getFilenameWithoutExtension(String filename) {
    if (!filename.contains("."))
      return filename;
    return filename.substring(0, filename.lastIndexOf("."));
  }

  private String getFileExtensionWithDot(String filename) {
    if (!filename.contains("."))
      return EMPTY_STRING;
    return filename.substring(filename.lastIndexOf("."));
  }

  Set<String> listImagesInImageFolder() {
    return Stream.of(new File(configService.getConfigProperty(IMAGE_FOLDER)).listFiles()).filter(file -> !file.isDirectory())
        .map(File::getName).collect(Collectors.toSet());
  }
}
