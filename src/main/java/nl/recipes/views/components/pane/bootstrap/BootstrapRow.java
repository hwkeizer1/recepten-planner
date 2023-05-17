package nl.recipes.views.components.pane.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.layout.GridPane;

/**
 * Based on https://edencoding.com/responsive-layouts/
 */
public class BootstrapRow {

  private final List<BootstrapColumn> columns = new ArrayList<>();

  public void addColumn(BootstrapColumn column) {
      if (column == null)
          return;
      columns.add(column);
  }

  public void removeColumn(BootstrapColumn column) {
      columns.remove(column);
  }

  public void clear() {
      columns.clear(); // remove all columns
  }

  public List<BootstrapColumn> getColumns() {
      return Collections.unmodifiableList(columns);
  }

  public int calculateRowPositions(int lastGridPaneRow, Breakpoint currentWindowSize) {
      int inputRow = lastGridPaneRow;
      if (this.getColumns().isEmpty())
          return 0;
      int currentGridPaneColumn = 0; // start in the first column
      for (BootstrapColumn column : this.getColumns()) {
          int contentWidth = column.getColumnWidth(currentWindowSize);
          if (currentGridPaneColumn + contentWidth > 12) {
              lastGridPaneRow++;
              currentGridPaneColumn = 0;
          }
          GridPane.setConstraints(column.getContent(), currentGridPaneColumn, lastGridPaneRow, contentWidth, 1);
          currentGridPaneColumn += contentWidth;
      }
      return lastGridPaneRow - inputRow + 1;
  }
}
