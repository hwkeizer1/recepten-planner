package nl.recipes.views.planning;

import static nl.recipes.views.ViewConstants.PLANNING_DATE;
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
import nl.recipes.domain.ShopType;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.services.PlanningService;
import nl.recipes.services.ShoppingItemService;

@Component
public class ShoppingListView {

  private final PlanningService planningService;
  private final ShoppingItemService shoppingItemService;

  AnchorPane shoppingView;

  HBox finalListBox = new HBox();

  ObservableList<ShoppingItem> shoppingItems;

  public ShoppingListView(PlanningService planningService,
      ShoppingItemService shoppingItemService) {
    this.planningService = planningService;
    this.shoppingItemService = shoppingItemService;
  }

  public AnchorPane getShoppingView() {
    shoppingItems = FXCollections.observableList(planningService.getShoppingList());
    shoppingItems
        .addAll(FXCollections.observableList(shoppingItemService.getReadonlyShoppingItemList()));


    shoppingView = new AnchorPane();
    shoppingView.getChildren().add(getShoppingPanel());
    return shoppingView;
  }

  private VBox getShoppingPanel() {
    VBox shoppingPanel = new VBox();
    shoppingPanel.setPadding(new Insets(15));
    shoppingPanel.setSpacing(50);

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

    HBox buttonPanel = new HBox();
    buttonPanel.setPadding(new Insets(30));
    buttonPanel.getChildren().add(generateShoppingList);
    return buttonPanel;
  }

  private GridPane getNoStockList() {
    GridPane noStockList = new GridPane();
    noStockList.setHgap(20);

    Label header = new Label("Boodschappen voor recepten");
    header.getStyleClass().add(PLANNING_DATE);
    noStockList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (ShoppingItem shoppingItem : shoppingItems.stream()
        .filter(s -> !s.isStandard())
        .filter(s -> !s.getIngredientName().isStock()).toList()) {
      Label amountLabel = new Label(shoppingItem.getAmount() == null ? ""
          : shoppingItem.getAmount().toString().replaceAll("\\.0*$", ""));
      Label measureUnitLabel = new Label(
          shoppingItem.getMeasureUnit() == null ? "" : shoppingItem.getMeasureUnit().getName());
      Label ingredientName = new Label(shoppingItem.getIngredientName() == null ? ""
          : shoppingItem.getIngredientName().getName());
      CheckBox onShoppingList = new CheckBox();
      onShoppingList.selectedProperty().addListener(
          (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            shoppingItem.setOnList(new_val);
          });
      onShoppingList.setSelected(shoppingItem.isOnList());

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

    Label header = new Label("Voorraad ingrediënten");
    header.getStyleClass().add(PLANNING_DATE);
    stockList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (ShoppingItem shoppingItem : shoppingItems.stream()
        .filter(s -> !s.isStandard())
        .filter(s -> s.getIngredientName().isStock()).toList()) {
      Label amountLabel = new Label(shoppingItem.getAmount() == null ? ""
          : shoppingItem.getAmount().toString().replaceAll("\\.0*$", ""));
      Label measureUnitLabel = new Label(
          shoppingItem.getMeasureUnit() == null ? "" : shoppingItem.getMeasureUnit().getName());
      Label ingredientName = new Label(shoppingItem.getIngredientName() == null ? ""
          : shoppingItem.getIngredientName().getName());
      CheckBox onShoppingList = new CheckBox();
      onShoppingList.selectedProperty().addListener(
          (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            shoppingItem.setOnList(new_val);
          });
      onShoppingList.setSelected(shoppingItem.isOnList());

      stockList.add(amountLabel, 1, row);
      stockList.add(measureUnitLabel, 2, row);
      stockList.add(ingredientName, 3, row);
      stockList.add(onShoppingList, 4, row);
      row++;
    }
    return stockList;
  }

  private GridPane getStandardList() {
    GridPane standardList = new GridPane();
    standardList.setHgap(20);

    Label header = new Label("Standaard boodschappen");
    header.getStyleClass().add(PLANNING_DATE);
    standardList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (ShoppingItem shoppingItem : shoppingItems.stream().filter(ShoppingItem::isStandard).toList()) {
      Label amountLabel = new Label(shoppingItem.getAmount() == null ? ""
          : shoppingItem.getAmount().toString().replaceAll("\\.0*$", ""));
      Label measureUnitLabel = new Label(
          shoppingItem.getMeasureUnit() == null ? "" : shoppingItem.getMeasureUnit().getName());
      Label ingredientName = new Label(shoppingItem.getIngredientName() == null ? ""
          : shoppingItem.getIngredientName().getName());
      CheckBox onShoppingList = new CheckBox();
      onShoppingList.selectedProperty().addListener(
          (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            shoppingItem.setOnList(new_val);
          });
      onShoppingList.setSelected(shoppingItem.isOnList());

      standardList.add(amountLabel, 1, row);
      standardList.add(measureUnitLabel, 2, row);
      standardList.add(ingredientName, 3, row);
      standardList.add(onShoppingList, 4, row);
      row++;
    }
    return standardList;
  }

  private void generateShoppingList(ActionEvent event) {
    finalListBox.getChildren().clear();
    finalListBox.setPadding(new Insets(15));
    finalListBox.setSpacing(30);
    finalListBox.getChildren().addAll(getEkoList(), getDekaList(), getMarktList(), getOtherList());
  }

  private GridPane getEkoList() {
    GridPane ekoShoppingList = new GridPane();
    ekoShoppingList.setHgap(20);

    Label header = new Label("Eko plaza");
    header.getStyleClass().add(PLANNING_DATE);
    ekoShoppingList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (ShoppingItem shoppingItem : shoppingItems.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getShopType().equals(ShopType.EKO)).toList()) {
      Label amountLabel = new Label(shoppingItem.getAmount() == null ? ""
          : shoppingItem.getAmount().toString().replaceAll("\\.0*$", ""));
      Label measureUnitLabel = new Label(
          shoppingItem.getMeasureUnit() == null ? "" : shoppingItem.getMeasureUnit().getName());
      Label ingredientName = new Label(shoppingItem.getIngredientName() == null ? ""
          : shoppingItem.getIngredientName().getName());

      ekoShoppingList.add(amountLabel, 1, row);
      ekoShoppingList.add(measureUnitLabel, 2, row);
      ekoShoppingList.add(ingredientName, 3, row);
      row++;
    }
    return ekoShoppingList;
  }

