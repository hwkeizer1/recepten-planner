package nl.recipes.domain;

/**
 * Entries in this enumeration must not be changed Entries can be reordered Entries can be added
 * Entries can contain max 20 characters
 */
public enum IngredientType {

  OVERIG("Overig"), ZUIVEL("Zuivel en vlees"), GROENTE("Groente");

  private String displayName;

  private IngredientType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

}
