package nl.recipes.views.shopping;

import static nl.recipes.views.ViewConstants.CSS_PLANNING_DATE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
import nl.recipes.services.MeasureUnitService;
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
  private final MeasureUnitService measureUnitService;
  private final GoogleSheetService googleSheetService;
  private final ShoppingPanelProvider shoppingPanelProvider;

  AnchorPane shoppingView;
  ScrollPane scrollPane;

  List<ShoppingItem> oneTimeShoppingList;
  HBox shoppingBox;
  HBox finalListBox;
  Button sendShoppingListToGoogle;

  ObservableList<Ingredient> ingredientList;
  List<ShoppingItem> finalShoppingList;

  public ShoppingPage(PlanningService planningService,
      StandardShoppingItemService standardShoppingItemService,
      GoogleSheetService googleSheetService, StockShoppingItemService stockShoppingItemService,
      MeasureUnitService measureUnitService, ShoppingPanelProvider shoppingPanelProvider) {
    this.planningService = planningService;
    this.standardShoppingItemService = standardShoppingItemService;
    this.stockShoppingItemService = stockShoppingItemService;
    this.measureUnitService = measureUnitService;
    this.googleSheetService = googleSheetService;
    this.shoppingPanelProvider = shoppingPanelProvider;

    oneTimeShoppingList = new ArrayList<>();
    finalListBox = new HBox();
    shoppingBox = new HBox();
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
    shoppingBox.setPadding(new Insets(15));
    shoppingBox.setSpacing(30);
    shoppingBox.getChildren().clear();
    shoppingBox.getChildren().addAll(getNoStockList(), getStockList(),
        shoppingPanelProvider.createShoppingPanel("Selecteer voorraad boodschappen", createRelevantStockList(), true),
        shoppingPanelProvider.createShoppingPanel("Selecteer standaard boodschappen", createStandardShoppingList(), true),
        shoppingPanelProvider.createShoppingPanel("Selecteer eenmalige boodschappen", oneTimeShoppingList, true,
            createNewItemButton()));
    return shoppingBox;
  }

  private GridPane getNoStockList() {
    GridPane noStockPane = new GridPane();
    noStockPane.setHgap(20);

    Label header = new Label("Boodschappen voor recepten");
    header.getStyleClass().add(CSS_PLANNING_DATE);
    noStockPane.add(header, 1, 0, 4, 1);

    List<ShoppingItem> noStockList = ingredientList.stream()
        .filter(i -> !i.getIngredientName().isStock())
        .<ShoppingItem>map(i -> new ShoppingItem.ShoppingItemBuilder().withAmount(i.getAmount())
            .withName(i.getIngredientName().getName())
            .withPluralName(i.getIngredientName().getPluralName())
            .withMeasureUnit(i.getIngredientName().getMeasureUnit())
            .withShopType(i.getIngredientName().getShopType())
            .withIngredientType(i.getIngredientName().getIngredientType()).withOnList(true).build())
        .toList();
    finalShoppingList.addAll(noStockList);

    return shoppingPanelProvider.createShoppingPanel("Boodschappen voor recepten", noStockList, true);
  }

  private GridPane getStockList() {
    GridPane stockPane = new GridPane();
    stockPane.setHgap(20);

    Label header = new Label("Benodigd uit voorraad");
    header.getStyleClass().add(CSS_PLANNING_DATE);
    stockPane.add(header, 1, 0, 4, 1);

    int row = 1;
    for (Ingredient ingredient : ingredientList.stream()
        .filter(s -> s.getIngredientName().isStock()).toList()) {
      Label amountLabel =
          new Label(ingredient.getAmount() == null ? "" : Utils.format(ingredient.getAmount()));
      Label measureUnitLabel =
          new Label(ingredient.getIngredientName().getMeasureUnit() == null ? ""
              : getIngredientMeasureUnitLabel(ingredient));
      Label ingredientName = new Label(getIngredientIngredientNameLabel(ingredient));

      stockPane.add(amountLabel, 1, row);
      stockPane.add(measureUnitLabel, 2, row);
      stockPane.add(ingredientName, 3, row);
      row++;
    }
    return stockPane;
  }

  private List<ShoppingItem> createRelevantStockList() {
    List<ShoppingItem> stockSelectionList = new ArrayList<>();
    for (Ingredient ingredient : ingredientList.stream()
        .filter(s -> s.getIngredientName().isStock()).toList()) {
      stockShoppingItemService.findByName(ingredient.getIngredientName().getName())
          .ifPresent(stockSelectionList::add);
    }
    finalShoppingList.addAll(stockSelectionList);
    return stockSelectionList;
  }

  private List<ShoppingItem> createStandardShoppingList() {
    List<ShoppingItem> standardShoppingList = new ArrayList<>();
    for (ShoppingItem shoppingItem : standardShoppingItemService.getList()) {
      if (standardShoppingList.stream()
          .noneMatch(s -> s.getName().equals(shoppingItem.getName()))) {
        standardShoppingList.add(shoppingItem);
      }
    }
    standardShoppingList.sort(Comparator.comparing(ShoppingItem::isOnList).reversed()
        .thenComparing(ShoppingItem::getName));
    finalShoppingList.addAll(standardShoppingList);
    return standardShoppingList;
  }

  private Button createNewItemButton() {
    Button button = new Button("Voeg eenmalige boodschap toe");
    button.setOnAction(this::showAddItemDialog);
    return button;
  }

  private void showAddItemDialog(ActionEvent event) {
    AddItemDialog dialog = new AddItemDialog(measureUnitService.getList());
    Optional<ShoppingItem> shoppingItem = dialog.getDialogResult();
    shoppingItem.ifPresent(s -> oneTimeShoppingList.add(s));
    int count = shoppingBox.getChildren().size();
    shoppingBox.getChildren().remove(count - 1);
    shoppingBox.getChildren().add(shoppingPanelProvider.createShoppingPanel("Selecteer eenmalige boodschappen",
        oneTimeShoppingList, true, createNewItemButton()));
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

  private void createFinalShoppingPanels(ActionEvent event) {
    updateFinalShoppingList();
    finalListBox.getChildren().clear();
    finalListBox.setPadding(new Insets(15));
    finalListBox.setSpacing(30);
    finalListBox.getChildren().addAll(
        shoppingPanelProvider.createShoppingPanel("Eko plaza", createEkoShoppingList(), false),
        shoppingPanelProvider.createShoppingPanel("DEKA", createDekaShoppingList(), false),
        shoppingPanelProvider.createShoppingPanel("Markt", createMarktShoppingList(), false),
        shoppingPanelProvider.createShoppingPanel("Other", createOtherShoppingList(), false));
    sendShoppingListToGoogle.setVisible(true);
  }

  private void updateFinalShoppingList() {
    for (ShoppingItem shoppingItem : oneTimeShoppingList) {
      if (!finalListContainsName(shoppingItem.getName())) {
        finalShoppingList.add(shoppingItem);
      }
    }
  }

  private boolean finalListContainsName(String name) {
    boolean result = false;
    for (ShoppingItem finalShoppingItem : finalShoppingList) {
      if (finalShoppingItem.getName().equalsIgnoreCase(name))
        result = true;
    }
    return result;
  }

  private void sendShoppingListToGoogle(ActionEvent event) {
    try {
      googleSheetService.setEkoShoppings(createEkoShoppingList());
      googleSheetService.setDekaShoppings(createDekaShoppingList());
      googleSheetService.setMarktShoppings(createMarktShoppingList());
      googleSheetService.setOtherShoppings(createOtherShoppingList());
    } catch (IOException ex) {
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
    a.setContentText(
        "Sluit eerst dit waarschuwingsvenster, open je browser, ga naar 'inloggen met Google account', "
            + "selecteer het juiste account en bevestig dat je wilt doorgaan.");
    a.showAndWait();
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
