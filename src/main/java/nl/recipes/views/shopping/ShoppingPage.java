package nl.recipes.views.shopping;

import static nl.recipes.views.ViewConstants.CSS_PLANNING_DATE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.GoogleSheetService;
import nl.recipes.services.PlanningService;
import nl.recipes.services.StandardShoppingItemService;
import nl.recipes.services.StockShoppingItemService;
import nl.recipes.views.components.utils.Utils;

@Slf4j
@Component
public class ShoppingPage {

  private final PlanningService planningService;
  private final StandardShoppingItemService standardShoppingItemService;
  private final StockShoppingItemService stockShoppingItemService;
  private final GoogleSheetService googleSheetService;

  AnchorPane shoppingView;
  ScrollPane scrollPane;

  HBox finalListBox = new HBox();
  Button sendShoppingListToGoogle;

  ObservableList<Ingredient> ingredientList;
  List<ShoppingItem> finalShoppingList;

  public ShoppingPage(PlanningService planningService, StandardShoppingItemService standardShoppingItemService,
      GoogleSheetService googleSheetService, StockShoppingItemService stockShoppingItemService) {
    this.planningService = planningService;
    this.standardShoppingItemService = standardShoppingItemService;
    this.stockShoppingItemService = stockShoppingItemService;
    this.googleSheetService = googleSheetService;
  }

  public ScrollPane getShoppingPage() {
    finalListBox.getChildren().clear();
    ingredientList = FXCollections.observableList(planningService.getIngredientList());
    
    finalShoppingList = new ArrayList<>();

    shoppingView = new AnchorPane();
    shoppingView.getChildren().add(getShoppingPanel());
    
    
    scrollPane = new ScrollPane();
    scrollPane.setContent(shoppingView);
    return scrollPane;
  }

  private VBox getShoppingPanel() {
    VBox shoppingPanel = new VBox();
    shoppingPanel.setPadding(new Insets(15));
    shoppingPanel.setSpacing(40);

    shoppingPanel.getChildren().addAll(getShoppingBox(), getButtonPanel(), finalListBox);
    return shoppingPanel;
  }

  private HBox getShoppingBox() {
    HBox shoppingBox = new HBox();
    shoppingBox.setPadding(new Insets(15));
    shoppingBox.setSpacing(30);
    shoppingBox.getChildren().addAll(getNoStockList(), getStockPane(), 
        createShoppingPanel("Selecteer voorraad boodschappen", createRelevantStockList(), true),
        createShoppingPanel("Selecteer standaard boodschappen", createStandardShoppingList(), true));
    return shoppingBox;
  }

  private HBox getButtonPanel() {
    Button generateShoppingList = new Button("Genereer boodschappenlijst");
    generateShoppingList.setOnAction(this::createFinalShoppingPanels);

    sendShoppingListToGoogle = new Button("Stuur boodschappenlijst naar Google sheets");
    sendShoppingListToGoogle.setOnAction(this::sendShoppingListToGoogle);
    sendShoppingListToGoogle.setVisible(false);

    HBox buttonPanel = new HBox();
    buttonPanel.setPadding(new Insets(30));
    buttonPanel.setSpacing(20);
    buttonPanel.getChildren().addAll(generateShoppingList, sendShoppingListToGoogle);
    return buttonPanel;
  }

  private GridPane getNoStockList() {
    GridPane noStockPane = new GridPane();
    noStockPane.setHgap(20);

    Label header = new Label("Boodschappen voor recepten");
    header.getStyleClass().add(CSS_PLANNING_DATE);
    noStockPane.add(header, 1, 0, 4, 1);

    List<ShoppingItem> noStockList = ingredientList.stream().filter(i -> !i.getIngredientName().isStock())
        .<ShoppingItem>map(i -> new ShoppingItem.ShoppingItemBuilder()
          .withAmount(i.getAmount())
          .withName(i.getIngredientName().getName())
          .withPluralName(i.getIngredientName().getPluralName())
          .withMeasureUnit(i.getIngredientName().getMeasureUnit())
          .withShopType(i.getIngredientName().getShopType())
          .withIngredientType(i.getIngredientName().getIngredientType())
          .withOnList(true)
          .build()).toList();
    
 // TODO: Also add to the final list for now => this probably needs refactoring to some sort of service
    finalShoppingList.addAll(noStockList);
      
      return createShoppingPanel("Boodschappen voor recepten", noStockList, true);
  }

  private GridPane getStockPane() {
    GridPane stockPane = new GridPane();
    stockPane.setHgap(20);

    Label header = new Label("Benodigd uit voorraad");
    header.getStyleClass().add(CSS_PLANNING_DATE);
    stockPane.add(header, 1, 0, 4, 1);

    int row = 1;
    for (Ingredient ingredient : ingredientList.stream().filter(s -> s.getIngredientName().isStock()).toList()) {
      Label amountLabel = new Label(ingredient.getAmount() == null ? "" : Utils.format(ingredient.getAmount()));
      Label measureUnitLabel = new Label(
          ingredient.getIngredientName().getMeasureUnit() == null ? "" : getIngredientMeasureUnitLabel(ingredient));
      Label ingredientName = new Label(getIngredientIngredientNameLabel(ingredient));

      stockPane.add(amountLabel, 1, row);
      stockPane.add(measureUnitLabel, 2, row);
      stockPane.add(ingredientName, 3, row);
      row++;
    }
    return stockPane;
  }

