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
public class ButtonFactory {

  private ButtonFactory() {}
  
  public static Node createToolBarImage(String imagePath, double size) {
    URL url = ButtonFactory.class.getResource(imagePath);
    SVGImage svgImage = SVGLoader.load(url).scale(0.1);
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

  public static Button createToolBarButton(String iconPath) {
    return createToolBarButton(iconPath, null);
  }
  
  public static Button createToolBarButton(String iconPath, String toolTipText) {
    URL url = ButtonFactory.class.getResource(iconPath);
    SVGImage image = SVGLoader.load(url).scale(0.1);
    Button button = new Button();
    button.setGraphic(image);
    button.setMinSize(20, 20);
    button.setPrefSize(20, 20);
    button.setMaxSize(20, 20);
    if (toolTipText == null) return button;
    Tooltip toolTip = new Tooltip(toolTipText);
    toolTip.setShowDelay(new Duration(500));
    button.setTooltip(toolTip);
    return button;
  }

  public static Button createLargeToolBarButton(String iconPath, String toolTipText) {
    URL url = ButtonFactory.class.getResource(iconPath);
    SVGImage image = SVGLoader.load(url).scale(0.15d);
    Button button = new Button();
    button.setGraphic(image);
    button.setMinSize(30, 30);
    button.setPrefSize(30, 30);
    button.setMaxSize(30, 30);
    Tooltip toolTip = new Tooltip(toolTipText);
    toolTip.setShowDelay(new Duration(500));
    button.setTooltip(toolTip);
    return button;
  }
}
