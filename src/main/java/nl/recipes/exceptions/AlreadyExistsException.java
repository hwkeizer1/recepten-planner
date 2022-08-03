package nl.recipes.exceptions;

public class AlreadyExistsException extends Exception {

  private static final long serialVersionUID = -2313004039891462516L;

  public AlreadyExistsException(String message) {
    super(message);
  }

}
