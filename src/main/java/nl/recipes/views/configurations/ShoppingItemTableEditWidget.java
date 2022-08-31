package nl.recipes.views.configurations;

import static nl.recipes.views.ViewConstants.DROP_SHADOW;
import static nl.recipes.views.ViewConstants.RP_TABLE;
import static nl.recipes.views.ViewConstants.TITLE;
import static nl.recipes.views.ViewConstants.VALIDATION;
import static nl.recipes.views.ViewConstants.WIDGET;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.stereotype.Component;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
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
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.MeasureUnitService;
import nl.recipes.services.StandardShoppingItemService;
import nl.recipes.views.components.utils.Utils;
import nl.recipes.views.converters.MeasureUnitStringConverter;

@Component
public class ShoppingItemTableEditWidget implements ListChangeListener<MeasureUnit> {

  private final StandardShoppingItemService shoppingItemService;
  private final MeasureUnitService measureUnitService;

  TableView<ShoppingItem> shoppingItemTableView;
  
  TextField amountTextField;

  TextField nameField;
  
  SearchableComboBox<MeasureUnit> measureUnitComboBox;
  
  ComboBox<ShopType> shopTypeComboBox;

  ComboBox<IngredientType> ingredientTypeComboBox;

  Label nameError = new Label();

  private ShoppingItem selectedShoppingItem;

  private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);

  public ShoppingItemTableEditWidget(StandardShoppingItemService shoppingItemService,
      MeasureUnitService measureUnitService) {
    this.shoppingItemService = shoppingItemService;
    this.measureUnitService = measureUnitService;

    this.measureUnitService.addListener(this);
    
    measureUnitComboBox = new SearchableComboBox<>();
    measureUnitComboBox.setConverter(new MeasureUnitStringConverter());
    TextFields.bindAutoCompletion(measureUnitComboBox.getEditor(), measureUnitComboBox.getItems(), measureUnitComboBox.getConverter());
    measureUnitComboBox.getItems().setAll(this.measureUnitService.getReadonlyMeasureUnitList());
    measureUnitComboBox.getItems().add(null); // Added to enable clearing the measure unit field
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
    shoppingItemTableView.setMinHeight(200); // prevent table from collapsing

    TableColumn<ShoppingItem, Number> amountColumn = new TableColumn<>("Hoeveelheid");
    amountColumn.setCellValueFactory(
        c -> (c.getValue().getAmount() != null && (10 * c.getValue().getAmount() % 10) == 0)
        ? new ReadOnlyObjectWrapper<>(Math.round(c.getValue().getAmount()))
        : new ReadOnlyObjectWrapper<>(c.getValue().getAmount()));
 
    TableColumn<ShoppingItem, String> measureUnitColumn = new TableColumn<>("Maateenheid");
    measureUnitColumn.setCellValueFactory(c -> {
      if (c.getValue().getMeasureUnit() == null) {
        return new ReadOnlyObjectWrapper<>();
      } else {
        return new ReadOnlyObjectWrapper<>(
            c.getValue().getMeasureUnit().getName());
      }
    });

    TableColumn<ShoppingItem, String> nameColumn = new TableColumn<>("Naam");
    nameColumn.setCellValueFactory(
        c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));

    amountColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.15));
    measureUnitColumn.prefWidthProperty()
        .bind(shoppingItemTableView.widthProperty().multiply(0.25));
    nameColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.30));
    
    TableColumn<ShoppingItem, ShopType> shopTypeColumn = new TableColumn<>("Winkel");
    shopTypeColumn
    .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getShopType()));
    shopTypeColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.15));

    TableColumn<ShoppingItem, IngredientType> ingredientTypeColumn = new TableColumn<>("Type");
    ingredientTypeColumn
    .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIngredientType()));
    ingredientTypeColumn.prefWidthProperty()
    .bind(shoppingItemTableView.widthProperty().multiply(0.15));

    shoppingItemTableView.getColumns().add(amountColumn);
    shoppingItemTableView.getColumns().add(measureUnitColumn);
    shoppingItemTableView.getColumns().add(nameColumn);
    shoppingItemTableView.getColumns().add(shopTypeColumn);
    shoppingItemTableView.getColumns().add(ingredientTypeColumn);

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
    column0.setPercentWidth(20);
    column0.setHalignment(HPos.RIGHT);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(30);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(20);
    column2.setHalignment(HPos.RIGHT);
    ColumnConstraints column3 = new ColumnConstraints();
    column3.setPercentWidth(30);
    
    form.getColumnConstraints().addAll(column0, column1, column2, column3);

    Label amountLabel = new Label("Hoeveelheid:");
    amountTextField = new TextField();
    form.add(amountLabel, 0, 0);
    form.add(amountTextField, 1, 0);
    amountTextField.setOnKeyReleased(this::handleKeyReleasedAction);
    
    Label measureUnitLabel = new Label("Maateenheid:");
    form.add(measureUnitLabel, 0, 1);
