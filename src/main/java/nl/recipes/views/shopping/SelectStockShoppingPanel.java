package nl.recipes.views.shopping;

import static nl.recipes.views.ViewMessages.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.PlanningService;
import nl.recipes.services.StockShoppingItemService;
import nl.recipes.views.components.utils.ButtonFactory;

@Component
public class SelectStockShoppingPanel extends ShoppingList {

  private final StockShoppingItemService stockShoppingItemService;
  private final PlanningService planningService;

  private ShoppingPanel panel;

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
      panel = new ShoppingPanel.ShoppingPanelBuilder().withHeader(SELECT_STOCK_SHOPPINGS)
          .withObservableList(observableList).withCheckBoxes(true).withToolBar()
          .withButtons(createToolBarButtonList()).build();
    }
    return panel.view();
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
    Button button = ButtonFactory.createToolBarButton("/icons/select_all.svg",
        "Selecteer alle voorraad boodschappen");
    button.setOnAction(this::selectAllStockingItems);
    buttons.add(button);

    button = ButtonFactory.createToolBarButton("/icons/select_none.svg",
        "Deselecteer alle voorraad boodschappen");
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
    panel.refresh(observableList);
  }
  
  /*
   * public update of list will only be used when item is removed because of duplication
   */
  public void updateShoppingListRemoval() {
    panel.refresh();
  }

  private void selectAllStockingItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(true);
    }
    panel.refresh();
  }

  private void selectNoneStockingItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(false);
    }
    panel.refresh();
  }
}
