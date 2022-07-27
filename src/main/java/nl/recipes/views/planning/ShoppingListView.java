package nl.recipes.views.planning;

import static nl.recipes.views.ViewConstants.PLANNING_DATE;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.GoogleSheetService;
import nl.recipes.services.PlanningService;
import nl.recipes.services.ShoppingItemService;

@Slf4j
@Component
public class ShoppingListView {

  private static final String REGEXP_NO_SINGLE_ZERO_DIGIT = "\\.0*$";
  
  private final PlanningService planningService;
  private final ShoppingItemService shoppingItemService;
  private final GoogleSheetService googleSheetService;

  AnchorPane shoppingView;

  HBox finalListBox = new HBox();
  
  Button sendShoppingListToGoogle;

  ObservableList<Ingredient> ingredientList;
  List<ShoppingItem> shoppingItems;

  public ShoppingListView(PlanningService planningService,
      ShoppingItemService shoppingItemService, GoogleSheetService googleSheetService) {
    this.planningService = planningService;
    this.shoppingItemService = shoppingItemService;
    this.googleSheetService = googleSheetService;
  }

  public AnchorPane getShoppingView() {
    finalListBox.getChildren().clear();
    ingredientList = FXCollections.observableList(planningService.getIngredientList());
    List<ShoppingItem> ingredientShoppingItems =
        ingredientList.stream().filter(i -> i.getIngredientName().isStock())
            .map(i -> new ShoppingItem.ShoppingItemBuilder()
                .withIngredientName(i.getIngredientName())
                .withIsStandard(false)
                .withOnList(false)
                .build())
            .collect(Collectors.toList());
    shoppingItems = new ArrayList<>();
    shoppingItems.addAll(ingredientShoppingItems);
    for (ShoppingItem shoppingItem : shoppingItemService.getShoppingItemList()) {
      if (shoppingItems.stream()
          .noneMatch(s -> s.getIngredientName().equals(shoppingItem.getIngredientName()))) {
        shoppingItems.add(shoppingItem);
      }
    }

    shoppingView = new AnchorPane();
    shoppingView.getChildren().add(getShoppingPanel());
    return shoppingView;
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
    shoppingBox.getChildren().addAll(getNoStockList(), getStockList(), getStandardList());
    return shoppingBox;
  }

  private HBox getButtonPanel() {
    Button generateShoppingList = new Button("Genereer boodschappenlijst");
    generateShoppingList.setOnAction(this::generateShoppingList);
    
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
    GridPane noStockList = new GridPane();
    noStockList.setHgap(20);

    Label header = new Label("Boodschappen voor recepten");
    header.getStyleClass().add(PLANNING_DATE);
    noStockList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (Ingredient ingredient : ingredientList.stream()
        .filter(s -> !s.getIngredientName().isStock()).toList()) {
      Label amountLabel = new Label(ingredient.getAmount() == null ? ""
          : ingredient.getAmount().toString().replaceAll(REGEXP_NO_SINGLE_ZERO_DIGIT, ""));
      Label measureUnitLabel =
          new Label(ingredient.getIngredientName().getMeasureUnit() == null ? ""
              : getIngredientMeasureUnitLabel(ingredient));
      Label ingredientName = new Label(getIngredientIngredientNameLabel(ingredient));
      ingredient.setOnList(true);
      CheckBox onShoppingList = new CheckBox();
      onShoppingList.selectedProperty().addListener((ObservableValue<? extends Boolean> ov,
          Boolean oldValue, Boolean newValue) -> ingredient.setOnList(newValue));
      onShoppingList.setSelected(ingredient.isOnList());

      noStockList.add(amountLabel, 1, row);
      noStockList.add(measureUnitLabel, 2, row);
      noStockList.add(ingredientName, 3, row);
      noStockList.add(onShoppingList, 4, row);
      row++;
    }
    return noStockList;
  }

  private GridPane getStockList() {
    GridPane stockList = new GridPane();
    stockList.setHgap(20);

    Label header = new Label("Benodigd uit voorraad");
    header.getStyleClass().add(PLANNING_DATE);
    stockList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (Ingredient ingredient : ingredientList.stream()
        .filter(s -> s.getIngredientName().isStock()).toList()) {
      Label amountLabel = new Label(ingredient.getAmount() == null ? ""
          : ingredient.getAmount().toString().replaceAll(REGEXP_NO_SINGLE_ZERO_DIGIT, ""));
      Label measureUnitLabel =
          new Label(ingredient.getIngredientName().getMeasureUnit() == null ? ""
              : getIngredientMeasureUnitLabel(ingredient));
      Label ingredientName = new Label(getIngredientIngredientNameLabel(ingredient));

      stockList.add(amountLabel, 1, row);
      stockList.add(measureUnitLabel, 2, row);
      stockList.add(ingredientName, 3, row);
      row++;
    }
    return stockList;
  }

