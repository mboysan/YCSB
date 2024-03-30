package site.ycsb.db;

/**
 * Destination definition for Consul cluster members.
 */
public class ConsulDestination {
  private final String id;
  private final String address;
  private final int port;

  public ConsulDestination(String id, String address, int port) {
    this.id = id;
    this.address = address;
    this.port = port;
  }

  public String getId() {
    return id;
  }

  public String getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return "EtcdDestination{" +
        "id='" + id + '\'' +
        ", address=" + address +
        ", port=" + port +
        '}';
  }
}
