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
	
	private final TagTableEditWidget tagTableEditWidget;
	private final MeasureUnitTableEditWidget measureUnitTableEditWidget;

	ScrollPane scrollPane;
	
	public ConfigurationView(TagTableEditWidget tagListEditWidget, 
			MeasureUnitTableEditWidget measureUnitTableEditWidget) {
		
		this.tagTableEditWidget = tagListEditWidget;
		this.measureUnitTableEditWidget = measureUnitTableEditWidget;
		BootstrapPane root = makeView();
		root.getStylesheets().add(getClass().getResource("/css/widget.css").toExternalForm());
		
		scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
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
		column.setBreakpointColumnWidth(Breakpoint.LARGE, 8);
		column.setBreakpointColumnWidth(Breakpoint.XLARGE, 6);
		return column;
	}
	
	
	
}
