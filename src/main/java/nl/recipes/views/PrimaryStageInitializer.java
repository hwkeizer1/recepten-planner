package nl.recipes.views;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.recipes.views.root.RootView;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

  private final RootView rootView;

  public PrimaryStageInitializer(RootView rootView) {
    this.rootView = rootView;
  }

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
    Stage stage = event.stage;
    Scene scene = new Scene(rootView.asParent());
    scene.getStylesheets().clear();
    scene.getStylesheets().addAll(getClass().getResource("/css/styles.css").toExternalForm(),
        getClass().getResource("/css/widget.css").toExternalForm(),
        getClass().getResource("/css/recipe-list-view.css").toExternalForm(),
        getClass().getResource("/css/single-recipe-view.css").toExternalForm(),
        getClass().getResource("/css/planning.css").toExternalForm());
    stage.setScene(scene);
    stage.setTitle(ViewConstants.PROGRAM_TITLE);
    stage.show();
    stage.setMaximized(true);
  }
}
