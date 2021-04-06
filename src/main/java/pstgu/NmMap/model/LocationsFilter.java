package pstgu.NmMap.model;

public class LocationsFilter {

  private String[] clusterTypes;

  public LocationsFilter(String... clusterTypes) {
    this.clusterTypes = clusterTypes;
  }

  public String[] getClusterTypes() {
    return clusterTypes;
  }
}
