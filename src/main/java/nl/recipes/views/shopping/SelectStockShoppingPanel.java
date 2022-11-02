package nl.recipes.views.shopping;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
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

  //TODO: make ingredientList observable and listen for changes here
  @Override
  protected void initializeList() {
    List<ShoppingItem> stockSelectionList = new ArrayList<>();
    for (Ingredient ingredient : planningService.getIngredientList().stream()
        .filter(s -> s.getIngredientName().isStock()).toList()) {
      stockShoppingItemService.findByName(ingredient.getIngredientName().getName())
          .ifPresent(stockSelectionList::add);
    }
    observableList = FXCollections.observableList(stockSelectionList);
  }

  @Override
  protected Node view() {
    if (observableList == null) {
      initializeList();
    }
    return ShoppingPanel.buildWithCheckboxes("Selecteer voorraad boodschappen", observableList);
  }
}
