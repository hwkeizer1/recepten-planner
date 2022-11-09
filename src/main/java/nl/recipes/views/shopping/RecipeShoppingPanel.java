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
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.PlanningService;

@Component
public class RecipeShoppingPanel extends ShoppingList {

  private final PlanningService planningService;

  private GridPane panel;
  
  public RecipeShoppingPanel(PlanningService planningService) {
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
    panel = ShoppingPanel.buildWithCheckboxesAndGeneralButtons("Boodschappen voor recepten", observableList,
        createToolBarButtonList());
    }
    return panel;
  }
  
  private ObservableList<ShoppingItem> createShoppingList() {
    return FXCollections.observableList(planningService.getIngredientList().stream()
        .filter(i -> !i.getIngredientName().isStock())
        .<ShoppingItem>map(i -> new ShoppingItem.ShoppingItemBuilder().withAmount(i.getAmount())
            .withName(i.getIngredientName().getName())
            .withPluralName(i.getIngredientName().getPluralName())
            .withMeasureUnit(i.getIngredientName().getMeasureUnit())
            .withShopType(i.getIngredientName().getShopType())
            .withIngredientType(i.getIngredientName().getIngredientType()).withOnList(true).build())
        .toList());
  }
  
  private List<Button> createToolBarButtonList() {
    List<Button> buttons = new ArrayList<>();
    Button button = ShoppingPanel.createToolBarButton("/icons/select_all.svg", "Selecteer alle boodschappen voor recepten");
    button.setOnAction(this::selectAllRecipeItems);
    buttons.add(button);
    
    button = ShoppingPanel.createToolBarButton("/icons/select_none.svg", "Deselecteer alle boodschappen voor recepten");
    button.setOnAction(this::selectNoneRecipeItems);
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
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
  
  private void selectAllRecipeItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(true);
    }
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
  
  private void selectNoneRecipeItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(false);
    }
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
}
