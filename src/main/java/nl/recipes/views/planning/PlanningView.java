package nl.recipes.views.planning;

import static nl.recipes.views.ViewConstants.*;

import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.services.PlanningService;

@Component
public class PlanningView {
	
	private final PlanningService planningService;

	ObservableList<Recipe> recipeList;
	SplitPane planningPanel;
	AnchorPane leftPanel;
	AnchorPane rightPanel;
//	VBox recipeListBox;
//	VBox planningListBox;
	
	TableView<Recipe> recipeListTableView;
	
	
	public PlanningView(PlanningService planningService) {
		this.planningService = planningService;
		
		planningPanel = new SplitPane();
		planningPanel.getItems().addAll(createRecipeListPanel(), createPlanningListPanel());
	}

	public SplitPane getPlanningPanel() {
		recipeList = planningService.getReadonlyRecipeList();
		recipeListTableView.setItems(recipeList);
		recipeListTableView.setFixedCellSize(25);
		recipeListTableView.prefHeightProperty().bind(recipeListTableView.fixedCellSizeProperty().multiply(Bindings.size(recipeListTableView.getItems())).add(30));
		return planningPanel;
	}
	
	private AnchorPane createRecipeListPanel() {
		Label leftPanelTitle = new Label("Geplande recepten");
		leftPanelTitle.getStyleClass().add("header");
		
		VBox recipeListBox = new VBox();
		recipeListBox.setPadding(new Insets(30));
		recipeListBox.getChildren().addAll(leftPanelTitle, createRecipeListTable());
		
		leftPanel = new AnchorPane();
		AnchorPane.setTopAnchor(recipeListBox, 0.0);
		AnchorPane.setBottomAnchor(recipeListBox, 0.0);
		AnchorPane.setRightAnchor(recipeListBox, 0.0);
		AnchorPane.setLeftAnchor(recipeListBox, 0.0);
		leftPanel.getChildren().addAll(recipeListBox);
		
		return leftPanel;
	}
	
	private TableView<Recipe> createRecipeListTable() {
		recipeListTableView = new TableView<>();
		TableColumn<Recipe, String> nameColumn = new TableColumn<>("Naam");
		TableColumn<Recipe, RecipeType> typeColumn = new TableColumn<>("Type");
		
		recipeListTableView.getColumns().add(nameColumn);
		recipeListTableView.getColumns().add(typeColumn);
		recipeListTableView.getStyleClass().add(RP_TABLE);
		
		nameColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.70));
		typeColumn.prefWidthProperty().bind(recipeListTableView.widthProperty().multiply(0.30));
		
		nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
		typeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getRecipeType()));

		return recipeListTableView;
	}
	
	private AnchorPane createPlanningListPanel() {
		Label rightPanelTitle = new Label("Dag planning");
		rightPanelTitle.getStyleClass().add("header");
		
		VBox planningListBox = new VBox();
		planningListBox.setPadding(new Insets(30));
		planningListBox.getChildren().addAll(rightPanelTitle, createPlanningListTable());
		
		rightPanel = new AnchorPane();
		AnchorPane.setTopAnchor(planningListBox, 0.0);
		AnchorPane.setBottomAnchor(planningListBox, 0.0);
		AnchorPane.setRightAnchor(planningListBox, 0.0);
		AnchorPane.setLeftAnchor(planningListBox, 0.0);
		rightPanel.getChildren().add(planningListBox);
		
		return rightPanel;
	}
	
	private Node createPlanningListTable() {
		VBox planBox = new VBox();
		return planBox;
	}
}
