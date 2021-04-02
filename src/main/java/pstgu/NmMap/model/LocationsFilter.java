package pstgu.NmMap.model;

public class LocationsFilter {
  public enum Type {
    ALL_POINTS, CLUSTER_TYPES
  }

  private Type type;
  private String request;

  public LocationsFilter(Type requestType, String request) {
    this.type = requestType;
    this.request = request;
  }

  public Type getType() {
    return type;
  }

  public String getRequest() {
    return request;
  }
}