  private GridPane getDekaList() {
    GridPane dekaShoppingList = new GridPane();
    dekaShoppingList.setHgap(20);

    Label header = new Label("DEKA");
    header.getStyleClass().add(PLANNING_DATE);
    dekaShoppingList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (ShoppingItem shoppingItem : shoppingItems.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getShopType().equals(ShopType.DEKA)).toList()) {
      Label amountLabel = new Label(shoppingItem.getAmount() == null ? ""
          : shoppingItem.getAmount().toString().replaceAll("\\.0*$", ""));
      Label measureUnitLabel = new Label(
          shoppingItem.getMeasureUnit() == null ? "" : shoppingItem.getMeasureUnit().getName());
      Label ingredientName = new Label(shoppingItem.getIngredientName() == null ? ""
          : shoppingItem.getIngredientName().getName());

      dekaShoppingList.add(amountLabel, 1, row);
      dekaShoppingList.add(measureUnitLabel, 2, row);
      dekaShoppingList.add(ingredientName, 3, row);
      row++;
    }
    return dekaShoppingList;
  }

  private GridPane getMarktList() {
    GridPane marktShoppingList = new GridPane();
    marktShoppingList.setHgap(20);

    Label header = new Label("Markt");
    header.getStyleClass().add(PLANNING_DATE);
    marktShoppingList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (ShoppingItem shoppingItem : shoppingItems.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getShopType().equals(ShopType.MARKT)).toList()) {
      Label amountLabel = new Label(shoppingItem.getAmount() == null ? ""
          : shoppingItem.getAmount().toString().replaceAll("\\.0*$", ""));
      Label measureUnitLabel = new Label(
          shoppingItem.getMeasureUnit() == null ? "" : shoppingItem.getMeasureUnit().getName());
      Label ingredientName = new Label(shoppingItem.getIngredientName() == null ? ""
          : shoppingItem.getIngredientName().getName());

      marktShoppingList.add(amountLabel, 1, row);
      marktShoppingList.add(measureUnitLabel, 2, row);
      marktShoppingList.add(ingredientName, 3, row);
      row++;
    }
    return marktShoppingList;
  }

  private GridPane getOtherList() {
    GridPane otherShoppingList = new GridPane();
    otherShoppingList.setHgap(20);

    Label header = new Label("Overig");
    header.getStyleClass().add(PLANNING_DATE);
    otherShoppingList.add(header, 1, 0, 4, 1);

    int row = 1;
    for (ShoppingItem shoppingItem : shoppingItems.stream().filter(ShoppingItem::isOnList)
        .filter(s -> s.getShopType().equals(ShopType.OVERIG)).toList()) {
      Label amountLabel = new Label(shoppingItem.getAmount() == null ? ""
          : shoppingItem.getAmount().toString().replaceAll("\\.0*$", ""));
      Label measureUnitLabel = new Label(
          shoppingItem.getMeasureUnit() == null ? "" : shoppingItem.getMeasureUnit().getName());
      Label ingredientName = new Label(shoppingItem.getIngredientName() == null ? ""
          : shoppingItem.getIngredientName().getName());

      otherShoppingList.add(amountLabel, 1, row);
      otherShoppingList.add(measureUnitLabel, 2, row);
      otherShoppingList.add(ingredientName, 3, row);
      row++;
    }
    return otherShoppingList;
  }

}