  private void createFinalShoppingPanels(ActionEvent event) {
    finalListBox.getChildren().clear();
    finalListBox.setPadding(new Insets(15));
    finalListBox.setSpacing(30);
    finalListBox.getChildren().addAll(createShoppingPanel("Eko plaza", createEkoShoppingList(), false), 
        createShoppingPanel("DEKA", createDekaShoppingList(), false), 
        createShoppingPanel("Markt", createMarktShoppingList(), false), 
        createShoppingPanel("Other", createOtherShoppingList(), false));
    sendShoppingListToGoogle.setVisible(true);
  }

  private void sendShoppingListToGoogle(ActionEvent event) {
    try {
      googleSheetService.setEkoShoppings(createEkoShoppingList());
      googleSheetService.setDekaShoppings(createDekaShoppingList());
      googleSheetService.setMarktShoppings(createMarktShoppingList());
      googleSheetService.setOtherShoppings(createOtherShoppingList());
    } catch(IOException ex) {
      log.debug("Catched {}", ex.getMessage());
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
    a.setContentText("Sluit eerst dit waarschuwingsvenster, open je browser, ga naar 'inloggen met Google account', "
        + "selecteer het juiste account en bevestig dat je wilt doorgaan.");
    a.showAndWait();
  }
  
  private GridPane createShoppingPanel(String header, List<ShoppingItem> shoppingItems, boolean showCheckBox) {
    GridPane shoppingPanel = new GridPane();
    shoppingPanel.setHgap(20);

    Label headerLabel = new Label(header);
    headerLabel.getStyleClass().add(CSS_PLANNING_DATE);
    shoppingPanel.add(headerLabel, 1, 0, 4, 1);

    int row = 1;
    for (ShoppingItem shoppingItem : shoppingItems) {
      Label amountLabel = new Label(shoppingItem.getAmount() == null ? "" : Utils.format(shoppingItem.getAmount()));
      Label measureUnitLabel =
          new Label(shoppingItem.getMeasureUnit() == null ? "" : getMeasureUnitLabel(shoppingItem));
      Label shoppingItemName = new Label(getShoppingItemNameLabel(shoppingItem));

      shoppingPanel.add(amountLabel, 1, row);
      shoppingPanel.add(measureUnitLabel, 2, row);
      shoppingPanel.add(shoppingItemName, 3, row);
      
      if (showCheckBox) {
        CheckBox shoppingCheckBox = new CheckBox();
        shoppingCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean oldValue,
            Boolean newValue) -> shoppingItem.setOnList(newValue));
        shoppingCheckBox.setSelected(shoppingItem.isOnList());
        shoppingPanel.add(shoppingCheckBox, 4, row);
      } 
      row++;
    }
    return shoppingPanel;
  }

  private List<ShoppingItem> createRelevantStockList() {
    List<ShoppingItem> stockSelectionList = new ArrayList<>();
    for (Ingredient ingredient : ingredientList.stream().filter(s -> s.getIngredientName().isStock()).toList()) {
      stockShoppingItemService.findByName(ingredient.getIngredientName().getName()).ifPresent(stockSelectionList::add);
    }
// TODO: Also add to the final list for now => this probably needs refactoring to some sort of service
    finalShoppingList.addAll(stockSelectionList);
    return stockSelectionList;
  }
  
//  TODO The lists can be used more efficiently, probably needs refactoring to some sort of service
  private List<ShoppingItem> createStandardShoppingList() {
    List<ShoppingItem> standardShoppingList = new ArrayList<>();
    for (ShoppingItem shoppingItem : standardShoppingItemService.getReadonlyShoppingItemList()) {
      if (standardShoppingList.stream().noneMatch(s -> s.getName().equals(shoppingItem.getName()))) {
        standardShoppingList.add(shoppingItem);
      }
    }
 // TODO: Also add to the final list for now => this probably needs refactoring to some sort of service
    finalShoppingList.addAll(standardShoppingList);
    return standardShoppingList;
  }
  
  private List<ShoppingItem> createEkoShoppingList() {
    return finalShoppingList.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getShopType().equals(ShopType.EKO)).toList();
  }

  private List<ShoppingItem> createDekaShoppingList() {
    return finalShoppingList.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getShopType().equals(ShopType.DEKA)).toList();
  }

  private List<ShoppingItem> createMarktShoppingList() {
    return finalShoppingList.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getShopType().equals(ShopType.MARKT)).toList();
  }

  private List<ShoppingItem> createOtherShoppingList() {
    return finalShoppingList.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getShopType().equals(ShopType.OVERIG)).toList();
  }
  
  private String getMeasureUnitLabel(ShoppingItem shoppingItem) {
    return (shoppingItem.getAmount() == null || shoppingItem.getAmount() <= 1)
        ? shoppingItem.getMeasureUnit().getName()
        : shoppingItem.getMeasureUnit().getPluralName();
  }
  
  private String getShoppingItemNameLabel(ShoppingItem shoppingItem) {
    if (shoppingItem.getPluralName() == null) return shoppingItem.getName();
    return (shoppingItem.getAmount() == null || shoppingItem.getAmount() <= 1)
        ? shoppingItem.getName()
        : shoppingItem.getPluralName();
  }
  
  private String getIngredientMeasureUnitLabel(Ingredient ingredient) {
    return (ingredient.getAmount() == null || ingredient.getAmount() <= 1)
        ? ingredient.getIngredientName().getMeasureUnit().getName()
        : ingredient.getIngredientName().getMeasureUnit().getPluralName();
  }

  private String getIngredientIngredientNameLabel(Ingredient ingredient) {
    return (ingredient.getAmount() == null || ingredient.getAmount() <= 1) ? ingredient.getIngredientName().getName()
        : ingredient.getIngredientName().getPluralName();
  }
}
