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
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.TagService;

import static nl.recipes.views.ViewConstants.*;
import static nl.recipes.views.ViewMessages.*;

@Component
public class TagEditPanel {

  private final TagService tagService;

  TableView<Tag> tagTableView;
  TextField nameTextField;
  Label nameError;

  private Tag selectedTag;
  private BooleanProperty modifiedProperty;

  public TagEditPanel(TagService tagService) {
    this.tagService = tagService;

    initComponents();
  }

  public Node getTagEditPanel() {
    VBox tagEditPanel = new VBox();
    tagEditPanel.setPadding(new Insets(20));
    tagEditPanel.getStyleClass().addAll(CSS_DROP_SHADOW, CSS_WIDGET);
    tagEditPanel.getChildren().addAll(createHeader(), createTableBox(), createForm(), createButtonBar());
    return tagEditPanel;
  }

  private void initComponents() {
    tagTableView = new TableView<>();
    nameTextField = new TextField();
    nameError = new Label();
    
    modifiedProperty = new SimpleBooleanProperty(false);
  }

  private Label createHeader() {
    Label title = new Label(EDIT_TAGS);
    title.getStyleClass().add(CSS_TITLE);
    return title;
  }

  private VBox createTableBox() {
    VBox tableBox = new VBox();

    tagTableView.getStyleClass().add(CSS_BASIC_TABLE);
    tagTableView.setItems(tagService.getReadonlyTagList());
    tagTableView.setMinHeight(200); // prevent table from collapsing
    tagTableView.getSelectionModel().clearSelection();
    
    tagTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      selectedTag = newValue;
      modifiedProperty.set(false);
      if (newValue != null) {
        nameTextField.setText(selectedTag.getName());
        nameError.setText("");
      } else {
        nameTextField.setText("");
      }
    });
    
    TableColumn<Tag, String> nameColumn = new TableColumn<>(TAG);
    nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
    nameColumn.prefWidthProperty().bind(tagTableView.widthProperty());

    tagTableView.getColumns().add(nameColumn);
    
    tagTableView.setRowFactory(callback -> {
      final TableRow<Tag> row = new TableRow<>();
      row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        final int index = row.getIndex();
        if (index >= 0 && index < tagTableView.getItems().size()
            && tagTableView.getSelectionModel().isSelected(index)) {
          tagTableView.getSelectionModel().clearSelection();
          event.consume();
        }
      });
      return row;
    });
    
    tableBox.getChildren().add(tagTableView);
    return tableBox;
  }

  private Node createForm() {
    GridPane form = new GridPane();
    form.setPadding(new Insets(20, 0, 0, 0));
    form.setHgap(20);
//    form.setVgap(15); // No validation fields

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(35);
    column1.setHalignment(HPos.RIGHT);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(65);

    form.getColumnConstraints().addAll(column1, column2);

    form.add(new Label(TAG+COLON), 0, 0);
    form.add(nameTextField, 1, 0);
    form.add(nameError, 1, 1);

    nameTextField.setOnKeyReleased(this::handleKeyReleasedAction);
    nameError.getStyleClass().add(CSS_VALIDATION);

    return form;
  }
  
  private ButtonBar createButtonBar() {
    Button removeButton = new Button(REMOVE);
    Button updateButton = new Button(UPDATE);
    Button createButton = new Button(CREATE);

    removeButton.disableProperty().bind(tagTableView.getSelectionModel().selectedItemProperty().isNull());
    removeButton.setOnAction(this::removeTag);

    updateButton.disableProperty().bind(tagTableView.getSelectionModel().selectedItemProperty().isNull()
        .or(modifiedProperty.not()).or(nameTextField.textProperty().isEmpty()));
    updateButton.setOnAction(this::updateTag);

    createButton.disableProperty().bind(tagTableView.getSelectionModel().selectedItemProperty().isNotNull());
    createButton.setOnAction(this::createTag);

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.setPadding(new Insets(15, 0, 0, 0));
    buttonBar.getButtons().addAll(removeButton, updateButton, createButton);

    return buttonBar;
  }

  private void handleKeyReleasedAction(KeyEvent keyEvent) {
    nameError.setText(null);
    modifiedProperty.set(true);
  }

  private void createTag(ActionEvent actionEvent) {
    Tag tag = new Tag(nameTextField.getText());
    try {
      tagService.create(tag);
      tagTableView.getSelectionModel().select(tag);
    } catch (AlreadyExistsException | IllegalValueException e) {
      nameError.setText(e.getMessage());
    }
  }

  private void updateTag(ActionEvent actionEvent) {
    try {
      tagService.update(selectedTag, nameTextField.getText());
    } catch (NotFoundException | AlreadyExistsException e) {
      nameError.setText(e.getMessage());
    }
    modifiedProperty.set(false);
  }

  private void removeTag(ActionEvent actionEvent) {
    try {
      tagService.remove(selectedTag);
    } catch (NotFoundException e) {
      nameError.setText(e.getMessage());
    }
  }
}
