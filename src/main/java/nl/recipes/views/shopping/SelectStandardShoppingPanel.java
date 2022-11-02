package nl.recipes.views.shopping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.StandardShoppingItemService;

@Component
public class SelectStandardShoppingPanel extends ShoppingList {

  private final StandardShoppingItemService standardShoppingItemService;
  
  private GridPane panel;
  
  public SelectStandardShoppingPanel(StandardShoppingItemService standardShoppingItemService) {
    this.standardShoppingItemService = standardShoppingItemService;
  }

  @Override
  protected void initializeList() {
    List<ShoppingItem> standardShoppingList = new ArrayList<>();
    for (ShoppingItem shoppingItem : standardShoppingItemService.getList()) {
      if (standardShoppingList.stream()
          .noneMatch(s -> s.getName().equals(shoppingItem.getName()))) {
        standardShoppingList.add(shoppingItem);
      }
    }
    standardShoppingList.sort(Comparator.comparing(ShoppingItem::isOnList).reversed()
        .thenComparing(ShoppingItem::getName));
    observableList = FXCollections.observableList(standardShoppingList);
  }

  @Override
  protected Node view() {
    if (observableList == null) {
      initializeList();
    }
    if (panel == null) {
    panel = ShoppingPanel.buildWithCheckboxesAndGeneralButtons("Selecteer standaard boodschappen", observableList,
        createToolBarButtonList());
    }
    return panel;
  }
  
  private List<Button> createToolBarButtonList() {
    List<Button> buttons = new ArrayList<>();
    Button button = ShoppingPanel.createToolBarButton("/icons/select_all.svg", "Selecteer alle standaard boodschappen");
    button.setOnAction(this::selectAllStandardItems);
    buttons.add(button);
    
    button = ShoppingPanel.createToolBarButton("/icons/select_none.svg", "Deselecteer alle standaard boodschappen");
    button.setOnAction(this::selectNoneStandardItems);
    buttons.add(button);
    return buttons;
  }
  
  private void selectAllStandardItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(true);
    }
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
  
  private void selectNoneStandardItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(false);
    }
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }

}
