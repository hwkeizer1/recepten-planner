package nl.recipes.views.components.utils;

import java.net.URL;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.springframework.stereotype.Component;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
@Component
public class ToolBarFactory {

  // ToolBar images should be .svg files with size 200x200px
  private static double svgImageSize = 200;
  
  private ToolBarFactory() {}
  
  public static Node createToolBarImage(String imagePath, double size) {
    SVGImage svgImage = createImage(imagePath, size);
    VBox vbox = new VBox();
    double paddingHeight = (size - svgImage.getScaledHeight())/2;
    double paddingWidth = (size - svgImage.getScaledWidth())/2;
    vbox.setPadding(new Insets(paddingHeight, paddingWidth, paddingHeight, paddingWidth));
    vbox.getChildren().add(svgImage);
    vbox.setMinSize(size, size);
    vbox.setPrefSize(size, size);
    vbox.setMaxSize(size, size);
    return vbox;
  }
  
  public static Button createToolBarButton(String iconPath, double size, String toolTipText) {
    SVGImage image = createImage(iconPath, size);
    Button button = new Button();
    button.setGraphic(image);
    button.setMinSize(size, size);
    button.setPrefSize(size, size);
    button.setMaxSize(size, size);
    if (toolTipText == null) return button;
    Tooltip toolTip = new Tooltip(toolTipText);
    toolTip.setShowDelay(new Duration(500));
    button.setTooltip(toolTip);
    return button;
  }
  
  public static SVGImage createImage(String path, double size) {
    URL url = ToolBarFactory.class.getResource(path);
    return SVGLoader.load(url).scale(size/svgImageSize);
  }
}