  private GridPane getStandardList() {
    GridPane standardList = new GridPane();
    standardList.setHgap(20);

    Label header = new Label("Standaard en voorraad boodschappen");
    header.getStyleClass().add(PLANNING_DATE);
    standardList.add(header, 1, 0, 4, 1);

    int row = 1;
    int col = 1;
    for (ShoppingItem shoppingItem : shoppingItems) {
      Label ingredientName = new Label(shoppingItem.getIngredientName().getPluralName());
      ingredientName.setTextFill(Color.GREEN);
      CheckBox onShoppingList = new CheckBox();
      onShoppingList.selectedProperty().addListener((ObservableValue<? extends Boolean> ov,
          Boolean oldValue, Boolean newValue) -> shoppingItem.setOnList(newValue));
      onShoppingList.setSelected(shoppingItem.isOnList());

      if (col % 2 != 0) {
        standardList.add(ingredientName, 2, row);
        standardList.add(onShoppingList, 3, row);
        col++;
      } else {
        standardList.add(ingredientName, 5, row);
        standardList.add(onShoppingList, 6, row);
        col++;
        row++;
      }

    }
    return standardList;
  }

  private void generateShoppingList(ActionEvent event) {
    finalListBox.getChildren().clear();
    finalListBox.setPadding(new Insets(15));
    finalListBox.setSpacing(30);
    finalListBox.getChildren().addAll(getEkoListPanel(), getDekaListPanel(), getMarktListPanel(), getOtherListPanel());
    sendShoppingListToGoogle.setVisible(true);
  }
  
  private void sendShoppingListToGoogle(ActionEvent event) {
    googleSheetService.setEkoShoppings(getEkoIngredientList(), getEkoShoppingList());
    googleSheetService.setDekaShoppings(getDekaIngredientList(), getDekaShoppingList());
    googleSheetService.setMarktShoppings(getMarktIngredientList(), getMarktShoppingList());
    googleSheetService.setOtherShoppings(getOtherIngredientList(), getOtherShoppingList());
  }

  private GridPane getEkoListPanel() {
    GridPane ekoShoppingList = new GridPane();
    ekoShoppingList.setHgap(20);

    Label header = new Label("Eko plaza");
    header.getStyleClass().add(PLANNING_DATE);
    ekoShoppingList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (Ingredient ingredient : getEkoIngredientList()) {
      Label amountLabel = new Label(ingredient.getAmount() == null ? ""
          : ingredient.getAmount().toString().replaceAll(REGEXP_NO_SINGLE_ZERO_DIGIT, ""));
      Label measureUnitLabel =
          new Label(ingredient.getIngredientName().getMeasureUnit() == null ? ""
              : getIngredientMeasureUnitLabel(ingredient));
      Label ingredientName = new Label(getIngredientIngredientNameLabel(ingredient));

      ekoShoppingList.add(amountLabel, 1, row);
      ekoShoppingList.add(measureUnitLabel, 2, row);
      ekoShoppingList.add(ingredientName, 3, row);
      row++;
    }

    for (ShoppingItem shoppingItem : getEkoShoppingList()) {
      Label ingredientName = new Label(shoppingItem.getIngredientName().getPluralName());
      ingredientName.setTextFill(Color.GREEN);

      ekoShoppingList.add(ingredientName, 3, row);
      row++;
    }
    return ekoShoppingList;
  }

  private GridPane getDekaListPanel() {
    GridPane dekaShoppingList = new GridPane();
    dekaShoppingList.setHgap(20);

    Label header = new Label("DEKA");
    header.getStyleClass().add(PLANNING_DATE);
    dekaShoppingList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (Ingredient ingredient : getDekaIngredientList()) {
      Label amountLabel = new Label(ingredient.getAmount() == null ? ""
          : ingredient.getAmount().toString().replaceAll(REGEXP_NO_SINGLE_ZERO_DIGIT, ""));
      Label measureUnitLabel =
          new Label(ingredient.getIngredientName().getMeasureUnit() == null ? ""
              : getIngredientMeasureUnitLabel(ingredient));
      Label ingredientName = new Label(getIngredientIngredientNameLabel(ingredient));

      dekaShoppingList.add(amountLabel, 1, row);
      dekaShoppingList.add(measureUnitLabel, 2, row);
      dekaShoppingList.add(ingredientName, 3, row);
      row++;
    }

    for (ShoppingItem shoppingItem : getDekaShoppingList()) {
      Label ingredientName = new Label(shoppingItem.getIngredientName().getPluralName());
      ingredientName.setTextFill(Color.GREEN);

      dekaShoppingList.add(ingredientName, 3, row);
      row++;
    }
    return dekaShoppingList;
  }

