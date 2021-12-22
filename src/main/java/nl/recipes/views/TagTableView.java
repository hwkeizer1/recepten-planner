package nl.recipes.views;

import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import nl.recipes.domain.Tag;
import nl.recipes.services.TagService;

@Component
public class TagTableView {

	private final TagService tagService;
	
	private TableView<Tag> tableView;

	public TagTableView(TagService tagService) {
		this.tagService = tagService;
		tableView = new TableView<>();
		tableView.setItems(this.tagService.getReadonlyTagList());
		setupTableView();
	}
	
	public TableView<Tag> getTableView() {
		return tableView;
	}
	
	private void setupTableView() {
		TableColumn<Tag, String> colName = new TableColumn<>("Categorie");
		colName.setCellValueFactory(v -> new ReadOnlyObjectWrapper<>(v.getValue().getName()));
		tableView.getColumns().add(colName);
	}
}
