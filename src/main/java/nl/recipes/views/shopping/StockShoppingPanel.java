package nl.recipes.views.shopping;

import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.PlanningService;

@Component
public class StockShoppingPanel extends ShoppingList {

  private final PlanningService planningService;
  
  public StockShoppingPanel(PlanningService planningService) {
    this.planningService = planningService;
  }

  //TODO: make ingredientList observable and listen for changes here
  @Override
  protected void initializeList() {
    observableList = FXCollections.observableList(planningService.getIngredientList().stream()
        .filter(i -> i.getIngredientName().isStock())
        .<ShoppingItem>map(i -> new ShoppingItem.ShoppingItemBuilder().withAmount(i.getAmount())
            .withName(i.getIngredientName().getName())
            .withPluralName(i.getIngredientName().getPluralName())
            .withMeasureUnit(i.getIngredientName().getMeasureUnit())
            .withShopType(i.getIngredientName().getShopType())
            .withIngredientType(i.getIngredientName().getIngredientType()).withOnList(true).build())
        .toList());
    
  }

  @Override
  protected Node view() {
    if (observableList == null) {
      initializeList();
    }
    return ShoppingPanel.build("Benodigd uit voorraad", observableList);
  }

}
