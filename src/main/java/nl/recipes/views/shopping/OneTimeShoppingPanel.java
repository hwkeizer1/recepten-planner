package nl.recipes.views.shopping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.MeasureUnitService;

@Slf4j
@Component
public class OneTimeShoppingPanel extends ShoppingList {

  private final MeasureUnitService measureUnitService;
  
  private GridPane panel;
  
  public OneTimeShoppingPanel(MeasureUnitService measureUnitService) {
    this.measureUnitService = measureUnitService;
  }

  @Override
  protected Node view() {
    if (observableList == null) {
      observableList = FXCollections.observableList(new ArrayList<ShoppingItem>()) ;
    }
    if (panel == null) {
    panel = ShoppingPanel.buildWithCheckboxesAndGeneralButtons("Selecteer eenmalige boodschappen", observableList,
        createToolBarButtonList());
    }
    return panel;
  }

  private List<Button> createToolBarButtonList() {
    List<Button> buttons = new ArrayList<>();
    Button button = ShoppingPanel.createToolBarButton("/icons/add.svg", "Voeg nieuwe eenmalige boodschap toe");
    button.setOnAction(this::addOneTimeShoppingItem);
    buttons.add(button);
    
    button = ShoppingPanel.createToolBarButton("/icons/remove.svg", "Verwijder alle huidige eenmalige boodschappen");
    button.setOnAction(this::deleteAllOneTimeItems);
    buttons.add(button);
    
    button = ShoppingPanel.createToolBarButton("/icons/select_all.svg", "Selecteer alle eenmalige boodschappen");
    button.setOnAction(this::selectAllOneTimeItems);
    buttons.add(button);
    
    button = ShoppingPanel.createToolBarButton("/icons/select_none.svg", "Deselecteer alle eenmalige boodschappen");
    button.setOnAction(this::selectNoneOneTimeItems);
    buttons.add(button);
    return buttons;
  }
  
  private void addOneTimeShoppingItem(ActionEvent event) {
    AddItemDialog dialog = new AddItemDialog(measureUnitService.getList());
    Optional<ShoppingItem> shoppingItem = dialog.getDialogResult();
    shoppingItem.ifPresent(s -> observableList.add(s));
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
  
  private void deleteAllOneTimeItems(ActionEvent event) {
    observableList.clear();
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
  
  private void selectAllOneTimeItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(true);
    }
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
  
  private void selectNoneOneTimeItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(false);
    }
    ShoppingPanel.updateShoppingItems(panel, observableList, true);
  }
}
