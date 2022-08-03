package nl.recipes.exceptions;

public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1891348049408884201L;

  public NotFoundException(String message) {
    super(message);
  }

}
