package nl.recipes.views.shopping;

import static nl.recipes.views.ViewMessages.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.MeasureUnitService;
import nl.recipes.views.components.utils.ToolBarFactory;

@Component
public class OneTimeShoppingPanel extends ShoppingList {

  private final MeasureUnitService measureUnitService;

  private ShoppingPanel panel;

  public OneTimeShoppingPanel(MeasureUnitService measureUnitService) {
    this.measureUnitService = measureUnitService;
  }

  @Override
  protected Node view() {
    if (observableList == null) {
      observableList = FXCollections.observableList(new ArrayList<ShoppingItem>());
    }
    panel = new ShoppingPanel.ShoppingPanelBuilder().withHeader(SELECT_ONETIME_SHOPPINGS)
        .withObservableList(observableList).withCheckBoxes(true).withToolBar()
        .withButtons(createToolBarButtonList()).build();
    return panel.view();
  }

  private List<Button> createToolBarButtonList() {
    List<Button> buttons = new ArrayList<>();
    Button button =
        ToolBarFactory.createToolBarButton("/icons/add.svg", 20, "Voeg nieuwe eenmalige boodschap toe");
    button.setOnAction(this::addOneTimeShoppingItem);
    buttons.add(button);

    button = ToolBarFactory.createToolBarButton("/icons/remove.svg", 20,
        "Verwijder alle huidige eenmalige boodschappen");
    button.setOnAction(this::deleteAllOneTimeItems);
    buttons.add(button);

    button = ToolBarFactory.createToolBarButton("/icons/select_all.svg", 20,
        "Selecteer alle eenmalige boodschappen");
    button.setOnAction(this::selectAllOneTimeItems);
    buttons.add(button);

    button = ToolBarFactory.createToolBarButton("/icons/select_none.svg", 20,
        "Deselecteer alle eenmalige boodschappen");
    button.setOnAction(this::selectNoneOneTimeItems);
    buttons.add(button);
    return buttons;
  }

  private void addOneTimeShoppingItem(ActionEvent event) {
    AddItemDialog dialog = new AddItemDialog(measureUnitService.getList());
    Optional<ShoppingItem> shoppingItem = dialog.getDialogResult();
    shoppingItem.ifPresent(s -> observableList.add(s));
    panel.refresh();
  }

  private void deleteAllOneTimeItems(ActionEvent event) {
    observableList.clear();
    panel.refresh();
  }

  private void selectAllOneTimeItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(true);
    }
    panel.refresh();
  }

  private void selectNoneOneTimeItems(ActionEvent event) {
    for (ShoppingItem shoppingItem : observableList) {
      shoppingItem.setOnList(false);
    }
    panel.refresh();
  }
}
