package nl.recipes.views.shopping;

import static nl.recipes.views.ViewMessages.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.StandardShoppingItemService;
import nl.recipes.views.components.utils.ToolBarFactory;

@Slf4j
@Component
public class SelectStandardShoppingPanel extends ShoppingList
    implements ListChangeListener<ShoppingItem> {

  private final StandardShoppingItemService standardShoppingItemService;

  private ShoppingPanel panel;
  
  private TextField searchFilter;
  private FilteredList<ShoppingItem> filteredList;

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
  
  /*
   * public update of list will only be used when item is highlighted because of duplication
   */
  public void updateShoppingListHighlighted() {
    panel.refresh();
  }

  @Override
  protected Node view() {
    if (observableList == null) {
      initializeList();
    }
    if (panel == null) {
      panel = new ShoppingPanel.ShoppingPanelBuilder().withHeader(SELECT_STANDARD_SHOPPINGS)
          .withObservableList(filteredList).withCheckBoxes(true).withToolBar()
          .withButtons(createToolBarButtonList())
          .withSearchFilter(createSearchFilter()).build();
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
    observableList = new FilteredList<>(FXCollections.observableList(standardShoppingList));
    filteredList = new FilteredList<>(observableList);
  }

  private List<Button> createToolBarButtonList() {
    List<Button> buttons = new ArrayList<>();
    Button button = ToolBarFactory.createToolBarButton("/icons/select_all.svg", 20,
        "Selecteer alle standaard boodschappen");
    button.setOnAction(this::selectAllStandardItems);
    buttons.add(button);

    button = ToolBarFactory.createToolBarButton("/icons/select_none.svg", 20, 
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
  
  private HBox createSearchFilter() {
    int searchFilterHeight = 20;
    HBox searchFilterBox = new HBox();
    searchFilterBox.getChildren().add(ToolBarFactory.createToolBarImage("/icons/filter.svg", searchFilterHeight));
    
    searchFilter = new TextField();
    searchFilter.setMaxHeight(searchFilterHeight);
    searchFilter.setMinHeight(searchFilterHeight);
    searchFilter.setPrefHeight(Region.USE_COMPUTED_SIZE);
    searchFilter.setMaxWidth(142);
    searchFilter.setMinWidth(142);
    searchFilter.setPrefWidth(Region.USE_COMPUTED_SIZE);
    searchFilterBox.getChildren().add(searchFilter);
    searchFilter.textProperty().addListener(
        (observable, oldValue, newValue) -> filteredList.setPredicate(createPredicate(newValue)));

    Button clear = ToolBarFactory.createToolBarButton("/icons/filter-remove.svg", searchFilterHeight,
        "Verwijder filter text");
    clear.setOnAction(this::clearSearch);
    
    searchFilterBox.getChildren().add(clear);
    return searchFilterBox;
  }
  
  private Predicate<ShoppingItem> createPredicate(String searchText) {
    return shopping -> {
      if (searchText == null || searchText.isEmpty())
        return true;
      return searchFindShopping(shopping, searchText);
    };
  }
  
  private boolean searchFindShopping(ShoppingItem shoppingItem, String searchText) {
    return (shoppingItem.getName().toLowerCase().contains(searchText.toLowerCase()))
        || (shoppingItem.getPluralName().toLowerCase()
            .contains(searchText.toLowerCase()));
  }
  
  private void clearSearch(ActionEvent event) {
    searchFilter.clear();
  }
}
