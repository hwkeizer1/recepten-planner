package nl.recipes.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.repositories.TagRepository;

@Service
public class TagService {

	private final TagRepository tagRepository;
	
	private ObservableList<Tag> observableTagList;

	public TagService(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
		observableTagList = FXCollections.observableList(tagRepository.findAll());
	}

	public ObservableList<Tag> getReadonlyTagList() {
		return FXCollections.unmodifiableObservableList(observableTagList);
	}
	
	public Tag create(Tag tag) throws AlreadyExistsException {
		if (findByName(tag.getName()).isPresent()) {
			throw new AlreadyExistsException("Categorie " + tag.getName() + " bestaat al");
		}
		Tag createdTag = tagRepository.save(tag);
		observableTagList.add(createdTag);
		return createdTag;
	}
	
	public Optional<Tag> findByName(String name) {
		return observableTagList.stream()
				.filter(tag -> name.equals(tag.getName()))
				.findAny();
	}
	
	public void addListener(ListChangeListener<Tag> listener) {
		observableTagList.addListener(listener);
	}
	
	public void removeChangeListener(ListChangeListener<Tag> listener) {
		observableTagList.removeListener(listener);
	}
	
	// Setter for JUnit testing only
	void setObservableTagList(ObservableList<Tag> observableTagList) {
		this.observableTagList = observableTagList;
	}
	
}
