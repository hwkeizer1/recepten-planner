package nl.recipes.views.configurations;

import static nl.recipes.views.ViewConstants.DROP_SHADOW;
import static nl.recipes.views.ViewConstants.RP_TABLE;
import static nl.recipes.views.ViewConstants.TITLE;
import static nl.recipes.views.ViewConstants.VALIDATION;
import static nl.recipes.views.ViewConstants.WIDGET;
import org.springframework.stereotype.Component;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.IngredientNameService;
import nl.recipes.services.MeasureUnitService;
import nl.recipes.services.ShoppingItemService;

@Component
public class ShoppingItemTableEditWidget {

  private final ShoppingItemService shoppingItemService;

  private final MeasureUnitService measureUnitService;

  private final IngredientNameService ingredientNameService;

  TableView<ShoppingItem> shoppingItemTableView;

  TextField amountTextField;

  ComboBox<MeasureUnit> measureUnitComboBox;

  ComboBox<IngredientName> ingredientNameComboBox;

  Label ingredientNameError = new Label();

  private ShoppingItem selectedShoppingItem;

  private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);

  public ShoppingItemTableEditWidget(ShoppingItemService shoppingItemService,
      MeasureUnitService measureUnitService, IngredientNameService ingredientNameService) {
    this.shoppingItemService = shoppingItemService;
    this.measureUnitService = measureUnitService;
    this.ingredientNameService = ingredientNameService;
  }

  public Node getShoppingItemPanel() {
    VBox shoppingItemPanel = new VBox();
    shoppingItemPanel.setPadding(new Insets(20));
    shoppingItemPanel.getStyleClass().addAll(DROP_SHADOW, WIDGET);

    shoppingItemPanel.getChildren().addAll(createHeader(), createTable(), createForm(),
        createButtonBar());

    return shoppingItemPanel;
  }

  private Label createHeader() {
    Label title = new Label("Standaard boodschappen bewerken");
    title.getStyleClass().add(TITLE);
    return title;
  }

  private VBox createTable() {
    shoppingItemTableView = new TableView<>();
    shoppingItemTableView.getStyleClass().add(RP_TABLE);
    shoppingItemTableView.setItems(shoppingItemService.getReadonlyShoppingItemList());

    TableColumn<ShoppingItem, String> amountColumn = new TableColumn<>("Hoeveelheid");
    amountColumn
        .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getAmount() == null ? ""
            : c.getValue().getAmount().toString().replaceAll("\\.0*$", "")));
    amountColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.25));

    TableColumn<ShoppingItem, String> measureUnitColumn = new TableColumn<>("Maateenheid");
    measureUnitColumn.setCellValueFactory(
        c -> new ReadOnlyObjectWrapper<>(c.getValue().getMeasureUnit().getName()));
    measureUnitColumn.prefWidthProperty()
        .bind(shoppingItemTableView.widthProperty().multiply(0.35));

    TableColumn<ShoppingItem, String> nameColumn = new TableColumn<>("Naam");
    nameColumn.setCellValueFactory(
        c -> new ReadOnlyObjectWrapper<>(c.getValue().getIngredientName().getName()));
    nameColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.40));

    shoppingItemTableView.getColumns().add(amountColumn);
    shoppingItemTableView.getColumns().add(measureUnitColumn);
    shoppingItemTableView.getColumns().add(nameColumn);

    VBox shoppingItemTableBox = new VBox();
    shoppingItemTableBox.getChildren().add(shoppingItemTableView);
    return shoppingItemTableBox;
  }

  private GridPane createForm() {
    GridPane form = new GridPane();
    form.setPadding(new Insets(10, 0, 0, 0));
    form.setHgap(20);
    form.setVgap(10);

    ColumnConstraints column0 = new ColumnConstraints();
    column0.setPercentWidth(40);
    column0.setHalignment(HPos.RIGHT);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(50);
    form.getColumnConstraints().addAll(column0, column1);

    Label amountLabel = new Label("Hoeveelheid:");
    amountTextField = new TextField();
    amountTextField.setMaxWidth(75);
    amountTextField.setOnKeyReleased(this::handleKeyReleasedAction);
    form.add(amountLabel, 0, 0);
    form.add(amountTextField, 1, 0);

    Label measureUnitLabel = new Label("Maateenheid:");
    measureUnitComboBox = new ComboBox<>();
    measureUnitComboBox.getItems().setAll(this.measureUnitService.getReadonlyMeasureUnitList());
    measureUnitComboBox.setMinWidth(150);
    measureUnitComboBox.setOnAction(e -> modifiedProperty.set(true));
    form.add(measureUnitLabel, 0, 1);
    form.add(measureUnitComboBox, 1, 1);

    Label ingredientNameLabel = new Label("Artikel:");
    ingredientNameComboBox = new ComboBox<>();
    ingredientNameComboBox.getItems()
        .setAll(this.ingredientNameService.getReadonlyIngredientNameList());
    ingredientNameComboBox.setMinWidth(150);
    ingredientNameComboBox.setOnAction(e -> modifiedProperty.set(true));
    GridPane.setValignment(ingredientNameLabel, VPos.TOP);
    VBox ingredientNameWithValidation = new VBox();
    ingredientNameError.getStyleClass().add(VALIDATION);
    ingredientNameWithValidation.getChildren().addAll(ingredientNameComboBox, ingredientNameError);
    form.add(ingredientNameLabel, 0, 2);
    form.add(ingredientNameWithValidation, 1, 2);

    shoppingItemTableView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          selectedShoppingItem = newValue;
          if (newValue != null) {
            amountTextField.setText(selectedShoppingItem.getAmount().toString());
            measureUnitComboBox.setValue(selectedShoppingItem.getMeasureUnit());
            ingredientNameComboBox.setValue(selectedShoppingItem.getIngredientName());
            ingredientNameError.setText("");
          } else {
            amountTextField.setText(null);
            measureUnitComboBox.setValue(null);
            ingredientNameComboBox.setValue(null);
            ingredientNameError.setText("");
          }
          modifiedProperty.set(false);
        });

    return form;
  }

  private ButtonBar createButtonBar() {
    Button createButton = new Button("Toevoegen");
    Button updateButton = new Button("Wijzigen");
    Button removeButton = new Button("Verwijderen");

    removeButton.disableProperty()
        .bind(shoppingItemTableView.getSelectionModel().selectedItemProperty().isNull());
    removeButton.setOnAction(this::removeShoppingItem);

    updateButton.disableProperty().bind(shoppingItemTableView.getSelectionModel()
        .selectedItemProperty().isNull().or(modifiedProperty.not()));
    updateButton.setOnAction(this::updateShoppingItem);

    createButton.disableProperty()
        .bind(shoppingItemTableView.getSelectionModel().selectedItemProperty().isNotNull());
    createButton.setOnAction(this::createShoppingItem);

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.setPadding(new Insets(15, 0, 0, 0));
    buttonBar.getButtons().addAll(removeButton, updateButton, createButton);

    return buttonBar;
  }

  private void handleKeyReleasedAction(KeyEvent keyEvent) {
    modifiedProperty.set(true);
  }

  private void createShoppingItem(ActionEvent actionEvent) {
    ShoppingItem shoppingItem = new ShoppingItem();
    shoppingItem
        .setAmount((amountTextField.getText() == null || amountTextField.getText().isEmpty()) ? null
            : Float.valueOf(amountTextField.getText()));
    shoppingItem.setIngredientName(ingredientNameComboBox.getValue());
    shoppingItem.setMeasureUnit(measureUnitComboBox.getValue());
    try {
      shoppingItemService.create(shoppingItem);
      shoppingItemTableView.getSelectionModel().select(shoppingItem);
    } catch (AlreadyExistsException | IllegalValueException e) {
      ingredientNameError.setText(e.getMessage());
    }
  }

  private void updateShoppingItem(ActionEvent actionEvent) {
    ShoppingItem update = new ShoppingItem();
    update
        .setAmount((amountTextField.getText() == null || amountTextField.getText().isEmpty()) ? null
            : Float.valueOf(amountTextField.getText()));
    update.setMeasureUnit(measureUnitComboBox.getValue());
    update.setIngredientName(ingredientNameComboBox.getValue());

    try {
      shoppingItemService.update(selectedShoppingItem, update);
    } catch (NotFoundException | AlreadyExistsException e) {
      ingredientNameError.setText(e.getMessage());
    }
    modifiedProperty.set(false);
  }

  private void removeShoppingItem(ActionEvent actionEvent) {
     try {
     shoppingItemService.remove(selectedShoppingItem);
     } catch (NotFoundException e) {
     ingredientNameError.setText(e.getMessage());
     }
  }

}
