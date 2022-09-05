package nl.recipes.views.configurations;

import static nl.recipes.views.ViewConstants.*;
import static nl.recipes.views.ViewMessages.*;

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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.MeasureUnitService;
import nl.recipes.services.StockShoppingItemService;
import nl.recipes.views.components.utils.Utils;
import nl.recipes.views.converters.MeasureUnitStringConverter;

@Slf4j
@Component
public class StockShoppingEditPanel implements ListChangeListener<MeasureUnit> {

  private final StockShoppingItemService stockShoppingItemService;
  private final MeasureUnitService measureUnitService;

  TableView<ShoppingItem> shoppingItemTableView;
  TextField amountTextField;
  SearchableComboBox<MeasureUnit> measureUnitComboBox;

  private ShoppingItem selectedShoppingItem;
  private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);

  public StockShoppingEditPanel(StockShoppingItemService stockShoppingItemService,
      MeasureUnitService measureUnitService) {
    this.stockShoppingItemService = stockShoppingItemService;
    this.measureUnitService = measureUnitService;

    this.measureUnitService.addListener(this);
    
    measureUnitComboBox = new SearchableComboBox<>();
    measureUnitComboBox.setConverter(new MeasureUnitStringConverter());
    TextFields.bindAutoCompletion(measureUnitComboBox.getEditor(), measureUnitComboBox.getItems(), measureUnitComboBox.getConverter());
    measureUnitComboBox.getItems().setAll(this.measureUnitService.getReadonlyMeasureUnitList());
//    measureUnitComboBox.getItems().add(null); // Added to enable clearing the measure unit field
  }

  public Node getStockShoppingEditPanel() {
    VBox stockShoppingEditPanel = new VBox();
    stockShoppingEditPanel.setPadding(new Insets(20));
    stockShoppingEditPanel.getStyleClass().addAll(CSS_DROP_SHADOW, CSS_WIDGET);
    stockShoppingEditPanel.getChildren().addAll(createHeader(), createTable(), createForm(),
        createButtonBar());
    return stockShoppingEditPanel;
  }
  
  @Override
  public void onChanged(Change<? extends MeasureUnit> c) {
    shoppingItemTableView.getSelectionModel().clearSelection();
    measureUnitComboBox.getItems().setAll(measureUnitService.getReadonlyMeasureUnitList());
  }

  private Label createHeader() {
    Label title = new Label(EDIT_STOCK_SHOPPING);
    title.getStyleClass().add(CSS_TITLE);
    return title;
  }

  private VBox createTable() {
    shoppingItemTableView = new TableView<>();
    shoppingItemTableView.getStyleClass().add(CSS_BASIC_TABLE);
    shoppingItemTableView.setItems(stockShoppingItemService.getReadonlyShoppingItemList());
    shoppingItemTableView.setMinHeight(200); // prevent table from collapsing
    shoppingItemTableView.getSelectionModel().clearSelection();

    TableColumn<ShoppingItem, Number> amountColumn = new TableColumn<>(AMOUNT);
    amountColumn.setCellValueFactory(
        c -> (c.getValue().getAmount() != null && (10 * c.getValue().getAmount() % 10) == 0)
        ? new ReadOnlyObjectWrapper<>(Math.round(c.getValue().getAmount()))
        : new ReadOnlyObjectWrapper<>(c.getValue().getAmount()));
 
    TableColumn<ShoppingItem, String> measureUnitColumn = new TableColumn<>(MEASURE_UNIT);
    measureUnitColumn.setCellValueFactory(c -> {
      if (c.getValue().getMeasureUnit() == null) {
        return new ReadOnlyObjectWrapper<>();
      } else {
        return new ReadOnlyObjectWrapper<>(
            c.getValue().getMeasureUnit().getName());
      }
    });
    
    TableColumn<ShoppingItem, String> nameColumn = new TableColumn<>(NAME);
    nameColumn.setCellValueFactory(
        c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));

    nameColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.40));
    amountColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.30));
    measureUnitColumn.prefWidthProperty()
        .bind(shoppingItemTableView.widthProperty().multiply(0.30));
    

    shoppingItemTableView.getColumns().add(nameColumn);
    shoppingItemTableView.getColumns().add(amountColumn);
    shoppingItemTableView.getColumns().add(measureUnitColumn);
    
    shoppingItemTableView.setRowFactory(callback -> {
      final TableRow<ShoppingItem> row = new TableRow<>();
      row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        final int index = row.getIndex();
        if (index >= 0 && index < shoppingItemTableView.getItems().size()
            && shoppingItemTableView.getSelectionModel().isSelected(index)) {
          shoppingItemTableView.getSelectionModel().clearSelection();
          event.consume();
        }
      });
      return row;
    });

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
    column0.setPercentWidth(35);
    column0.setHalignment(HPos.RIGHT);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(65);
    
    form.getColumnConstraints().addAll(column0, column1);

    Label nameLabel = new Label(NAME);
    Label nameText = new Label();
    form.add(nameLabel, 0, 0);
    form.add(nameText, 1, 0);
    
    Label amountLabel = new Label(AMOUNT);
    amountTextField = new TextField();
    form.add(amountLabel, 0, 1);
    form.add(amountTextField, 1, 1);
    amountTextField.setOnKeyReleased(this::handleKeyReleasedAction);
    
    Label measureUnitLabel = new Label(MEASURE_UNIT);
    form.add(measureUnitLabel, 0, 2);
    measureUnitComboBox.setOnAction(e -> modifiedProperty.set(true));
    form.add(measureUnitComboBox, 1, 2);

    shoppingItemTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      selectedShoppingItem = newValue;
      if (newValue != null) {
        nameText.setText(selectedShoppingItem.getName());
        amountTextField
            .setText(selectedShoppingItem.getAmount() == null ? null : Utils.format(selectedShoppingItem.getAmount()));
        measureUnitComboBox.setValue(selectedShoppingItem.getMeasureUnit());
      } else {
        nameText.setText(null);
        amountTextField.setText(null);
        measureUnitComboBox.setValue(null);
      }
      modifiedProperty.set(false);
    });

    return form;
  }

  private ButtonBar createButtonBar() {
    Button updateButton = new Button(UPDATE);

    updateButton.disableProperty().bind(shoppingItemTableView.getSelectionModel()
        .selectedItemProperty().isNull().or(modifiedProperty.not()));
    updateButton.setOnAction(this::updateShoppingItem);

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.setPadding(new Insets(15, 0, 0, 0));
    buttonBar.getButtons().addAll(updateButton);

    return buttonBar;
  }
  
  private void handleKeyReleasedAction(KeyEvent keyEvent) {
    modifiedProperty.set(true);
  }

  private void updateShoppingItem(ActionEvent actionEvent) {
    ShoppingItem update = new ShoppingItem.ShoppingItemBuilder()
        .withAmount((amountTextField.getText() == null || amountTextField.getText().isEmpty()) ? null
            : Float.valueOf(amountTextField.getText()))
        .withMeasureUnit(measureUnitComboBox.getValue())
        .build();
 
    try {
      stockShoppingItemService.update(selectedShoppingItem, update);
    } catch (NotFoundException e) {
      log.debug("stockShoppingItem not found");
    }
    shoppingItemTableView.getSelectionModel().clearSelection();
    modifiedProperty.set(false);
  }
}
