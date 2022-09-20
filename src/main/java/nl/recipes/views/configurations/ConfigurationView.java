package nl.recipes.views.configurations;

import org.springframework.stereotype.Component;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import nl.recipes.views.components.pane.bootstrap.BootstrapColumn;
import nl.recipes.views.components.pane.bootstrap.BootstrapPane;
import nl.recipes.views.components.pane.bootstrap.BootstrapRow;
import nl.recipes.views.components.pane.bootstrap.Breakpoint;

@Component
public class ConfigurationView {

  private final TagEditPanel tagEditPanel;
  private final MeasureUnitEditPanel measureUnitEditPanel;
  private final ShoppingItemTableEditWidget shoppingItemTableEditWidget;
  private final StockShoppingEditPanel stockShoppingEditPanel;
  private final IngredientNameTableEditWidget ingredientNameTableEditWidget;

  ScrollPane scrollPane;

  public ConfigurationView(
      TagEditPanel tagEditPanel,
      MeasureUnitEditPanel measureUnitEditPanel,
      IngredientNameTableEditWidget ingredientNameTableEditWidget,
      ShoppingItemTableEditWidget shoppingItemTableEditWidget, 
      StockShoppingEditPanel stockShoppingEditPanel) {

    this.tagEditPanel = tagEditPanel;
    this.measureUnitEditPanel = measureUnitEditPanel;
    this.shoppingItemTableEditWidget = shoppingItemTableEditWidget;
    this.stockShoppingEditPanel = stockShoppingEditPanel;
    this.ingredientNameTableEditWidget = ingredientNameTableEditWidget;
    BootstrapPane root = makeView();

    scrollPane = new ScrollPane(root);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
    scrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
      root.notifyWidthChange(newValue);
    });
  }

  public Node getConfigurationViewPanel() {
    return scrollPane;
  }

  private BootstrapPane makeView() {
    BootstrapPane bootstrapPane = new BootstrapPane();
    bootstrapPane.setPadding(new Insets(15));
    bootstrapPane.getStyleClass().add("background");
    bootstrapPane.setVgap(25);
    bootstrapPane.setHgap(25);

    BootstrapRow row = new BootstrapRow();
    row.addColumn(createColumn(tagEditPanel.getTagEditPanel()));
    row.addColumn(createColumn(measureUnitEditPanel.getMeasureUnitEditPanel()));
    row.addColumn(createColumn(stockShoppingEditPanel.getStockShoppingEditPanel()));
    row.addColumn(createLargeColumn(shoppingItemTableEditWidget.getShoppingItemPanel()));
    row.addColumn(
        createLargeColumn(ingredientNameTableEditWidget.getIngredientNameTableEditWidget()));

    bootstrapPane.addRow(row);
    
    

    return bootstrapPane;
  }

  private BootstrapColumn createColumn(Node widget) {
    BootstrapColumn column = new BootstrapColumn(widget);
    column.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
    column.setBreakpointColumnWidth(Breakpoint.SMALL, 9);
    column.setBreakpointColumnWidth(Breakpoint.LARGE, 6);
    column.setBreakpointColumnWidth(Breakpoint.XLARGE, 4);
    return column;
  }

  private BootstrapColumn createLargeColumn(Node widget) {
    BootstrapColumn column = new BootstrapColumn(widget);
    column.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
    column.setBreakpointColumnWidth(Breakpoint.SMALL, 10);
    column.setBreakpointColumnWidth(Breakpoint.LARGE, 9);
    column.setBreakpointColumnWidth(Breakpoint.XLARGE, 6);
    return column;
  }

}