  private GridPane getMarktListPanel() {
    GridPane marktShoppingList = new GridPane();
    marktShoppingList.setHgap(20);

    Label header = new Label("Markt");
    header.getStyleClass().add(PLANNING_DATE);
    marktShoppingList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (Ingredient ingredient : getMarktIngredientList()) {
      Label amountLabel = new Label(ingredient.getAmount() == null ? ""
          : ingredient.getAmount().toString().replaceAll(REGEXP_NO_SINGLE_ZERO_DIGIT, ""));
      Label measureUnitLabel =
          new Label(ingredient.getIngredientName().getMeasureUnit() == null ? ""
              : getIngredientMeasureUnitLabel(ingredient));
      Label ingredientName = new Label(getIngredientIngredientNameLabel(ingredient));

      marktShoppingList.add(amountLabel, 1, row);
      marktShoppingList.add(measureUnitLabel, 2, row);
      marktShoppingList.add(ingredientName, 3, row);
      row++;
    }

    for (ShoppingItem shoppingItem : getMarktShoppingList()) {
      Label ingredientName = new Label(shoppingItem.getIngredientName().getPluralName());
      ingredientName.setTextFill(Color.GREEN);

      marktShoppingList.add(ingredientName, 3, row);
      row++;
    }
    return marktShoppingList;
  }

  private GridPane getOtherListPanel() {
    GridPane otherShoppingList = new GridPane();
    otherShoppingList.setHgap(20);

    Label header = new Label("Overig");
    header.getStyleClass().add(PLANNING_DATE);
    otherShoppingList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (Ingredient ingredient : getOtherIngredientList()) {
      Label amountLabel = new Label(ingredient.getAmount() == null ? ""
          : ingredient.getAmount().toString().replaceAll(REGEXP_NO_SINGLE_ZERO_DIGIT, ""));
      Label measureUnitLabel =
          new Label(ingredient.getIngredientName().getMeasureUnit() == null ? ""
              : getIngredientMeasureUnitLabel(ingredient));
      Label ingredientName = new Label(getIngredientIngredientNameLabel(ingredient));

      otherShoppingList.add(amountLabel, 1, row);
      otherShoppingList.add(measureUnitLabel, 2, row);
      otherShoppingList.add(ingredientName, 3, row);
      row++;
    }

    for (ShoppingItem shoppingItem : getOtherShoppingList()) {
      Label ingredientName = new Label(shoppingItem.getIngredientName().getPluralName());
      ingredientName.setTextFill(Color.GREEN);

      otherShoppingList.add(ingredientName, 3, row);
      row++;
    }
    return otherShoppingList;
  }
  
  private List<Ingredient> getEkoIngredientList() {
    return ingredientList.stream().filter(Ingredient::isOnList)
        .filter(s -> s.getIngredientName().getShopType().equals(ShopType.EKO)).toList();
  }
  
  private List<ShoppingItem> getEkoShoppingList() {
    return shoppingItems.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getIngredientName().getShopType().equals(ShopType.EKO)).toList();
  }
  
  private List<Ingredient> getDekaIngredientList() {
    return ingredientList.stream().filter(Ingredient::isOnList)
        .filter(s -> s.getIngredientName().getShopType().equals(ShopType.DEKA)).toList();
  }
  
  private List<ShoppingItem> getDekaShoppingList() {
    return shoppingItems.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getIngredientName().getShopType().equals(ShopType.DEKA)).toList();
  }
  
  private List<Ingredient> getMarktIngredientList() {
    return ingredientList.stream().filter(Ingredient::isOnList)
        .filter(s -> s.getIngredientName().getShopType().equals(ShopType.MARKT)).toList();
  }
  
  private List<ShoppingItem> getMarktShoppingList() {
    return shoppingItems.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getIngredientName().getShopType().equals(ShopType.MARKT)).toList();
  }
  
  private List<Ingredient> getOtherIngredientList() {
    return ingredientList.stream().filter(Ingredient::isOnList)
        .filter(s -> s.getIngredientName().getShopType().equals(ShopType.OVERIG)).toList();
  }
  
  private List<ShoppingItem> getOtherShoppingList() {
    return shoppingItems.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getIngredientName().getShopType().equals(ShopType.OVERIG)).toList();
  }

  private String getIngredientMeasureUnitLabel(Ingredient ingredient) {
    return (ingredient.getAmount() == null || ingredient.getAmount() <= 1)
        ? ingredient.getIngredientName().getMeasureUnit().getName()
        : ingredient.getIngredientName().getMeasureUnit().getPluralName();
  }

  private String getIngredientIngredientNameLabel(Ingredient ingredient) {
    return (ingredient.getAmount() == null || ingredient.getAmount() <= 1)
        ? ingredient.getIngredientName().getName()
        : ingredient.getIngredientName().getPluralName();
  }
}
