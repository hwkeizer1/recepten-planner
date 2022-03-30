package nl.recipes.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.TagRepository;

@Service
public class TagService {

	private final TagRepository tagRepository;
	
	private ObservableList<Tag> observableTagList;

	public TagService(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
		observableTagList = FXCollections.observableList(tagRepository.findByOrderByNameAsc());
	}

	public ObservableList<Tag> getReadonlyTagList() {
		return FXCollections.unmodifiableObservableList(observableTagList);
	}
	
	public Tag create(Tag tag) throws AlreadyExistsException, IllegalValueException {
		if (tag == null || tag.getName().isEmpty()) {
			throw new IllegalValueException("Categorie naam mag niet leeg zijn");
		}
		if (findByName(tag.getName()).isPresent()) {
			throw new AlreadyExistsException("Categorie " + tag.getName() + " bestaat al");
		}
		Tag createdTag = tagRepository.save(tag);
		observableTagList.add(createdTag);
		return createdTag;
	}
	
	public Tag update(Tag tag, String name) throws NotFoundException, AlreadyExistsException {
		if (!findById(tag.getId()).isPresent()) {
			throw new NotFoundException("Categorie " + tag.getName() + " niet gevonden");
		}
		if (findByName(name).isPresent()) {
			throw new AlreadyExistsException("Categorie " + name + " bestaat al");
		}
		tag.setName(name);
		Tag updatedTag = tagRepository.save(tag);
		observableTagList.set(observableTagList.lastIndexOf(tag), updatedTag);
		return updatedTag;
	}
	
	public void remove(Tag tag) throws NotFoundException {
		// TODO add check for removing tags that are in use
		if (!findById(tag.getId()).isPresent()) {
			throw new NotFoundException("Categorie " + tag.getName() + " niet gevonden");
		}
		tagRepository.delete(tag);
		observableTagList.remove(tag);
	}
	
	public Optional<Tag> findByName(String name) {
		return observableTagList.stream()
				.filter(tag -> name.equals(tag.getName()))
				.findAny();
	}
	
	public Optional<Tag> findById(Long id) {
		return observableTagList.stream()
				.filter(tag -> id.equals(tag.getId()))
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
