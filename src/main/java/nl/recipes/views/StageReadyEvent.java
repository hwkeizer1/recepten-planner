package nl.recipes.views;

import org.springframework.context.ApplicationEvent;

import javafx.stage.Stage;

public class StageReadyEvent extends ApplicationEvent {

  private static final long serialVersionUID = 8014079889396677445L;

  public final Stage stage;

  public StageReadyEvent(Stage stage) {

    super(stage);
    this.stage = stage;
  }

}
