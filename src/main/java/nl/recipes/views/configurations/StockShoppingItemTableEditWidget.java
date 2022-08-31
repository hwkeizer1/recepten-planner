package nl.recipes.views.configurations;

import static nl.recipes.views.ViewConstants.DROP_SHADOW;
import static nl.recipes.views.ViewConstants.RP_TABLE;
import static nl.recipes.views.ViewConstants.TITLE;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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
public class StockShoppingItemTableEditWidget implements ListChangeListener<MeasureUnit> {

  private final StockShoppingItemService stockShoppingItemService;
  private final MeasureUnitService measureUnitService;

  TableView<ShoppingItem> shoppingItemTableView;
  
  Label nameText;
  
  TextField amountTextField;
  
  SearchableComboBox<MeasureUnit> measureUnitComboBox;

  private ShoppingItem selectedShoppingItem;

  private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);

  public StockShoppingItemTableEditWidget(StockShoppingItemService stockShoppingItemService,
      MeasureUnitService measureUnitService) {
    this.stockShoppingItemService = stockShoppingItemService;
    this.measureUnitService = measureUnitService;

    this.measureUnitService.addListener(this);
    
    measureUnitComboBox = new SearchableComboBox<>();
    measureUnitComboBox.setConverter(new MeasureUnitStringConverter());
    TextFields.bindAutoCompletion(measureUnitComboBox.getEditor(), measureUnitComboBox.getItems(), measureUnitComboBox.getConverter());
    measureUnitComboBox.getItems().setAll(this.measureUnitService.getReadonlyMeasureUnitList());
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
    Label title = new Label("Voorraad boodschappen bewerken");
    title.getStyleClass().add(TITLE);
    return title;
  }

  private VBox createTable() {
    shoppingItemTableView = new TableView<>();
    shoppingItemTableView.getStyleClass().add(RP_TABLE);
    shoppingItemTableView.setItems(stockShoppingItemService.getReadonlyShoppingItemList());
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

    nameColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.40));
    amountColumn.prefWidthProperty().bind(shoppingItemTableView.widthProperty().multiply(0.30));
    measureUnitColumn.prefWidthProperty()
        .bind(shoppingItemTableView.widthProperty().multiply(0.30));
    

    shoppingItemTableView.getColumns().add(nameColumn);
    shoppingItemTableView.getColumns().add(amountColumn);
    shoppingItemTableView.getColumns().add(measureUnitColumn);

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
    column1.setPercentWidth(60);
    
    form.getColumnConstraints().addAll(column0, column1);

    Label nameLabel = new Label("Naam:");
    nameText = new Label();
    form.add(nameLabel, 0, 0);
    form.add(nameText, 1, 0);
    
    Label amountLabel = new Label("Hoeveelheid:");
    amountTextField = new TextField();
    form.add(amountLabel, 0, 1);
    form.add(amountTextField, 1, 1);
    amountTextField.setOnKeyReleased(this::handleKeyReleasedAction);
    
    Label measureUnitLabel = new Label("Maateenheid:");
    form.add(measureUnitLabel, 0, 2);
    measureUnitComboBox.setMinWidth(150);
    measureUnitComboBox.setOnAction(e -> modifiedProperty.set(true));
    form.add(measureUnitComboBox, 1, 2);

    shoppingItemTableView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          selectedShoppingItem = newValue;
          if (newValue != null) {
            nameText.setText(selectedShoppingItem.getName());
            amountTextField.setText(selectedShoppingItem.getAmount() == null ? null
                : Utils.format(selectedShoppingItem.getAmount()));
            measureUnitComboBox.setValue(selectedShoppingItem.getMeasureUnit());
          } else {
            amountTextField.setText(null);
            measureUnitComboBox.setValue(null);
          }
          modifiedProperty.set(false);
        });

    return form;
  }

  private ButtonBar createButtonBar() {
    Button updateButton = new Button("Wijzigen");

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
    modifiedProperty.set(false);
  }

  @Override
  public void onChanged(Change<? extends MeasureUnit> c) {
    measureUnitComboBox.getItems().setAll(measureUnitService.getReadonlyMeasureUnitList());
  }

}
