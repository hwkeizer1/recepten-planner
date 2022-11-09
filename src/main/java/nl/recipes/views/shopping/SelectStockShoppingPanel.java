package nl.recipes.views.shopping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.PlanningService;
import nl.recipes.services.StockShoppingItemService;

@Component
public class SelectStockShoppingPanel extends ShoppingList {

  private final StockShoppingItemService stockShoppingItemService;
  private final PlanningService planningService;

  private GridPane panel;
  
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
    if (panel == null) {
      panel = ShoppingPanel.buildWithCheckboxesAndGeneralButtons("Selecteer voorraad boodschappen", observableList,
        createToolBarButtonList());
    }
    return panel;
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
  
  private List<Button> createToolBarButtonList() {
    List<Button> buttons = new ArrayList<>();
    Button button = ShoppingPanel.createToolBarButton("/icons/select_all.svg", "Selecteer alle voorraad boodschappen");
    button.setOnAction(this::selectAllStockingItems);
    buttons.add(button);
    
    button = ShoppingPanel.createToolBarButton("/icons/select_none.svg", "Deselecteer alle voorraad boodschappen");
    button.setOnAction(this::selectNoneStockingItems);
    buttons.add(button);
    return buttons;
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
  
  private void selectAllStockingItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(true);
    }
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
  
  private void selectNoneStockingItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(false);
    }
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
}
