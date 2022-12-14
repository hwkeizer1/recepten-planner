package nl.recipes.views.components.utils;

import java.net.URL;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.springframework.stereotype.Component;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

@Component
public class ButtonFactory {

  private ButtonFactory() {}

  public static Button createToolBarButton(String iconPath, String toolTipText) {
    URL url = ButtonFactory.class.getResource(iconPath);
    SVGImage image = SVGLoader.load(url).scale(0.4d);
    Button button = new Button();
    button.setGraphic(image);
    button.setMinSize(20, 20);
    button.setPrefSize(20, 20);
    button.setMaxSize(20, 20);
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
