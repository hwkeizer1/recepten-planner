package nl.recipes.services;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

  public String selectImage(String recipeName, String selectedImage) throws IOException {
    Path selectedImagePath = Paths.get(selectedImage);

    // Special case where selected image is the deleted image for the same recipe.
    // The selectedImage is renamed to a temporary file and used in the normal way
    if (selectedImagePath.equals(Paths.get(getDeletePath().toString(), selectedImagePath.getFileName().toString()))) {
      Path newSelectedImagePath =
          Paths.get(getDeletePath().toString(), "temp" + getFileExtensionWithDot(selectedImagePath.getFileName().toString()));
      Files.move(selectedImagePath, newSelectedImagePath, StandardCopyOption.REPLACE_EXISTING);
      selectedImagePath = newSelectedImagePath;
    }

    Path newImagePath = Paths.get(configService.getConfigProperty(IMAGE_FOLDER),
        recipeName + getFileExtensionWithDot(selectedImagePath.getFileName().toString()));

    try {
      moveFileToDeleteFolderIfExists(newImagePath.getFileName().toString());
      Files.copy(selectedImagePath, newImagePath);
    } catch (IOException e) {
      log.error("Could not copy " + selectedImagePath.toString() + " to " + newImagePath.toString());
    }
    return newImagePath.getFileName().toString();
  }

  public void moveFileToDeleteFolderIfExists(String fileName) throws IOException {
    if (fileName == null)
      return;
    if (filenameAlreadyExists(fileName)) {

      Path existingFile = Paths.get(configService.getConfigProperty(IMAGE_FOLDER), fileName);
      Path deletedFile = Paths.get(getDeletePath().toString(), fileName);
      try {
        Files.move(existingFile, deletedFile, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        log.error("Could not move " + existingFile.toString() + " to " + deletedFile.toString());
      }
    }
  }

  public ImageView loadRecipeImage(ImageView imageView, Recipe recipe) {
    return loadRecipeImage(imageView, recipe, 300d);
  }

  public ImageView loadRecipeImage(ImageView imageView, Recipe recipe, Double width) {
    if (recipe != null) {
      return loadImage(imageView, recipe.getImage(), width);
    }
    return loadImage(imageView, null, width);
  }

  public ImageView loadImage(ImageView imageView, String fileName, Double width) {
    if (fileName == null || fileName.isEmpty())
      return imageView;
    Image image = new Image(loadRecipeImageUrl(fileName));
    imageView.setImage(image);
    imageView.setFitWidth(width);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setCache(false);
    return imageView;
  }

  public String renameImageFileName(String oldName, String newName) {
    String file = getFilenameWithoutExtension(newName) + getFileExtensionWithDot(oldName);
    try {
      Files.move(Paths.get(configService.getConfigProperty(IMAGE_FOLDER), oldName),
          Paths.get(configService.getConfigProperty(IMAGE_FOLDER), file));
    } catch (IOException e) {
      // Replace with proper error handling
      e.printStackTrace();
    }
    return file;
  }

  private Path getDeletePath() throws IOException {
    Path deletePath = Paths.get(configService.getConfigProperty(IMAGE_FOLDER), "deleted");
    if (!Files.exists(deletePath)) {
      Files.createDirectory(deletePath);
    }
    return deletePath;
  }

  private String loadRecipeImageUrl(String image) {
    return "file:" + configService.getConfigProperty(IMAGE_FOLDER) + "/" + image;

  }

  /**
   * Validate if the image name is equal to the recipename
   * 
   * @param recipe
   * @return
   */
  public boolean validateImageName(Recipe recipe) {
    if (recipe.getImage() == null || recipe.getImage().isEmpty())
      return true;
    return recipe.getName().equals(getFilenameWithoutExtension(recipe.getImage()));
  }

  boolean filenameAlreadyExists(String filename) {
    return listImagesInImageFolder().stream().anyMatch(f -> getFilenameWithoutExtension(filename).equals(getFilenameWithoutExtension(f)));
  }

  public String getFilenameWithoutExtension(String filename) {
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
