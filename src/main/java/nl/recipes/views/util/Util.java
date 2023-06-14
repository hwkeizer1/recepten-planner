package nl.recipes.views.util;

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class Util {

  public static void setColor(Region node, Color color) {
    node.setBackground(new javafx.scene.layout.Background(
        new javafx.scene.layout.BackgroundFill(color,
            javafx.scene.layout.CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
  }
}
