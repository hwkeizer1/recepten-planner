package nl.recipes.views.shopping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.PlanningService;
import nl.recipes.services.StockShoppingItemService;

@Component
public class SelectStockShoppingPanel extends ShoppingList {

  private final StockShoppingItemService stockShoppingItemService;
  private final PlanningService planningService;

  public SelectStockShoppingPanel(StockShoppingItemService stockShoppingItemService,
      PlanningService planningService) {
    this.stockShoppingItemService = stockShoppingItemService;
    this.planningService = planningService;
  }

  @Override
  protected Node view() {
    if (observableList == null) {
      observableList = createShoppingList();
    } else {
      updateShoppingList();
    }
    return ShoppingPanel.buildWithCheckboxes("Selecteer voorraad boodschappen", observableList);
  }
  
  private ObservableList<ShoppingItem> createShoppingList() {
    List<ShoppingItem> stockSelectionList = new ArrayList<>();
    for (Ingredient ingredient : planningService.getIngredientList().stream()
        .filter(s -> s.getIngredientName().isStock()).toList()) {
      stockShoppingItemService.findByName(ingredient.getIngredientName().getName())
          .ifPresent(stockSelectionList::add);
    }
    return FXCollections.observableList(stockSelectionList);
  }
  
  private boolean nameAndMeasureUnitAreEqual(ShoppingItem a, ShoppingItem b) {
    if (a.getMeasureUnit() == null) {
      return (a.getName().equals(b.getName()) && b.getMeasureUnit() == null);
    }
    if (b.getMeasureUnit() == null)
      return false;
    return (a.getName().equals(b.getName())
        && a.getMeasureUnit().getName().equals(b.getMeasureUnit().getName()));
  }
  
  private void updateShoppingList() {
    ObservableList<ShoppingItem> newList = createShoppingList();
    for (ShoppingItem shoppingItem : newList) {
      Optional<ShoppingItem> optionalShoppingItem = observableList.stream()
          .filter(s -> nameAndMeasureUnitAreEqual(s, shoppingItem)).findFirst();
      optionalShoppingItem.ifPresent(s -> shoppingItem.setOnList(s.isOnList()));
    }
    observableList = newList;
  }
}
