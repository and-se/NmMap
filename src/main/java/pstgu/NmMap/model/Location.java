package pstgu.NmMap.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"N", "E", "description"})
public class Location {
  private double N;
  private double E;

  private String description;

  public Location() {}

  public Location(double n, double e, String description) {
    N = n;
    E = e;
    this.description = description;
  }

  @Override
  public String toString() {
    return String.format("{N%f° E%f° - %s}", N, E, description);
  }

  @JsonGetter("N")
  public double getN() {
    return N;
  }

  @JsonGetter("E")
  public double getE() {
    return E;
  }

  public String getDescription() {
    return description;
  }

  public void setN(double n) {
    N = n;
  }

  public void setE(double e) {
    E = e;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
