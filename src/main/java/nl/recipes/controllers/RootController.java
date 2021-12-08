package nl.recipes.controllers;

import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import net.rgielen.fxweaver.core.FxmlView;

@Controller
@FxmlView("root.fxml")
public class RootController {

	@FXML
	private BorderPane rootWindow;
}
