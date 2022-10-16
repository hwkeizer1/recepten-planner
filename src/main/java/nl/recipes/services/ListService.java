package nl.recipes.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@Component
public class ListService<T> {

  protected JpaRepository<T, Long> repository;
  protected ObservableList<T> observableList;
  protected Comparator<T> comparator;
  
  public ObservableList<T> getList() {
    return FXCollections.unmodifiableObservableList(observableList);
  }
  
  public List<T> getBackupList() {
    return Collections.unmodifiableList(repository.findAll());
  }
  
  public void addListener(ListChangeListener<T> listener) {
    observableList.addListener(listener);
  }

  public void removeChangeListener(ListChangeListener<T> listener) {
    observableList.removeListener(listener);
  }
  
  T save(T o) {
    T createdObject = repository.save(o);
    observableList.add(createdObject);
    FXCollections.sort(observableList, comparator);
    return createdObject;
  }
  
  T update(T o) {
    T update = repository.save(o);
    observableList.set(observableList.lastIndexOf(o), update);
    FXCollections.sort(observableList, comparator);
    return update;
  }
  
  void delete(T o) {
    repository.delete(o);
    observableList.remove(o);
  }
}
