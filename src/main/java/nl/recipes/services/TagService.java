package nl.recipes.services;

import static nl.recipes.views.ViewMessages.TAG_;
import static nl.recipes.views.ViewMessages.TAG_NAME_CANNOT_BE_EMPTY;
import static nl.recipes.views.ViewMessages._ALREADY_EXISTS;
import static nl.recipes.views.ViewMessages._NOT_FOUND;
import java.util.Optional;
import org.springframework.data.domain.Sort;
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
    Sort sort = Sort.by("name").ascending();
    observableList = FXCollections.observableList(repository.findAll(sort));
    comparator = (t1, t2) -> t1.getName().compareTo(t2.getName());
  }

  public Optional<Tag> findByName(String name) {
    return observableList.stream().filter(tag -> name.equals(tag.getName())).findAny();
  }

  public Optional<Tag> findById(Long id) {
    return observableList.stream().filter(tag -> id.equals(tag.getId())).findAny();
  }

  public Tag create(Tag tag) throws AlreadyExistsException, IllegalValueException {
    if (tag == null || tag.getName().isEmpty()) {
      throw new IllegalValueException(TAG_NAME_CANNOT_BE_EMPTY);
    }
    if (findByName(tag.getName()).isPresent()) {
      throw new AlreadyExistsException(TAG_ + tag.getName() + _ALREADY_EXISTS);
    }
    return save(tag);
  }

  public Tag edit(Tag tag, String name) throws NotFoundException, AlreadyExistsException {
    if (!findById(tag.getId()).isPresent()) {
      throw new NotFoundException(TAG_ + tag.getName() + _NOT_FOUND);
    }
    if (findByName(name).isPresent()) {
      throw new AlreadyExistsException(TAG_ + name + _ALREADY_EXISTS);
    }

    tag.setName(name);
    return update(tag);
  }

  public void remove(Tag tag) throws NotFoundException {
    if (!findById(tag.getId()).isPresent()) {
      throw new NotFoundException(TAG_ + tag.getName() + _NOT_FOUND);
    }
    delete(tag);
  }

  /**
   * Setter for JUnit testing only!
   * 
   * @param observableList
   */
  void setObservableList(ObservableList<Tag> observableList) {
    this.observableList = observableList;
  }
}
