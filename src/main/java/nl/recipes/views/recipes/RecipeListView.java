package nl.recipes.views.recipes;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.services.RecipeService;
import nl.recipes.views.root.RootView;

@Slf4j
@Component
public class RecipeListView {

	private static final String RP_TABLE = "rp-table";

	private final RecipeService recipeService;
	
	private RootView rootView;
	
	private Recipe selectedRecipe;
	ChangeListener<Recipe> recipeChangeListener;
	
	AnchorPane recipeListPanel = new AnchorPane();
	
	Alert removeAlert;
	
	HBox buttonPanel = new HBox();
	VBox recipeListVBox = new VBox();
	
	TableView<Recipe> recipeListTableView = new TableView<>();
	TableColumn<Recipe, String> nameColumn = new TableColumn<>("Naam");
	TableColumn<Recipe, RecipeType> typeColumn = new TableColumn<>("Type");
	TableColumn<Recipe, String> tagColumn = new TableColumn<>("Trefwoorden");
	TableColumn<Recipe, LocalDate> lastServedColumn = new TableColumn<>("Laatst gegegeten");
	TableColumn<Recipe, Integer> timesServedColumn = new TableColumn<>("Aantal keren gegeten");

	public RecipeListView(RecipeService recipeService) {
		this.recipeService = recipeService;
		
		recipeChangeListener = (observable, oldValue, newValue) -> {
			selectedRecipe = newValue;
		};
		
		AnchorPane.setTopAnchor(recipeListVBox, 0.0);
		AnchorPane.setBottomAnchor(recipeListVBox, 0.0);
		AnchorPane.setRightAnchor(recipeListVBox, 0.0);
		AnchorPane.setLeftAnchor(recipeListVBox, 0.0);
		
		recipeListPanel.getChildren().add(recipeListVBox);
		
		buttonPanel.setPadding(new Insets(10));
		buttonPanel.setSpacing(30);
		
		recipeListVBox.getChildren().add(buttonPanel);
		recipeListVBox.getChildren().add(recipeListTableView);
		
		initializeRecipeListTable();
		initializeButtonPanel();
	}
	
	public void setRootView(RootView rootView) {
		this.rootView = rootView;
	}
	
	public AnchorPane getRecipeListPanel() {
		recipeListTableView.setItems(recipeService.getReadonlyRecipeList());
		recipeListTableView.setFixedCellSize(25);
		recipeListTableView.prefHeightProperty().bind(recipeListTableView.fixedCellSizeProperty().multiply(Bindings.size(recipeListTableView.getItems())));
		return recipeListPanel;
	}
	
	private void initializeRecipeListTable() {
		recipeListTableView.getColumns().add(nameColumn);
		recipeListTableView.getColumns().add(typeColumn);
		recipeListTableView.getColumns().add(tagColumn);
		recipeListTableView.getColumns().add(lastServedColumn);
		recipeListTableView.getColumns().add(timesServedColumn);
		recipeListTableView.getStyleClass().add(RP_TABLE);
		
		recipeListTableView.getSelectionModel().selectedItemProperty().addListener(recipeChangeListener);
		
		nameColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.35));
		typeColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.15));
		tagColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.2));
		lastServedColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.15));
		timesServedColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.15));
		
		recipeListTableView.setRowFactory(tv -> {
			TableRow<Recipe> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && (!row.isEmpty())) {
					Recipe selectedRecipe = row.getItem();
					showRecipeSingleView(selectedRecipe);
				}
			});
			return row;
		});
		
		nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
		typeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getRecipeType()));
		tagColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTagString()));
		lastServedColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLastServed()));
		timesServedColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTimesServed()));
		
		
		VBox.setVgrow(recipeListVBox, Priority.NEVER);
	}
	
	private void initializeButtonPanel() {
		Button newRecipe = new Button("Nieuw recept toevoegen");
		newRecipe.setOnAction(this::showNewRecipeEditView);
		buttonPanel.getChildren().add(newRecipe);
		
		Button removeRecipeButton = new Button("Recept verwijderen");
		removeRecipeButton.setOnAction(this::showRemoveRecipeAlert);
		buttonPanel.getChildren().add(removeRecipeButton);
		}
	
	private void showRecipeSingleView(Recipe recipe) {
		if (rootView != null) {
			rootView.showRecipeSingleViewPanel(recipe);
		}
	}
	
	private void showNewRecipeEditView(ActionEvent event) {
		if (rootView != null) {
			rootView.showNewRecipeEditViewPanel();
		}
	}
	
	private void showRemoveRecipeAlert(ActionEvent event) {
		if (selectedRecipe != null) {
			removeAlert = new Alert(AlertType.CONFIRMATION, "Weet u zeker dat u het geselecteerde recept '"
					+ selectedRecipe.getName() + "'  wilt verwijderen?");
			removeAlert.showAndWait()
		      .filter(response -> response == ButtonType.OK)
		      .ifPresent(response -> removeRecipe());
		}
	}
	
	private void removeRecipe() {
		Optional<Recipe> optionalRecipe = recipeService.findById(selectedRecipe.getId());
		if (optionalRecipe.isPresent()) {
			recipeService.remove(optionalRecipe.get());
		}

	}
}
