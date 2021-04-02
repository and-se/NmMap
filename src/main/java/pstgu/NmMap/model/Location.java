package pstgu.NmMap.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"N", "E", "clusterType", "type", "dating", "description"})
@JsonInclude(Include.NON_NULL)
public class Location {
  private double N;
  private double E;

  private String clusterType;
  private String type;
  private String dating;
  private String description;

  // поля для описания меток на карте
  private Human human;

  private static final String DEFAULT_CLUSTER_TYPE = "другое";
  private static final Map<String, String> clusterTypes = new HashMap<>(25) {
    {
      put("арест", "репрессии");
      put("духовно-образовательная деятельность", "служение");
      put("заключение", "репрессии");
      put("канонизация", "служение");
      put("кончина", "обстоятельства кончины");
      put("кратковременный арест", "репрессии");
      put("награда", "служение");
      put("образование", "другое");
      put("осуждение", "репрессии");
      put("погребение", "обстоятельства кончины");
      put("постриг", "служение");
      put("предварительное заключение", "репрессии");
      put("приговор", "репрессии");
      put("приговор внесудебного органа", "репрессии");
      put("приговор судебного органа", "репрессии");
      put("проживание", "другое");
      put("работа в церкви", "служение");
      put("работа в церкви/монастыре", "служение");
      put("раскулачивание", "репрессии");
      put("расстрел", "обстоятельства кончины");
      put("рождение", "другое");
      put("рукоположение", "служение");
      put("служение", "служение");
      put("смерть", "обстоятельства кончины");
      put("ссылка/высылка", "репрессии");
    }
  };

  public Location() {}

  public Location(double n, double e, String dating, String description) {
    N = n;
    E = e;
    this.dating = dating;
    this.description = description;
  }

  public Location(double n, double e, String type, String dating, String description) {
    this(n, e, dating, description);
    this.type = type;
    this.clusterType = clusterTypes.getOrDefault(type, DEFAULT_CLUSTER_TYPE);
  }

  public Location(Human human, double n, double e, String type, String dating, String description) {
    this(n, e, type, dating, description);
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

  @JsonGetter("clusterType")
  public String getClusterType() {
    return clusterType;
  }

  public void setClusterType(String clusterType) {
    if (clusterTypes.values().contains(clusterType)) {
      this.clusterType = clusterType;
    } else {
      this.clusterType = DEFAULT_CLUSTER_TYPE;
    }
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
