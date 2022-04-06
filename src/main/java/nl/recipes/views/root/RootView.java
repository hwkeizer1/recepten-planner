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
import nl.recipes.views.backup.CreateBackupDialog;
import nl.recipes.views.backup.RestoreBackupDialog;
import nl.recipes.views.configurations.ConfigurationView;
import nl.recipes.views.configurations.SettingsDialog;
import nl.recipes.views.planning.PlanningView;
import nl.recipes.views.recipes.RecipeEditView;
import nl.recipes.views.recipes.RecipeListView;
import nl.recipes.views.recipes.RecipeSingleView;

import static nl.recipes.views.ViewConstants.*;

@Component
public class RootView {
	
	private final ConfigurationView configurationView;
	private final RecipeListView recipeListView;
	private final RecipeSingleView recipeSingleView;
	private final RecipeEditView recipeEditView;
	private final RestoreBackupDialog restoreBackupDialog;
	private final CreateBackupDialog createBackupDialog;
	private final SettingsDialog settingsDialog;
	private final PlanningView planningView;

	private BorderPane rootWindow = new BorderPane();
	
	private MenuBar menuBar = new MenuBar();

	public RootView(RecipeSingleView recipeSingleView, 
			RecipeListView recipeListView,
			RecipeEditView recipeEditView,
			ConfigurationView configurationView, 
			RestoreBackupDialog restoreBackupDialog, 
			CreateBackupDialog createBackupDialog, 
			SettingsDialog settingsDialog, 
			PlanningView planningView) {
		
		this.configurationView = configurationView;
		this.recipeListView = recipeListView;
		this.recipeSingleView = recipeSingleView;
		this.recipeEditView = recipeEditView;
		this.restoreBackupDialog = restoreBackupDialog;
		this.createBackupDialog = createBackupDialog;
		this.settingsDialog = settingsDialog;
		this.planningView = planningView;
		
		recipeListView.setRootView(this);
		recipeSingleView.setRootView(this);
		recipeEditView.setRootView(this);
		
		initializeRootWindow();
		initializeMenu();
	}

	public Parent asParent() {
        return rootWindow ;
    }
	
	private void initializeRootWindow() {
		rootWindow.getStyleClass().add(BACKGROUND);
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
		
		MenuItem editSettings = new MenuItem("Wijzig instellingen");
		editSettings.setOnAction(this::handleSettingsDialog);
		
		MenuItem planningOverview = new MenuItem("Planning overzicht");
		planningOverview.setOnAction(this::handlePlanningPanel);
		
		Menu recipePlanner = new Menu("Recepten-planner");
		recipePlanner.getItems().addAll(recipeList, firstSeparator, createBackup, restoreBackup, secondSeparator, exit);
		
		Menu configuration = new Menu("Instellingen");
		configuration.getItems().addAll(editBasicElements, editSettings);
		
		Menu planning = new Menu("Planning");
		planning.getItems().addAll(planningOverview);
		
		menuBar.getMenus().addAll(recipePlanner, configuration, planning);
	}
	
	public void handleBackupDialog(ActionEvent actionEvent) {
		createBackupDialog.createBackup();
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
	
	public void handleSettingsDialog(ActionEvent actionEvent) {
		settingsDialog.showSettingsDialog();
	}
	
	public void showRecipeSingleViewPanel(Recipe recipe) {
		rootWindow.setCenter(recipeSingleView.getRecipeSingleViewPanel(recipe));
	}
	
	public void showRecipeEditViewPanel(Recipe recipe) {
		rootWindow.setCenter(recipeEditView.getRecipeEditViewPanel(recipe));
	}
	
	public void showNewRecipeEditViewPanel() {
		rootWindow.setCenter(recipeEditView.getNewRecipeEditViewPanel());
	}
	
	public void handlePlanningPanel(ActionEvent actionEvent) {
		rootWindow.setCenter(planningView.getPlanningPanel());
	}
}
