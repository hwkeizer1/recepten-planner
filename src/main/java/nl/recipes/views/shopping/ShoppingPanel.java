package nl.recipes.views.shopping;

import java.util.List;

import static nl.recipes.views.ViewConstants.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatProperty;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.ShoppingItem;

@Slf4j
public class ShoppingPanel {

  private String header;
  private boolean withToolBar = false;
  private List<Button> buttons;
  private ObservableList<ShoppingItem> shoppingItems;
  private boolean showCheckBoxes;
  private TableView<ShoppingItem> tableView;

  private ShoppingPanel() {}

  private ShoppingPanel(ShoppingPanelBuilder builder) {
    this.header = builder.header;
    this.withToolBar = builder.withToolBar;
    this.buttons = builder.buttons;
    this.shoppingItems = builder.shoppingItems;
    this.showCheckBoxes = builder.showCheckBoxes;
  }

  /*
   * Builder convenience class
   */
  public static class ShoppingPanelBuilder {
    private String header;
    private boolean withToolBar;
    private List<Button> buttons;
    private ObservableList<ShoppingItem> shoppingItems;
    private boolean showCheckBoxes;

    public ShoppingPanelBuilder withHeader(String header) {
      this.header = header;
      return this;
    }

    public ShoppingPanelBuilder withToolBar() {
      this.withToolBar = true;
      return this;
    }

    public ShoppingPanelBuilder withButtons(List<Button> buttons) {
      this.buttons = buttons;
      return this;
    }

    public ShoppingPanelBuilder withObservableList(ObservableList<ShoppingItem> shoppingItems) {
      this.shoppingItems = shoppingItems;
      return this;
    }

    public ShoppingPanelBuilder withCheckBoxes(boolean showCheckBoxes) {
      this.showCheckBoxes = showCheckBoxes;
      return this;
    }

    public ShoppingPanel build() {
      return new ShoppingPanel(this);
    }
  }

  public VBox view() {
    VBox vbox = new VBox();
    vbox.getStyleClass().addAll(CSS_DROP_SHADOW, CSS_SHOPPING_PANEL);
    vbox.getChildren().add(createHeader());
    if (withToolBar) {
      vbox.getChildren().add(createToolBar());
    }
    vbox.getChildren().add(createTableView());
    return vbox;
  }

  public void refresh() {
    tableView.getSelectionModel().clearSelection();
    tableView.refresh();
  }

  public void refresh(ObservableList<ShoppingItem> shoppingItems) {
    this.shoppingItems = shoppingItems;
    tableView.getSelectionModel().clearSelection();
    tableView.refresh();
  }

  private HBox createHeader() {
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(0, 0, 5, 0));
    Label label = new Label(header);
    label.getStyleClass().add(CSS_SHOPPING_PANEL_TITLE);
    hbox.getChildren().add(label);
    return hbox;
  }

  private ToolBar createToolBar() {
    ToolBar toolBar = new ToolBar();
    toolBar.setMinHeight(24);
    toolBar.setPrefHeight(24);
    toolBar.setMaxHeight(24);
    toolBar.setPadding(new Insets(2, 5, 2, 5));
    if (buttons != null && !buttons.isEmpty()) {
      toolBar.getItems().addAll(buttons);
    }
    return toolBar;
  }

  private HBox createTableView() {
    HBox hbox = new HBox();
    hbox.getStyleClass().add(CSS_SHOPPING_PANEL_BORDER);
    tableView = new TableView<>();

    tableView.focusedProperty().addListener((obs, oldVal, newVal) -> {
      if (Boolean.FALSE.equals(newVal)) {
        tableView.getSelectionModel().clearSelection();
      }
    });

    tableView.setEditable(true);

    tableView.getStyleClass().addAll(CSS_SHOPPING_PANEL_TABLE);
    tableView.setItems(shoppingItems);
    tableView.getSelectionModel().clearSelection();

    tableView.setFixedCellSize(19);
    if (tableView.getItems().size() > 20) {
      tableView.setMaxHeight(380);
    }
    tableView.prefHeightProperty().bind(
        tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems())).add(2));

    TableColumn<ShoppingItem, Number> amountColumn = new TableColumn<>();
    amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
    amountColumn.setCellValueFactory(cellData -> {
      ShoppingItem shoppingItem = cellData.getValue();
      try {
        JavaBeanFloatPropertyBuilder builder = JavaBeanFloatPropertyBuilder.create();
        JavaBeanFloatProperty property = builder.bean(shoppingItem).name("amount").build();
        return property;
      } catch (Exception e) {
        log.error("Error creating amount column {}", e);
        return null;
      }
    });

    TableColumn<ShoppingItem, String> measureUnitColumn = new TableColumn<>();
    measureUnitColumn.setCellValueFactory(c -> {
      if (c.getValue().getMeasureUnit() == null) {
        return new ReadOnlyObjectWrapper<>();
      } else {
        return new ReadOnlyObjectWrapper<>(getMeasureUnitLabel(c.getValue()));
      }
    });

    TableColumn<ShoppingItem, String> nameColumn = new TableColumn<>();
    nameColumn.setCellValueFactory(
        c -> new ReadOnlyObjectWrapper<>(getShoppingItemNameLabel(c.getValue())));

    TableColumn<ShoppingItem, Boolean> listColumn = new TableColumn<>();
    if (showCheckBoxes) {
      listColumn.setCellValueFactory(cellData -> {
        ShoppingItem shoppingItem = cellData.getValue();
        try {
          JavaBeanBooleanPropertyBuilder builder = JavaBeanBooleanPropertyBuilder.create();
          return builder.bean(shoppingItem).name("onList").build();
        } catch (Exception e) {
          log.error("Error creating checkbox column");
          return null;
        }
      });
      listColumn.setCellFactory(c -> new CheckBoxTableCell<>());
    }


    amountColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.14));
    measureUnitColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.26));
    nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.48));
    listColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.09));

    tableView.getColumns().add(amountColumn);
    tableView.getColumns().add(measureUnitColumn);
    tableView.getColumns().add(nameColumn);
    tableView.getColumns().add(listColumn);

    tableView.setRowFactory(tv -> new TableRow<ShoppingItem>() {
      @Override
      protected void updateItem(ShoppingItem item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && item.highlight()) {
          setStyle("-fx-background-color: #EC9242;");
          Tooltip toolTip = new Tooltip("Ook benodigd voor geplande recepten");
          toolTip.setShowDelay(new Duration(500));
          setTooltip(toolTip);
        } else {
          setStyle("");
          setTooltip(null);
        }
      }
    });

    hbox.getChildren().add(tableView);
    return hbox;
  }

  private String getMeasureUnitLabel(ShoppingItem shoppingItem) {
    return (shoppingItem.getAmount() == null || shoppingItem.getAmount() <= 1)
        ? shoppingItem.getMeasureUnit().getName()
        : shoppingItem.getMeasureUnit().getPluralName();
  }

  private String getShoppingItemNameLabel(ShoppingItem shoppingItem) {
    if (shoppingItem.getPluralName() == null)
      return shoppingItem.getName();
    return (shoppingItem.getAmount() == null || shoppingItem.getAmount() <= 1)
        ? shoppingItem.getName()
        : shoppingItem.getPluralName();
  }
}
