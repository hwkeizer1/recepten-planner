package nl.recipes.views.shopping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.GoogleSheetService;
import nl.recipes.views.components.utils.ToolBarFactory;

@Slf4j
@Component
public class ShoppingPage {

  private final OneTimeShoppingPanel oneTimeShoppingPanel;
  private final RecipeShoppingPanel recipeShoppingPanel;
  private final SelectStandardShoppingPanel selectStandardShoppingPanel;
  private final SelectStockShoppingPanel selectStockShoppingPanel;
  private final StockShoppingPanel stockShoppingPanel;
  private final GoogleSheetService googleSheetService;

  Button cloudButton;
  List<ShoppingItem> finalShoppingList;
  HBox finalShoppingPanels;
  

  Comparator<ShoppingItem> comparator =
      (t1, t2) -> t1.getIngredientType().compareTo(t2.getIngredientType());

  public ShoppingPage(OneTimeShoppingPanel oneTimeShoppingPanel,
      RecipeShoppingPanel recipeShoppingPanel,
      SelectStandardShoppingPanel selectStandardShoppingPanel,
      SelectStockShoppingPanel selectStockShoppingPanel, StockShoppingPanel stockShoppingPanel,
      GoogleSheetService googleSheetService) {
    this.oneTimeShoppingPanel = oneTimeShoppingPanel;
    this.recipeShoppingPanel = recipeShoppingPanel;
    this.selectStandardShoppingPanel = selectStandardShoppingPanel;
    this.selectStockShoppingPanel = selectStockShoppingPanel;
    this.stockShoppingPanel = stockShoppingPanel;
    this.googleSheetService = googleSheetService;
  }

  public ScrollPane view() {
    VBox shoppingPanel = new VBox();
    AnchorPane anchorPane = new AnchorPane(shoppingPanel);
    ScrollPane scrollPane = new ScrollPane();
    
    shoppingPanel.setPadding(new Insets(0));
    shoppingPanel.setSpacing(20);
    shoppingPanel.getChildren().addAll(getToolBar(scrollPane), getShoppingPanels(), getFinalPanels());


    scrollPane.setPannable(true);
    scrollPane.setContent(anchorPane);
    
    return scrollPane;
  }

  private ToolBar getToolBar(ScrollPane anchorPane) {
    ToolBar toolBar = new ToolBar();
    toolBar.prefWidthProperty().bind(anchorPane.widthProperty());
    Button listButton =
        ToolBarFactory.createToolBarButton("/icons/list.svg", 30, "Genereer boodschappenlijst");
    listButton.setOnAction(this::createFinalShoppingPanels);
    toolBar.getItems().add(listButton);

    cloudButton = ToolBarFactory.createToolBarButton("/icons/cloud.svg", 30,
        "Upload boodschappenlijst naar Google sheets");
    cloudButton.setOnAction(this::sendShoppingListToGoogle);
    cloudButton.setVisible(false);
    toolBar.getItems().add(cloudButton);
    return toolBar;
  }

  private HBox getShoppingPanels() {
    HBox shoppingPanels = new HBox();
    shoppingPanels.setPadding(new Insets(10));
    shoppingPanels.setSpacing(10);
    shoppingPanels.getChildren().addAll(recipeShoppingPanel.view(), stockShoppingPanel.view(),
        selectStockShoppingPanel.view(), selectStandardShoppingPanel.view(),
        oneTimeShoppingPanel.view());
    highlightDuplicates();
    return shoppingPanels;
  }
  
  /*
   * Check if selectStock items exist also on selectStandard list
   * if true, remove from select stock list and highlight on selectStandard list
   */
  private void highlightDuplicates() {
    resetHighLighting();
    if (selectStockShoppingPanel.getList() == null) return; // no duplicates
    for (ShoppingItem standardItem : selectStandardShoppingPanel.getList()) {
      for (Iterator<ShoppingItem> iterator = selectStockShoppingPanel.getList().iterator(); iterator.hasNext();) {
        ShoppingItem stockItem = iterator.next();
        if (standardItem.getName().equals(stockItem.getName())) {
          standardItem.setHighlight(true);
          iterator.remove();
        }
      }
    }
    selectStockShoppingPanel.updateShoppingListRemoval();
    selectStandardShoppingPanel.updateShoppingListHighlighted();
  }
  
