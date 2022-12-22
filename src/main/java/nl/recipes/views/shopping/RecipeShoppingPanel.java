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
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.PlanningService;
import nl.recipes.views.components.utils.ToolBarFactory;

@Component
public class RecipeShoppingPanel extends ShoppingList {

  private final PlanningService planningService;

  private ShoppingPanel panel;

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
      panel = new ShoppingPanel.ShoppingPanelBuilder().withHeader(SELECT_RECIPE_SHOPPINGS)
          .withObservableList(observableList).withCheckBoxes(true).withToolBar()
          .withButtons(createToolBarButtonList()).build();
    }
    return panel.view();
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
    Button button = ToolBarFactory.createToolBarButton("/icons/select_all.svg", 20,
        "Selecteer alle boodschappen voor recepten");
    button.setOnAction(this::selectAllRecipeItems);
    buttons.add(button);

    button = ToolBarFactory.createToolBarButton("/icons/select_none.svg", 20,
        "Deselecteer alle boodschappen voor recepten");
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
    panel.refresh(observableList);
  }

  private void selectAllRecipeItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(true);
    }
    panel.refresh();
  }

  private void selectNoneRecipeItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(false);
    }
    panel.refresh();
  }
}
