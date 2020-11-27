package pstgu.NmMap.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"N", "E", "dating", "description"})
@JsonInclude(Include.NON_NULL)
public class Location {
  private double N;
  private double E;

  private String dating;
  
  private LocalDate startDate;
  private LocalDate endDate;
  private int countDays;
  
  private String description;

  // поля для описания меток на карте
  private Human human;

  public Location() {}

  public Location(double n, double e, String dating, String description) {
    N = n;
    E = e;
    this.dating = dating;
    this.description = description;
  }

  public Location(Human human, double n, double e, String dating, String description) {
    this(n, e, dating, description);
    this.human = human;
  }

  @Override
  public String toString() {
    return String.format("{N%f° E%f° - %s %s}", N, E, dating, description);
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

  public String getDating() {
    return dating;
  }

  public void setDating(String dating) {
    this.dating = dating;
  }
  
  public String getDescription() {
    return description;
  }


    
  
  
  
  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public int getCountDays() {
    return countDays;
  }

  public void setCountDays(int countDays) {
    this.countDays = countDays;
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
    return human != null ? human.getTitle() : null;
  }

}
