package nl.recipes.views.shopping;

import static nl.recipes.views.ViewConstants.CSS_PLANNING_DATE;
import java.util.List;
import org.springframework.stereotype.Component;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.views.components.utils.Utils;

@Component
public class ShoppingPanelProvider {

  public GridPane createShoppingPanel(String header, List<ShoppingItem> shoppingItems,
      boolean showCheckBox) {
    return createShoppingPanel(header, shoppingItems, showCheckBox, null);
  }
  
  public GridPane createShoppingPanel(String header, List<ShoppingItem> shoppingItems,
      boolean showCheckBox, Button button) {
    GridPane shoppingPanel = new GridPane();
    shoppingPanel.setHgap(20);

    Label headerLabel = new Label(header);
    headerLabel.getStyleClass().add(CSS_PLANNING_DATE);
    shoppingPanel.add(headerLabel, 1, 0, 4, 1);

    int row = 1;
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

    if (button != null) {
      final Pane space = new Pane();
      space.minHeightProperty().bind(headerLabel.heightProperty());
      shoppingPanel.add(space, 1, row++);
      shoppingPanel.add(button, 1, row, 4, 1);
    }
    return shoppingPanel;
  }

  private String getMeasureUnitLabel(ShoppingItem shoppingItem) {
    return (shoppingItem.getAmount() == null || shoppingItem.getAmount() <= 1)
        ? shoppingItem.getMeasureUnit().getName()
        : shoppingItem.getMeasureUnit().getPluralName();
  }

  private String getShoppingItemNameLabel(ShoppingItem shoppingItem) {
    if (shoppingItem.getPluralName() == null)
      return shoppingItem.getName();
    return (shoppingItem.getAmount() == null || shoppingItem.getAmount() <= 1)
        ? shoppingItem.getName()
        : shoppingItem.getPluralName();
  }
}
