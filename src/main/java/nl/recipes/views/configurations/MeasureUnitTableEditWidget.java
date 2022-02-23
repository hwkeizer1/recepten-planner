package nl.recipes.views.configurations;

import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
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
import nl.recipes.domain.MeasureUnit;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.MeasureUnitService;

@Component
public class MeasureUnitTableEditWidget {
	
	private static final String RP_TABLE = "rp-table";
	private static final String VALIDATION = "validation";
	private static final String DROP_SHADOW = "drop-shadow";
	private static final String WIDGET = "widget";
	private static final String TITLE = "title";
	
	private final MeasureUnitService measureUnitService;

	VBox rpWidget = new VBox();
	VBox measureUnitTableBox = new VBox();
	VBox measureUnitEditBox = new VBox();
	
	TableView<MeasureUnit> measureUnitTableView = new TableView<>();
	TableColumn<MeasureUnit, String> nameColumn = new TableColumn<>("Maateenheid");
	TableColumn<MeasureUnit, String> pluralNameColumn = new TableColumn<>("Meervoud");
	
	TextField nameTextField = new TextField();
	Label nameError = new Label();
	
	TextField pluralNameTextField = new TextField();
	Label pluralNameError = new Label();
	
	Button createButton = new Button("Toevoegen");
	Button updateButton = new Button("Wijzigen");
	Button removeButton = new Button("Verwijderen");
	
	private MeasureUnit selectedMeasureUnit;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	ChangeListener<MeasureUnit> measureUnitChangeListener;
	
	public MeasureUnitTableEditWidget(MeasureUnitService measureUnitService) {
		this.measureUnitService = measureUnitService;
		
		measureUnitChangeListener = (observable, oldValue, newValue) -> {
			selectedMeasureUnit = newValue;
			modifiedProperty.set(false);
			if (newValue != null) {
				nameTextField.setText(selectedMeasureUnit.getName());
				nameError.setText("");
				pluralNameTextField.setText(selectedMeasureUnit.getPluralName());
				pluralNameError.setText("");
			} else {
				nameTextField.setText(null);
				pluralNameTextField.setText(null);
			}
		};
		initializeMeasureUnitTableBox();
		initializeMeasureUnitEditBox();
		
		Label title = new Label("Maateenheid bewerken");
		title.getStyleClass().add(TITLE);
		
		rpWidget.getStyleClass().addAll(DROP_SHADOW, WIDGET);
		rpWidget.getChildren().addAll(title, measureUnitTableBox,measureUnitEditBox);
		rpWidget.setPadding(new Insets(20));
	}
	
	public Node getMeasureUnitTableEditWidget() {
		return rpWidget;
	}
	
	private void initializeMeasureUnitTableBox() {
		measureUnitTableView.setItems(measureUnitService.getReadonlyMeasureUnitList());
		measureUnitTableView.setFixedCellSize(25);
		measureUnitTableView.prefHeightProperty().bind(Bindings.size(measureUnitTableView.getItems()).multiply(measureUnitTableView.getFixedCellSize()).add(4));
		measureUnitTableView.setMinHeight(200); // prevent table from collapsing
		
		nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
		nameColumn.prefWidthProperty().bind(measureUnitTableView.widthProperty().multiply(0.5));
		pluralNameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPluralName()));
		pluralNameColumn.prefWidthProperty().bind(measureUnitTableView.widthProperty().multiply(0.5));
		
		measureUnitTableView.getColumns().add(nameColumn);
		measureUnitTableView.getColumns().add(pluralNameColumn);
		measureUnitTableView.getSelectionModel().selectedItemProperty().addListener(measureUnitChangeListener);
		
		measureUnitTableBox.getChildren().add(measureUnitTableView);
		measureUnitTableBox.getStyleClass().add(RP_TABLE);
	}
	
	private void initializeMeasureUnitEditBox() {
		initializeButtons();
		
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setPadding(new Insets(0, 20, 0, 0));
		buttonBar.getButtons().addAll(removeButton, updateButton, createButton);
		
		measureUnitEditBox.setPadding(new Insets(10));
		measureUnitEditBox.setSpacing(20);
		measureUnitEditBox.getChildren().addAll(createInputForm(), buttonBar);
	}
	
	private void initializeButtons() {
		removeButton.disableProperty().bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNull());
		removeButton.setOnAction(this::removeMeasureUnit);
		
		updateButton.disableProperty()
		.bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNull().or(modifiedProperty.not()).or(
				nameTextField.textProperty().isEmpty()));
		updateButton.setOnAction(this::updateMeasureUnit);
		
		createButton.disableProperty().bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNotNull());
		createButton.setOnAction(this::createMeasureUnit);
	}
	
	private Node createInputForm() {
		
		GridPane inputForm = new GridPane();
		inputForm.setPadding(new Insets(10, 0, 0, 0));
		inputForm.setHgap(20);
		
		ColumnConstraints column0 = new ColumnConstraints();
		column0.setPercentWidth(40);
		column0.setHalignment(HPos.RIGHT);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);

		inputForm.getColumnConstraints().addAll(column0, column1);
		
		Label nameLabel = new Label("Maateenheid:");
		inputForm.add(nameLabel, 0, 0);
		inputForm.add(nameTextField, 1, 0);
		inputForm.add(nameError,1 ,1 );
		
		Label pluralNameLabel = new Label("Meervoud:");
		inputForm.add(pluralNameLabel, 0, 2);
		inputForm.add(pluralNameTextField, 1, 2);
		inputForm.add(pluralNameError,1 ,3 );
		
		nameTextField.setOnKeyReleased(this::handleKeyReleasedAction);
		nameError.getStyleClass().add(VALIDATION);
		pluralNameTextField.setOnKeyReleased(this::handleKeyReleasedAction);
		pluralNameError.getStyleClass().add(VALIDATION);
		inputForm.prefWidthProperty().bind(measureUnitEditBox.prefWidthProperty());
		
		return inputForm;
	}
	
	private void handleKeyReleasedAction(KeyEvent keyEvent) {
		nameError.setText(null);
		modifiedProperty.set(true);
	}
	
	private void createMeasureUnit(ActionEvent actionEvent) {
		MeasureUnit measureUnit = new MeasureUnit(nameTextField.getText(), pluralNameTextField.getText());
		try {
			measureUnitService.create(measureUnit);
			measureUnitTableView.getSelectionModel().select(measureUnit);
			measureUnitTableView.scrollTo(measureUnit);
		} catch (AlreadyExistsException | IllegalValueException e) {
			nameError.setText(e.getMessage());
		}
	}
	
	private void updateMeasureUnit(ActionEvent actionEvent) {
		MeasureUnit update = new MeasureUnit();
		update.setName(nameTextField.getText());
		update.setPluralName(pluralNameTextField.getText());
		try {
			measureUnitService.update(selectedMeasureUnit, update);
		} catch (NotFoundException | AlreadyExistsException e) {
			nameError.setText(e.getMessage());
		}
		modifiedProperty.set(false);
	}
	
	private void removeMeasureUnit(ActionEvent actionEvent) {
		try {
			measureUnitService.remove(selectedMeasureUnit);
		} catch (NotFoundException e) {
			nameError.setText(e.getMessage());
		}
	}
}
