package nl.recipes.views.shopping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.GoogleSheetService;

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
    shoppingPanel.setPadding(new Insets(15));
    shoppingPanel.setSpacing(20);
    shoppingPanel.getChildren().addAll(getToolBar(), getShoppingPanels(), getFinalPanels());

    AnchorPane anchorPane = new AnchorPane(shoppingPanel);

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setPannable(true);
    scrollPane.setContent(anchorPane);
    return scrollPane;
  }

  private ToolBar getToolBar() {
    ToolBar toolBar = new ToolBar();
    Button listButton =
        ShoppingPanel.createLargeToolBarButton("/icons/list.svg", "Genereer boodschappenlijst");
    listButton.setOnAction(this::createFinalShoppingPanels);
    toolBar.getItems().add(listButton);

    cloudButton = ShoppingPanel.createLargeToolBarButton("/icons/cloud.svg",
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
    return shoppingPanels;
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
        ShoppingPanel.buildWithoutToolBar("Eko plaza", createEkoShoppingList()),
        ShoppingPanel.buildWithoutToolBar("DEKA", createDekaShoppingList()),
        ShoppingPanel.buildWithoutToolBar("Markt", createMarktShoppingList()),
        ShoppingPanel.buildWithoutToolBar("Other", createOtherShoppingList()));
     cloudButton.setVisible(true);
  }

  private void createFinalShoppingList() {
    finalShoppingList = new ArrayList<>();
    finalShoppingList.addAll(recipeShoppingPanel.getList());
    finalShoppingList.addAll(selectStockShoppingPanel.getList());
    finalShoppingList.addAll(selectStandardShoppingPanel.getList());
    finalShoppingList.addAll(oneTimeShoppingPanel.getList());
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
    return FXCollections.observableArrayList(finalShoppingList.stream()
        .filter(ShoppingItem::isOnList).filter(s -> s.getShopType().equals(ShopType.EKO)).toList());
  }

  private ObservableList<ShoppingItem> createDekaShoppingList() {
    return FXCollections
        .observableArrayList(finalShoppingList.stream().filter(ShoppingItem::isOnList)
            .filter(s -> s.getShopType().equals(ShopType.DEKA)).toList());
  }

  private ObservableList<ShoppingItem> createMarktShoppingList() {
    return FXCollections
        .observableArrayList(finalShoppingList.stream().filter(ShoppingItem::isOnList)
            .filter(s -> s.getShopType().equals(ShopType.MARKT)).toList());
  }

  private ObservableList<ShoppingItem> createOtherShoppingList() {
    return FXCollections
        .observableArrayList(finalShoppingList.stream().filter(ShoppingItem::isOnList)
            .filter(s -> s.getShopType().equals(ShopType.OVERIG)).toList());
  }
}
