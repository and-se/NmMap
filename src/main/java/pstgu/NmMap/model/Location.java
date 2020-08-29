package pstgu.NmMap.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"N", "E", "description"})
@JsonInclude(Include.NON_NULL)
public class Location {
  private double N;
  private double E;

  private String description;

  // поля для описания меток на карте
  private Human human;

  public Location() {}

  public Location(double n, double e, String description) {
    N = n;
    E = e;
    this.description = description;
  }

  public Location(Human human, double n, double e, String description) {
    this(n, e, description);
    this.human = human;
  }

  @Override
  public String toString() {
    return String.format("{N%f° E%f° - %s}", N, E, description);
  }

  @JsonGetter("N")
  public double getN() {
    return N;
  }

  public void setN(double n) {
    N = n;
  }

  @JsonGetter("E")
  public double getE() {
    return E;
  }

  public void setE(double e) {
    E = e;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setHuman(final Human human) {
    if (this.human == null)
      this.human = human;
  }

  public Integer getHumanId() {
    return human != null ? human.getId() : null;
  }

  public String getHumanFio() {
    return human != null ? human.getFio() : null;
  }

}
