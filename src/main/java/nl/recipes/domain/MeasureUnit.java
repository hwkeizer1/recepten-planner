package nl.recipes.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class MeasureUnit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(unique = true)
  private String name;

  private String pluralName;

  protected MeasureUnit() {}

  private MeasureUnit(MeasureUnitBuilder builder) {
    this.name = builder.name;
    this.pluralName = builder.pluralName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPluralName() {
    return pluralName;
  }

  public void setPluralName(String pluralName) {
    this.pluralName = pluralName;
  }

  public static class MeasureUnitBuilder {
    private String name;
    private String pluralName;

    public MeasureUnitBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public MeasureUnitBuilder withPluralName(String pluralName) {
      this.pluralName = pluralName;
      return this;
    }

    public MeasureUnit build() {
      MeasureUnit measureUnit = new MeasureUnit(this);
      return measureUnit;
    }

    public MeasureUnit build(Long id) {
      MeasureUnit measureUnit = new MeasureUnit(this);
      measureUnit.setId(id);
      return measureUnit;
    }
  }

  @Override
  public String toString() {
    return "[" + id + ", " + name + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MeasureUnit other = (MeasureUnit) obj;
    return Objects.equals(id, other.id);
  }
}
