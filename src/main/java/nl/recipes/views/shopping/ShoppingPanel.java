package nl.recipes.views.shopping;

import static nl.recipes.views.ViewConstants.CSS_PLANNING_DATE;
import java.util.List;
import org.springframework.stereotype.Component;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.views.components.utils.Utils;

@Component
public class ShoppingPanel {

  public static class builder {
    private String header;
    private List<ShoppingItem> shoppingItems;

    public builder withHeader(String header) {
      this.header = header;
      return this;
    }

    public builder withList(List<ShoppingItem> shoppingItems) {
      this.shoppingItems = shoppingItems;
      return this;
    }

    public GridPane build() {
      GridPane panel = new GridPane();
      addHeader(panel, header);
      addList(panel, shoppingItems);
      return panel;
    }

    private void addHeader(GridPane panel, String header) {
      Label headerLabel = new Label(header);
      headerLabel.getStyleClass().add(CSS_PLANNING_DATE);
      panel.add(headerLabel, 1, 0, 4, 1);
    }

    private void addList(GridPane panel, List<ShoppingItem> shoppingItems) {
      int row = 1;
      for (ShoppingItem shoppingItem : shoppingItems) {
        Label amountLabel = new Label(
            shoppingItem.getAmount() == null ? "" : Utils.format(shoppingItem.getAmount()));
        Label measureUnitLabel = new Label(
            shoppingItem.getMeasureUnit() == null ? "" : getMeasureUnitLabel(shoppingItem));
        Label shoppingItemName = new Label(getShoppingItemNameLabel(shoppingItem));

        panel.add(amountLabel, 1, row);
        panel.add(measureUnitLabel, 2, row);
        panel.add(shoppingItemName, 3, row);

        // if (showCheckBox) {
        // CheckBox shoppingCheckBox = new CheckBox();
        // shoppingCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov,
        // Boolean oldValue, Boolean newValue) -> shoppingItem.setOnList(newValue));
        // shoppingCheckBox.setSelected(shoppingItem.isOnList());
        // shoppingPanel.add(shoppingCheckBox, 4, row);
        // }
        row++;
      }
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

}
