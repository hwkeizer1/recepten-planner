package nl.recipes.views.configurations;

import org.springframework.stereotype.Component;

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
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.TagService;

import static nl.recipes.views.ViewConstants.*;

@Component
public class TagTableEditWidget {

  private final TagService tagService;

  VBox rpWidget = new VBox();

  VBox tagTableBox = new VBox();

  VBox tagEditBox = new VBox();

  TableView<Tag> tagTableView = new TableView<>();

  TableColumn<Tag, String> nameColumn = new TableColumn<>("Categorie");

  TextField nameTextField = new TextField();

  Label nameError = new Label();

  Button createButton = new Button("Toevoegen");

  Button updateButton = new Button("Wijzigen");

  Button removeButton = new Button("Verwijderen");

  private Tag selectedTag;

  private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);

  ChangeListener<Tag> tagChangeListener;

  public TagTableEditWidget(TagService tagService) {
    this.tagService = tagService;

    tagChangeListener = (observable, oldValue, newValue) -> {
      selectedTag = newValue;
      modifiedProperty.set(false);
      if (newValue != null) {
        nameTextField.setText(selectedTag.getName());
        nameError.setText("");
      } else {
        nameTextField.setText("");
      }
    };

    initializeTagTableBox();
    initializeTagEditBox();

    Label title = new Label("Categoriëen bewerken");
    title.getStyleClass().add(TITLE);

    rpWidget.getStyleClass().addAll(DROP_SHADOW, WIDGET);
    rpWidget.getChildren().addAll(title, tagTableBox, tagEditBox);
    rpWidget.setPadding(new Insets(20));
  }

  public Node getTagTableEditWidget() {
    return rpWidget;
  }

  private void initializeTagTableBox() {
    tagTableView.setItems(tagService.getReadonlyTagList());

    nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
    nameColumn.prefWidthProperty().bind(tagTableView.widthProperty());
    tagTableView.setMinHeight(200); // prevent table from collapsing

    tagTableView.getColumns().add(nameColumn);
    tagTableView.getSelectionModel().selectedItemProperty().addListener(tagChangeListener);

    tagTableBox.getChildren().add(tagTableView);
    tagTableView.getStyleClass().add(RP_TABLE);
  }

  private void initializeTagEditBox() {
    initializeButtons();

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.getButtons().addAll(removeButton, updateButton, createButton);

    tagEditBox.setPadding(new Insets(10));
    tagEditBox.setSpacing(20);
    tagEditBox.getChildren().addAll(createInputForm(), buttonBar);

  }

  private void initializeButtons() {
    removeButton.disableProperty()
        .bind(tagTableView.getSelectionModel().selectedItemProperty().isNull());
    removeButton.setOnAction(this::removeTag);

    updateButton.disableProperty().bind(tagTableView.getSelectionModel().selectedItemProperty()
        .isNull().or(modifiedProperty.not()).or(nameTextField.textProperty().isEmpty()));
    updateButton.setOnAction(this::updateTag);

    createButton.disableProperty()
        .bind(tagTableView.getSelectionModel().selectedItemProperty().isNotNull());
    createButton.setOnAction(this::createTag);
  }

  private Node createInputForm() {

    GridPane inputForm = new GridPane();
    inputForm.setPadding(new Insets(10, 0, 0, 0));
    inputForm.setHgap(20);

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(40);
    column1.setHalignment(HPos.RIGHT);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(50);

    inputForm.getColumnConstraints().addAll(column1, column2);

    Label nameLabel = new Label("Categorie:");
    inputForm.add(nameLabel, 0, 0);
    inputForm.add(nameTextField, 1, 0);
    inputForm.add(nameError, 1, 1);

    nameTextField.setOnKeyReleased(this::handleKeyReleasedAction);
    nameError.getStyleClass().add(VALIDATION);
    inputForm.prefWidthProperty().bind(tagEditBox.prefWidthProperty());

    return inputForm;
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
