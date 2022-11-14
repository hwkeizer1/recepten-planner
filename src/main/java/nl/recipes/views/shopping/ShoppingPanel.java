package nl.recipes.views.shopping;

import static nl.recipes.views.ViewConstants.CSS_DROP_SHADOW;
import static nl.recipes.views.ViewConstants.CSS_PLANNING_DATE;
import java.net.URL;
import java.util.List;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.springframework.stereotype.Component;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.views.components.utils.Utils;

@Component
public class ShoppingPanel {

  private ShoppingPanel() {}

  public static GridPane build(String header, ObservableList<ShoppingItem> shoppingItems) {
    return createShoppingPanel(header, shoppingItems, false, null);
  }
  
  public static GridPane buildWithoutToolBar(String header, ObservableList<ShoppingItem> shoppingItems) {
    return createShoppingPanelWithoutToolBar(header, shoppingItems, false, null);
  }

  public static GridPane buildWithCheckboxes(String header, ObservableList<ShoppingItem> shoppingItems) {
    return createShoppingPanel(header, shoppingItems, true, null);
  }

  public static GridPane buildWithCheckboxesAndGeneralButtons(String header,
      ObservableList<ShoppingItem> shoppingItems, List<Button> buttons) {
    return createShoppingPanel(header, shoppingItems, true, buttons);
  }
  
  public static Button createToolBarButton(String iconPath, String toolTipText) {
    URL url = ShoppingPanel.class.getResource(iconPath);
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
    URL url = ShoppingPanel.class.getResource(iconPath);
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

  private static GridPane createShoppingPanel(String header, ObservableList<ShoppingItem> shoppingItems,
      boolean showCheckBox, List<Button> buttons) {
    GridPane shoppingPanel = new GridPane();
    shoppingPanel.setHgap(20);

    Label headerLabel = new Label(header);
    headerLabel.getStyleClass().add(CSS_PLANNING_DATE);
    shoppingPanel.add(headerLabel, 1, 0, 4, 1);
    
    ToolBar toolBar = createToolBar(buttons);
    if (buttons == null) toolBar.setVisible(false);
    shoppingPanel.add(toolBar, 1, 1, 4, 1);

    final Pane space = new Pane();
    space.minHeightProperty().bind(headerLabel.heightProperty().multiply(0.2));
    shoppingPanel.add(space, 1, 2);
    
    updateShoppingItems(shoppingPanel, shoppingItems, showCheckBox);

    return shoppingPanel;
  }
  
  //TODO: Make this DRY!!!
  private static GridPane createShoppingPanelWithoutToolBar(String header, ObservableList<ShoppingItem> shoppingItems,
      boolean showCheckBox, List<Button> buttons) {
    GridPane shoppingPanel = new GridPane();
    shoppingPanel.setHgap(20);

    Label headerLabel = new Label(header);
    headerLabel.getStyleClass().add(CSS_PLANNING_DATE);
    shoppingPanel.add(headerLabel, 1, 0, 4, 1);
    
    updateShoppingItems(shoppingPanel, shoppingItems, showCheckBox);

    return shoppingPanel;
  }
  
  public static void updateShoppingItems(GridPane shoppingPanel, ObservableList<ShoppingItem> shoppingItems, boolean showCheckBox) {
    int row = 3;
    shoppingPanel.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= 3);
    
    for (ShoppingItem shoppingItem : shoppingItems) {
      Label amountLabel =
          new Label(shoppingItem.getAmount() == null ? "" : Utils.format(shoppingItem.getAmount()));
      Label measureUnitLabel =
          new Label(shoppingItem.getMeasureUnit() == null ? "" : getMeasureUnitLabel(shoppingItem));
      Label shoppingItemName = new Label(getShoppingItemNameLabel(shoppingItem));

      shoppingPanel.add(amountLabel, 1, row);
      shoppingPanel.add(measureUnitLabel, 2, row);
      shoppingPanel.add(shoppingItemName, 3, row);

      if (showCheckBox) {
        CheckBox shoppingCheckBox = new CheckBox();
        shoppingCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov,
            Boolean oldValue, Boolean newValue) -> shoppingItem.setOnList(newValue));
        shoppingCheckBox.setSelected(shoppingItem.isOnList());
        shoppingPanel.add(shoppingCheckBox, 4, row);
      }
      row++;
    }
  }

  private static ToolBar createToolBar(List<Button> buttons) {
    ToolBar toolBar = new ToolBar();
    toolBar.setMinHeight(24);
    toolBar.setPrefHeight(24);
    toolBar.setMaxHeight(24);
    toolBar.setPadding(new Insets(2,5,2,5));
    toolBar.getStyleClass().add(CSS_DROP_SHADOW);
    if (buttons != null) {
      toolBar.getItems().addAll(buttons);
    }
    return toolBar;
  }

  private static String getMeasureUnitLabel(ShoppingItem shoppingItem) {
    return (shoppingItem.getAmount() == null || shoppingItem.getAmount() <= 1)
        ? shoppingItem.getMeasureUnit().getName()
        : shoppingItem.getMeasureUnit().getPluralName();
  }

  private static String getShoppingItemNameLabel(ShoppingItem shoppingItem) {
    if (shoppingItem.getPluralName() == null)
      return shoppingItem.getName();
    return (shoppingItem.getAmount() == null || shoppingItem.getAmount() <= 1)
        ? shoppingItem.getName()
        : shoppingItem.getPluralName();
  }
}
