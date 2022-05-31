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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.IngredientNameService;
import nl.recipes.services.ShoppingItemService;

@Component
public class ShoppingItemTableEditWidget {

  private final ShoppingItemService shoppingItemService;

  private final IngredientNameService ingredientNameService;

  TableView<ShoppingItem> shoppingItemTableView;

  ComboBox<IngredientName> ingredientNameComboBox;

  Label ingredientNameError = new Label();

  private ShoppingItem selectedShoppingItem;

  private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
  
  Callback<ListView<IngredientName>, ListCell<IngredientName>> ingredientNameCellFactory =
      input -> new ListCell<IngredientName>() {

        @Override
        protected void updateItem(IngredientName item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
          } else {
            setText(item.getListLabel());
          }
        }
      };

  public ShoppingItemTableEditWidget(ShoppingItemService shoppingItemService, IngredientNameService ingredientNameService) {
    this.shoppingItemService = shoppingItemService;
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
    shoppingItemTableView.setItems(shoppingItemService.getShoppingItemList());

    TableColumn<ShoppingItem, String> measureUnitColumn = new TableColumn<>("Maateenheid");
    measureUnitColumn.setCellValueFactory(c -> {
      if (c.getValue().getIngredientName().getMeasureUnit() == null) {
        return new ReadOnlyObjectWrapper<>();
      } else {
        return new ReadOnlyObjectWrapper<>(c.getValue().getIngredientName().getMeasureUnit().getName());        
      }
    });
    
    TableColumn<ShoppingItem, String> nameColumn = new TableColumn<>("Naam");
    nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
        c.getValue().getIngredientName().getName()));
        
    nameColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty());
    
    measureUnitColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.35));
    nameColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.65));

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

    Label ingredientNameLabel = new Label("Artikel:");
    ingredientNameComboBox = new ComboBox<>();
    ingredientNameComboBox.getItems()
        .setAll(this.ingredientNameService.getReadonlyIngredientNameList());
    ingredientNameComboBox.setButtonCell(ingredientNameCellFactory.call(null));
    ingredientNameComboBox.setCellFactory(ingredientNameCellFactory);
    ingredientNameComboBox.setMinWidth(150);
    ingredientNameComboBox.setOnAction(e -> modifiedProperty.set(true));
    GridPane.setValignment(ingredientNameLabel, VPos.TOP);
    VBox ingredientNameWithValidation = new VBox();
    ingredientNameError.getStyleClass().add(VALIDATION);
    ingredientNameWithValidation.getChildren().addAll(ingredientNameComboBox, ingredientNameError);
    form.add(ingredientNameLabel, 0, 0);
    form.add(ingredientNameWithValidation, 1, 0);

    shoppingItemTableView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          selectedShoppingItem = newValue;
          if (newValue != null) {
            ingredientNameComboBox.setValue(selectedShoppingItem.getIngredientName());
            ingredientNameError.setText("");
          } else {
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

  private void createShoppingItem(ActionEvent actionEvent) {
    ShoppingItem shoppingItem = new ShoppingItem();
    shoppingItem.setIngredientName(ingredientNameComboBox.getValue());
    try {
      shoppingItemService.create(shoppingItem);
      shoppingItemTableView.getSelectionModel().select(shoppingItem);
    } catch (AlreadyExistsException | IllegalValueException e) {
      ingredientNameError.setText(e.getMessage());
    }
  }

  private void updateShoppingItem(ActionEvent actionEvent) {
    ShoppingItem update = new ShoppingItem();
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
