package nl.recipes.views.configurations;

import org.springframework.stereotype.Component;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.views.components.pane.bootstrap.BootstrapColumn;
import nl.recipes.views.components.pane.bootstrap.BootstrapPane;
import nl.recipes.views.components.pane.bootstrap.BootstrapRow;
import nl.recipes.views.components.pane.bootstrap.Breakpoint;

@Slf4j
@Component
public class ConfigurationView {

  private final TagTableEditWidget tagTableEditWidget;
  private final MeasureUnitTableEditWidget measureUnitTableEditWidget;
  private final ShoppingItemTableEditWidget shoppingItemTableEditWidget;
  private final StockShoppingItemTableEditWidget stockShoppingItemTableEditWidget;
  private final IngredientNameTableEditWidget ingredientNameTableEditWidget;

  ScrollPane scrollPane;

  public ConfigurationView(TagTableEditWidget tagListEditWidget,
      MeasureUnitTableEditWidget measureUnitTableEditWidget,
      IngredientNameTableEditWidget ingredientNameTableEditWidget,
      ShoppingItemTableEditWidget shoppingItemTableEditWidget, 
      StockShoppingItemTableEditWidget stockShoppingItemTableEditWidget) {

    this.tagTableEditWidget = tagListEditWidget;
    this.measureUnitTableEditWidget = measureUnitTableEditWidget;
    this.shoppingItemTableEditWidget = shoppingItemTableEditWidget;
    this.stockShoppingItemTableEditWidget = stockShoppingItemTableEditWidget;
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
    row.addColumn(createColumn(tagTableEditWidget.getTagTableEditWidget()));
    row.addColumn(createColumn(measureUnitTableEditWidget.getMeasureUnitTableEditWidget()));
    row.addColumn(createColumn(stockShoppingItemTableEditWidget.getShoppingItemPanel()));
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
    column.setBreakpointColumnWidth(Breakpoint.XLARGE, 3);
    return column;
  }

  private BootstrapColumn createLargeColumn(Node widget) {
    BootstrapColumn column = new BootstrapColumn(widget);
    column.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
    column.setBreakpointColumnWidth(Breakpoint.SMALL, 10);
    column.setBreakpointColumnWidth(Breakpoint.LARGE, 9);
    column.setBreakpointColumnWidth(Breakpoint.XLARGE, 7);
    return column;
  }

}
