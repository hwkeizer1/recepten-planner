package nl.recipes.views.shopping;

import static nl.recipes.views.ViewMessages.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.StandardShoppingItemService;
import nl.recipes.views.components.utils.ButtonFactory;

@Slf4j
@Component
public class SelectStandardShoppingPanel extends ShoppingList
    implements ListChangeListener<ShoppingItem> {

  private final StandardShoppingItemService standardShoppingItemService;

  private ShoppingPanel panel;

  public SelectStandardShoppingPanel(StandardShoppingItemService standardShoppingItemService) {
    this.standardShoppingItemService = standardShoppingItemService;
    this.standardShoppingItemService.addListener(this);

    setComparator((ShoppingItem s1, ShoppingItem s2) -> {
      int n;
      n = s2.isOnList().compareTo(s1.isOnList());
      if (n != 0)
        return n;
      return s1.getName().compareTo(s2.getName());
    });
  }

  @Override
  protected Node view() {
    if (observableList == null) {
      initializeList();
    }
    if (panel == null) {
      panel = new ShoppingPanel.ShoppingPanelBuilder().withHeader(SELECT_STANDARD_SHOPPINGS)
          .withObservableList(observableList).withCheckBoxes(true).withToolBar()
          .withButtons(createToolBarButtonList()).build();
    }
    return panel.view();
  }

  private void initializeList() {
    List<ShoppingItem> standardShoppingList = new ArrayList<>();
    for (ShoppingItem shoppingItem : standardShoppingItemService.getList()) {
      if (standardShoppingList.stream()
          .noneMatch(s -> s.getName().equals(shoppingItem.getName()))) {
        standardShoppingList.add(shoppingItem);
      }
    }
    standardShoppingList.sort(comparator);
    observableList = FXCollections.observableList(standardShoppingList);
  }

  private List<Button> createToolBarButtonList() {
    List<Button> buttons = new ArrayList<>();
    Button button = ButtonFactory.createToolBarButton("/icons/select_all.svg",
        "Selecteer alle standaard boodschappen");
    button.setOnAction(this::selectAllStandardItems);
    buttons.add(button);

    button = ButtonFactory.createToolBarButton("/icons/select_none.svg",
        "Deselecteer alle standaard boodschappen");
    button.setOnAction(this::selectNoneStandardItems);
    buttons.add(button);
    return buttons;
  }

  private void selectAllStandardItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(true);
    }
    panel.refresh();
  }

  private void selectNoneStandardItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(false);
    }
    panel.refresh();
  }

  @Override
  public void onChanged(Change<? extends ShoppingItem> c) {
    if (panel == null)
      return;
    while (c.next()) {
      if (c.wasReplaced()) {
        if (c.getRemovedSize() != 1 || c.getAddedSize() != 1) {
          /* Should not happen */
          log.debug("Unexpected mutations on IngredientNameList");
          return;
        }
        Optional<ShoppingItem> item = observableList.stream()
            .filter(s -> s.getName().equals(c.getRemoved().get(0).getName())).findFirst();
        item.ifPresent(i -> update(i, c.getAddedSubList().get(0)));
        return;
      }

      if (c.wasAdded()) {
        for (ShoppingItem added : c.getAddedSubList()) {
          save(added);
        }
      }

      if (c.wasRemoved()) {
        for (ShoppingItem removed : c.getRemoved()) {
          delete(removed);
        }
      }
    }
  }
}
