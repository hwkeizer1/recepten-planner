package nl.recipes.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.TagRepository;

@Service
public class TagService extends ListService<Tag> {

  public TagService(TagRepository tagRepository) {
    repository = tagRepository;
    observableList = FXCollections.observableList(repository.findAll());
    comparator = (t1, t2)-> t1.getName().compareTo(t2.getName());
  }
  
  public Optional<Tag> findByName(String name) {
    return observableList.stream().filter(tag -> name.equals(tag.getName())).findAny();
  }

  public Optional<Tag> findById(Long id) {
    return observableList.stream().filter(tag -> id.equals(tag.getId())).findAny();
  }
  
  public Tag create(Tag tag) throws AlreadyExistsException, IllegalValueException {
    if (tag == null || tag.getName().isEmpty()) {
      throw new IllegalValueException("Categorie naam mag niet leeg zijn");
    }
    if (findByName(tag.getName()).isPresent()) {
      throw new AlreadyExistsException("Categorie " + tag.getName() + " bestaat al");
    }
    return save(tag);
  }

  public Tag edit(Tag tag, String name) throws NotFoundException, AlreadyExistsException {
    if (!findById(tag.getId()).isPresent()) {
      throw new NotFoundException("Categorie " + tag.getName() + " niet gevonden");
    }
    if (findByName(name).isPresent()) {
      throw new AlreadyExistsException("Categorie " + name + " bestaat al");
    }
    tag.setName(name);
    Tag updatedTag = update(tag);
    return updatedTag;
  }

  public void remove(Tag tag) throws NotFoundException {
    if (!findById(tag.getId()).isPresent()) {
      throw new NotFoundException("Categorie " + tag.getName() + " niet gevonden");
    }
    delete(tag);
  }
  
  /**
   * Setter for JUnit testing only!
   * @param observableList
   */
  void setObservableList(ObservableList<Tag> observableList) {
    this.observableList = observableList;
  }
}
