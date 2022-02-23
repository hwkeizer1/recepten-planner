package nl.recipes.views.recipes;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.services.RecipeService;
import nl.recipes.views.root.RootView;

@Component
public class RecipeListView {

	private static final String RP_TABLE = "rp-table";

	private final RecipeService recipeService;
	
	private RootView rootView;
	
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
		
		recipeListPanel.getChildren().add(recipeListVBox);
		recipeListVBox.getChildren().add(recipeListTableView);
		
		initializeRecipeListTableView();
	}
	
	public AnchorPane getRecipeListPanel() {
		recipeListTableView.setItems(recipeService.getReadonlyRecipeList());
		recipeListTableView.setFixedCellSize(25);
		recipeListTableView.prefHeightProperty().bind(recipeListTableView.fixedCellSizeProperty().multiply(Bindings.size(recipeListTableView.getItems())));
		return recipeListPanel;
	}
	
	public void setRootView(RootView rootView) {
		this.rootView = rootView;
	}
	
	private void initializeRecipeListTableView() {
		recipeListTableView.getColumns().add(nameColumn);
		recipeListTableView.getColumns().add(typeColumn);
		recipeListTableView.getColumns().add(tagColumn);
		recipeListTableView.getColumns().add(lastServedColumn);
		recipeListTableView.getColumns().add(timesServedColumn);
		recipeListTableView.getStyleClass().add(RP_TABLE);
		
		
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
	}
	
	private void showSingleRecipeView(Recipe recipe) {
		if (rootView != null) {
			rootView.showSingleViewRecipePanel(recipe);
		}
	}
}
