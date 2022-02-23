package nl.recipes.views.root;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import nl.recipes.domain.Recipe;
import nl.recipes.views.backup.RestoreBackupDialog;
import nl.recipes.views.configurations.ConfigurationView;
import nl.recipes.views.recipes.RecipeListView;
import nl.recipes.views.recipes.SingelRecipeView;

@Component
public class RootView {
	
	private final ConfigurationView configurationView;
	private final RecipeListView recipeListView;
	private final SingelRecipeView singleRecipeView;
	private final RestoreBackupDialog restoreBackupDialog;

	private BorderPane rootWindow = new BorderPane();
	
	private MenuBar menuBar = new MenuBar();

	public RootView(SingelRecipeView singleRecipeView, 
			RecipeListView recipeListView,
			ConfigurationView configurationView, 
			RestoreBackupDialog restoreBackupDialog) {
		
		this.configurationView = configurationView;
		this.recipeListView = recipeListView;
		this.singleRecipeView = singleRecipeView;
		this.restoreBackupDialog = restoreBackupDialog;
		
		recipeListView.setRootView(this);
		
		initializeRootWindow();
		initializeMenu();
	}

	public Parent asParent() {
        return rootWindow ;
    }
	
	private void initializeRootWindow() {
		rootWindow.getStyleClass().add("background");
		rootWindow.setCenter(recipeListView.getRecipeListPanel());
		rootWindow.setTop(menuBar);
	}
	
	private void initializeMenu() {
		
		SeparatorMenuItem firstSeparator = new SeparatorMenuItem();
		SeparatorMenuItem secondSeparator = new SeparatorMenuItem();
		
		MenuItem recipeList = new MenuItem("Recepten lijst");
		recipeList.setOnAction(this::handleRecipeListPanel);
		
		MenuItem createBackup = new MenuItem("Backup maken");
		createBackup.setOnAction(this::handleBackupDialog);
		
		MenuItem restoreBackup = new MenuItem("Backup terugzetten");
		restoreBackup.setOnAction(this::handleRestoreBackupDialog);
		
		MenuItem exit = new MenuItem("Programma afsluiten");
		exit.setOnAction(e -> Platform.exit());
		
		MenuItem editBasicElements = new MenuItem("Wijzig basis elementen");
		editBasicElements.setOnAction(this::handleConfigurationPanel);
		
		Menu recipePlanner = new Menu("Recepten-planner");
		recipePlanner.getItems().addAll(recipeList, firstSeparator, createBackup, restoreBackup, secondSeparator, exit);
		
		Menu configuration = new Menu("Instellingen");
		configuration.getItems().add(editBasicElements);
		
		menuBar.getMenus().addAll(recipePlanner, configuration);
	}
	
	public void handleBackupDialog(ActionEvent actionEvent) {
	}
	
	public void handleRestoreBackupDialog(ActionEvent actionEvent) {
		restoreBackupDialog.showRestoreBackupDialog();
	}
	
	public void handleRecipeListPanel(ActionEvent actionEvent) {
		rootWindow.setCenter(recipeListView.getRecipeListPanel());
	}
	
	public void handleConfigurationPanel(ActionEvent actionEvent) {
		rootWindow.setCenter(configurationView.getConfigurationViewPanel());
	}
	
	public void showSingleViewRecipePanel(Recipe recipe) {
		rootWindow.setCenter(singleRecipeView.getSingleRecipeViewPanel(recipe));
	}
	

}