  private void resetHighLighting() {
    for (ShoppingItem standardItem: selectStandardShoppingPanel.getList()) {
      standardItem.setHighlight(false);
    }
  }
  
  private HBox getFinalPanels() {
    finalShoppingPanels = new HBox();
    return finalShoppingPanels;
  }

  private void createFinalShoppingPanels(ActionEvent event) {
    createFinalShoppingList();
    finalShoppingPanels.getChildren().clear();
    finalShoppingPanels.setPadding(new Insets(15));
    finalShoppingPanels.setSpacing(30);
    finalShoppingPanels.getChildren().addAll(
        createFinalShoppingPanel("Eko plaza", createEkoShoppingList()).view(),
        createFinalShoppingPanel("DEKA", createDekaShoppingList()).view(),
        createFinalShoppingPanel("Markt", createMarktShoppingList()).view(),
        createFinalShoppingPanel("Other", createOtherShoppingList()).view());
    cloudButton.setVisible(true);
  }

  private ShoppingPanel createFinalShoppingPanel(String header,
      ObservableList<ShoppingItem> shoppingList) {
    return new ShoppingPanel.ShoppingPanelBuilder().withHeader(header)
        .withObservableList(shoppingList).build();
  }

  private void createFinalShoppingList() {
    finalShoppingList = new ArrayList<>();
    finalShoppingList.addAll(recipeShoppingPanel.getList());
    finalShoppingList.addAll(selectStockShoppingPanel.getList());
    finalShoppingList.addAll(selectStandardShoppingPanel.getList());
    finalShoppingList.addAll(oneTimeShoppingPanel.getList());
    finalShoppingList.forEach(s -> s.setHighlight(false));
  }

  private void sendShoppingListToGoogle(ActionEvent event) {
    try {
      googleSheetService.setEkoShoppings(createEkoShoppingList());
      googleSheetService.setDekaShoppings(createDekaShoppingList());
      googleSheetService.setMarktShoppings(createMarktShoppingList());
      googleSheetService.setOtherShoppings(createOtherShoppingList());
    } catch (IOException ex) {
      googleSheetService.deleteStoredCredentials();
    }
    if (!googleSheetService.credentialsValid()) {
      googleSheetService.deleteStoredCredentials();
      showCredentialsAlert();
      googleSheetService.createSheetService();
    }
  }

  private void showCredentialsAlert() {
    Alert a = new Alert(AlertType.WARNING);
    a.initModality(Modality.WINDOW_MODAL);
    a.setTitle("Waarschuwing");
    a.setHeaderText("Google credentials niet gevonden of verlopen");
    a.setContentText(
        "Sluit eerst dit waarschuwingsvenster, open je browser, ga naar 'inloggen met Google account', "
            + "selecteer het juiste account en bevestig dat je wilt doorgaan.");
    a.showAndWait();
  }

  private ObservableList<ShoppingItem> createEkoShoppingList() {
    ObservableList<ShoppingItem> list = FXCollections.observableArrayList(finalShoppingList.stream()
        .filter(ShoppingItem::isOnList).filter(s -> s.getShopType().equals(ShopType.EKO)).toList());
    Collections.sort(list, comparator);
    return list;
  }

  private ObservableList<ShoppingItem> createDekaShoppingList() {
    ObservableList<ShoppingItem> list =
        FXCollections.observableArrayList(finalShoppingList.stream().filter(ShoppingItem::isOnList)
            .filter(s -> s.getShopType().equals(ShopType.DEKA)).toList());
    Collections.sort(list, comparator);
    return list;
  }

  private ObservableList<ShoppingItem> createMarktShoppingList() {
    ObservableList<ShoppingItem> list =
        FXCollections.observableArrayList(finalShoppingList.stream().filter(ShoppingItem::isOnList)
            .filter(s -> s.getShopType().equals(ShopType.MARKT)).toList());
    Collections.sort(list, comparator);
    return list;
  }

  private ObservableList<ShoppingItem> createOtherShoppingList() {
    ObservableList<ShoppingItem> list =
        FXCollections.observableArrayList(finalShoppingList.stream().filter(ShoppingItem::isOnList)
            .filter(s -> s.getShopType().equals(ShopType.OVERIG)).toList());
    Collections.sort(list, comparator);
    return list;
  }
}
