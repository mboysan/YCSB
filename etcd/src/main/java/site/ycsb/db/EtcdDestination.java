package site.ycsb.db;

/**
 * Destination definition for Etcd cluster members.
 */
public class EtcdDestination {
  private final String id;
  private final String address;

  public EtcdDestination(String id, String address) {
    this.id = id;
    this.address = address;
  }

  public String getId() {
    return id;
  }

  public String getAddress() {
    return address;
  }

  @Override
  public String toString() {
    return "EtcdDestination{" +
        "id='" + id + '\'' +
        ", address=" + address +
        '}';
  }
}
