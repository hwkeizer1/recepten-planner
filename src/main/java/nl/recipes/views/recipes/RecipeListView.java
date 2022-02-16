package nl.recipes.views.recipes;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.controllers.views.RootController;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.services.RecipeService;

@Slf4j
@Component
public class RecipeListView {

	private final RecipeService recipeService;
	
	// TODO: for now this is still a controller, will be a RootView component later
	private RootController rootController;
	
	AnchorPane recipeListPanel = new AnchorPane();
	
	VBox recipeListVBox = new VBox();
	
	TableView<Recipe> recipeListTableView = new TableView<>();
	TableColumn<Recipe, String> nameColumn = new TableColumn<>("Naam");
	TableColumn<Recipe, RecipeType> typeColumn = new TableColumn<>("Type");
	TableColumn<Recipe, String> tagColumn = new TableColumn<>("Trefwoorden");
	TableColumn<Recipe, LocalDate> lastServedColumn = new TableColumn<>("Laatst gegegeten");
	TableColumn<Recipe, Integer> timesServedColumn = new TableColumn<>("Aantal keren gegeten");

	public RecipeListView(RecipeService recipeService) {
		this.recipeService = recipeService;
		
		AnchorPane.setTopAnchor(recipeListVBox, 0.0);
		AnchorPane.setBottomAnchor(recipeListVBox, 0.0);
		AnchorPane.setRightAnchor(recipeListVBox, 0.0);
		AnchorPane.setLeftAnchor(recipeListVBox, 0.0);
		
		recipeListPanel.getStylesheets().add(getClass().getResource("/css/recipe-list-view.css").toExternalForm());
		recipeListPanel.getChildren().add(recipeListVBox);
		recipeListVBox.getChildren().add(recipeListTableView);
		recipeListVBox.getStyleClass().add("background");
//		recipeListVBox.setBackground(new Background(new BackgroundFill(Color.web("#ffffcc"), CornerRadii.EMPTY , Insets.EMPTY)));
		
		initializeRecipeListTableView();
	}
	
	public AnchorPane getRecipeListPanel() {
		recipeListTableView.setItems(recipeService.getReadonlyRecipeList());
		recipeListTableView.setFixedCellSize(25);
		recipeListTableView.prefHeightProperty().bind(recipeListTableView.fixedCellSizeProperty().multiply(Bindings.size(recipeListTableView.getItems())));
		return recipeListPanel;
	}
	
	public void setRootController(RootController rootController) {
		this.rootController = rootController;
	}
	
	private void initializeRecipeListTableView() {
		recipeListTableView.getColumns().add(nameColumn);
		recipeListTableView.getColumns().add(typeColumn);
		recipeListTableView.getColumns().add(tagColumn);
		recipeListTableView.getColumns().add(lastServedColumn);
		recipeListTableView.getColumns().add(timesServedColumn);
		recipeListTableView.getStyleClass().add("recipe-table");
		
		
		nameColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.3));
		typeColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.3));
		tagColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.1));
		lastServedColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.15));
		timesServedColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.15));
		
		recipeListTableView.setRowFactory(tv -> {
			TableRow<Recipe> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && (!row.isEmpty())) {
					Recipe selectedRecipe = row.getItem();
					showSingleRecipeView(selectedRecipe);
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
//		recipeListVBox.setPadding(new Insets(20));
//		recipeListPanel.getChildren().add(recipeListVBox);
	}
	
	private void showSingleRecipeView(Recipe recipe) {
		if (rootController != null) {
			rootController.showSingleViewRecipePanel(recipe);
		}
	}
}
