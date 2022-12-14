package nl.recipes.views.configurations;

import org.springframework.stereotype.Component;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
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
import nl.recipes.domain.MeasureUnit;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.MeasureUnitService;

import static nl.recipes.views.ViewConstants.*;
import static nl.recipes.views.ViewMessages.*;

@Component
public class MeasureUnitEditPanel {

  private final MeasureUnitService measureUnitService;

  TableView<MeasureUnit> measureUnitTableView;
  TextField nameTextField;
  Label nameError;
  TextField pluralNameTextField;

  private MeasureUnit selectedMeasureUnit;
  private BooleanProperty modifiedProperty;

  public MeasureUnitEditPanel(MeasureUnitService measureUnitService) {
    this.measureUnitService = measureUnitService;

    initComponents();
  }

  public Node getMeasureUnitEditPanel() {
    VBox measureUnitEditPanel = new VBox();
    measureUnitEditPanel.setPadding(new Insets(20));
    measureUnitEditPanel.getStyleClass().addAll(CSS_DROP_SHADOW, CSS_WIDGET);
    measureUnitEditPanel.getChildren().addAll(createHeader(), createTableBox(), createForm(),
        createButtonBar());
    return measureUnitEditPanel;
  }

  private void initComponents() {
    measureUnitTableView = new TableView<>();

    nameTextField = new TextField();
    nameError = new Label();
    pluralNameTextField = new TextField();

    modifiedProperty = new SimpleBooleanProperty(false);
  }

  private Label createHeader() {
    Label title = new Label(EDIT_MEASURE_UNITS);
    title.getStyleClass().add(CSS_TITLE);
    return title;
  }

  private VBox createTableBox() {
    VBox tableBox = new VBox();

    measureUnitTableView.getStyleClass().add(CSS_BASIC_TABLE);
    measureUnitTableView.setItems(measureUnitService.getList());
    measureUnitTableView.setMinHeight(200); // prevent table from collapsing
    measureUnitTableView.getSelectionModel().clearSelection();

    measureUnitTableView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          selectedMeasureUnit = newValue;
          modifiedProperty.set(false);
          if (newValue != null) {
            nameTextField.setText(selectedMeasureUnit.getName());
            nameError.setText("");
            pluralNameTextField.setText(selectedMeasureUnit.getPluralName());
          } else {
            nameTextField.setText(null);
            pluralNameTextField.setText(null);
          }
        });

    TableColumn<MeasureUnit, String> nameColumn = new TableColumn<>(MEASURE_UNIT);
    nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));

    TableColumn<MeasureUnit, String> pluralNameColumn = new TableColumn<>(PLURAL_NAME);
    pluralNameColumn
        .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPluralName()));


    nameColumn.prefWidthProperty().bind(measureUnitTableView.widthProperty().multiply(0.5));
    pluralNameColumn.prefWidthProperty().bind(measureUnitTableView.widthProperty().multiply(0.5));

    measureUnitTableView.getColumns().add(nameColumn);
    measureUnitTableView.getColumns().add(pluralNameColumn);

    measureUnitTableView.setRowFactory(callback -> {
      final TableRow<MeasureUnit> row = new TableRow<>();
      row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        final int index = row.getIndex();
        if (index >= 0 && index < measureUnitTableView.getItems().size()
            && measureUnitTableView.getSelectionModel().isSelected(index)) {
          measureUnitTableView.getSelectionModel().clearSelection();
          event.consume();
        }
      });
      return row;
    });

    tableBox.getChildren().add(measureUnitTableView);
    return tableBox;
  }

  private Node createForm() {
    GridPane form = new GridPane();
    form.setPadding(new Insets(20, 0, 0, 0));
    form.setHgap(20);

    ColumnConstraints column0 = new ColumnConstraints();
    column0.setPercentWidth(35);
    column0.setHalignment(HPos.RIGHT);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(65);

    form.getColumnConstraints().addAll(column0, column1);

    form.add(new Label(MEASURE_UNIT + COLON), 0, 0);
    form.add(nameTextField, 1, 0);
    form.add(nameError, 1, 1);
    nameTextField.setOnKeyReleased(this::handleKeyReleasedAction);
    nameError.getStyleClass().add(CSS_VALIDATION);

    form.add(new Label(PLURAL_NAME + COLON), 0, 2);
    form.add(pluralNameTextField, 1, 2);
    pluralNameTextField.setOnKeyReleased(this::handleKeyReleasedAction);

    return form;
  }

  private ButtonBar createButtonBar() {
    Button removeButton = new Button(REMOVE);
    Button updateButton = new Button(UPDATE);
    Button createButton = new Button(CREATE);

    removeButton.disableProperty()
        .bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNull());
    removeButton.setOnAction(this::removeMeasureUnit);

    updateButton.disableProperty()
        .bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNull()
            .or(modifiedProperty.not()).or(nameTextField.textProperty().isEmpty()));
    updateButton.setOnAction(this::updateMeasureUnit);

    createButton.disableProperty()
        .bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNotNull());
    createButton.setOnAction(this::createMeasureUnit);


    ButtonBar buttonBar = new ButtonBar();
    buttonBar.setPadding(new Insets(15, 0, 0, 0));
    buttonBar.getButtons().addAll(removeButton, updateButton, createButton);

    return buttonBar;
  }

  private void handleKeyReleasedAction(KeyEvent keyEvent) {
    nameError.setText(null);
    modifiedProperty.set(true);
  }

  private void createMeasureUnit(ActionEvent actionEvent) {
    MeasureUnit measureUnit = new MeasureUnit.MeasureUnitBuilder().withName(nameTextField.getText())
        .withPluralName(pluralNameTextField.getText()).build();
    try {
      measureUnitService.create(measureUnit);
      measureUnitTableView.scrollTo(measureUnit);
      measureUnitTableView.getSelectionModel().select(measureUnit);
      measureUnitTableView.getSelectionModel().clearSelection();
    } catch (AlreadyExistsException | IllegalValueException e) {
      nameError.setText(e.getMessage());
    }
  }

  private void updateMeasureUnit(ActionEvent actionEvent) {
    MeasureUnit update = new MeasureUnit.MeasureUnitBuilder().withName(nameTextField.getText())
        .withPluralName(pluralNameTextField.getText()).build();
    try {
      measureUnitService.update(selectedMeasureUnit, update);
      measureUnitTableView.getSelectionModel().clearSelection();
    } catch (NotFoundException | AlreadyExistsException e) {
      nameError.setText(e.getMessage());
    }
    modifiedProperty.set(false);
  }

  private void removeMeasureUnit(ActionEvent actionEvent) {
    try {
      measureUnitService.remove(selectedMeasureUnit);
      measureUnitTableView.getSelectionModel().clearSelection();
    } catch (NotFoundException e) {
      nameError.setText(e.getMessage());
    }
  }
}