//    measureUnitComboBox.setMinWidth(150);
    measureUnitComboBox.setOnAction(e -> modifiedProperty.set(true));
    form.add(measureUnitComboBox, 1, 1);
    
    Label nameLabel = new Label("Naam:");
    nameField = new TextField();
//    nameField.setMinWidth(150);
    nameField.setOnAction(e -> modifiedProperty.set(true));
    GridPane.setValignment(nameLabel, VPos.TOP);
    nameField.setOnKeyReleased(this::handleKeyReleasedAction);
    VBox nameWithValidation = new VBox();
    nameError.getStyleClass().add(VALIDATION);
    nameWithValidation.getChildren().addAll(nameField, nameError);
    form.add(nameLabel, 0, 2);
    form.add(nameWithValidation, 1, 2);
    
    Label shopTypeLabel = new Label("Winkel:");
    form.add(shopTypeLabel, 2, 1);
    shopTypeComboBox = new ComboBox<>();
    shopTypeComboBox.getItems().setAll(ShopType.values());
//    shopTypeComboBox.setMinWidth(150);
    shopTypeComboBox.setOnAction(e -> modifiedProperty.set(true));
    form.add(shopTypeComboBox, 3, 1);

    Label ingredientTypeLabel = new Label("Ingrediënt type:");
    form.add(ingredientTypeLabel, 2, 2);
    ingredientTypeComboBox = new ComboBox<>();
    ingredientTypeComboBox.getItems().setAll(IngredientType.values());
    ingredientTypeComboBox.setMinWidth(150);
    ingredientTypeComboBox.setOnAction(e -> modifiedProperty.set(true));
    form.add(ingredientTypeComboBox, 3, 2);

    shoppingItemTableView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          selectedShoppingItem = newValue;
          if (newValue != null) {
            amountTextField.setText(selectedShoppingItem.getAmount() == null ? null
                : Utils.format(selectedShoppingItem.getAmount()));
            nameField.setText(selectedShoppingItem.getName());
            nameError.setText("");
            measureUnitComboBox.setValue(selectedShoppingItem.getMeasureUnit());
            shopTypeComboBox.setValue(selectedShoppingItem.getShopType());
            ingredientTypeComboBox.setValue(selectedShoppingItem.getIngredientType());
          } else {
            amountTextField.setText(null);
            nameField.setText(null);
            nameError.setText("");
            measureUnitComboBox.setValue(null);
            shopTypeComboBox.setValue(null);
            ingredientTypeComboBox.setValue(null);
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
    ShoppingItem shoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withAmount((amountTextField.getText() == null || amountTextField.getText().isEmpty()) ? null
                : Float.valueOf(amountTextField.getText()))
        .withMeasureUnit(measureUnitComboBox.getValue())
        .withName(nameField.getText())
        .withShopType(shopTypeComboBox.getValue())
        .withIngredientType(ingredientTypeComboBox.getValue())
        .build();
    
    try {
      shoppingItemService.create(shoppingItem);
      shoppingItemTableView.getSelectionModel().select(shoppingItem);
    } catch (AlreadyExistsException | IllegalValueException e) {
      nameError.setText(e.getMessage());
    }
  }

  private void updateShoppingItem(ActionEvent actionEvent) {
    ShoppingItem update = new ShoppingItem.ShoppingItemBuilder()
        .withAmount((amountTextField.getText() == null || amountTextField.getText().isEmpty()) ? null
            : Float.valueOf(amountTextField.getText()))
        .withMeasureUnit(measureUnitComboBox.getValue())
        .withName(nameField.getText())
        .withShopType(shopTypeComboBox.getValue())
        .withIngredientType(ingredientTypeComboBox.getValue())
        .build();
 
    try {
      shoppingItemService.update(selectedShoppingItem, update);
    } catch (NotFoundException | AlreadyExistsException e) {
      nameError.setText(e.getMessage());
    }
    modifiedProperty.set(false);
  }

  private void removeShoppingItem(ActionEvent actionEvent) {
    try {
      shoppingItemService.remove(selectedShoppingItem);
    } catch (NotFoundException e) {
      nameError.setText(e.getMessage());
    }
  }

  @Override
  public void onChanged(Change<? extends MeasureUnit> c) {
    measureUnitComboBox.getItems().setAll(measureUnitService.getReadonlyMeasureUnitList());
  }

}
