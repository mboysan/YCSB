package site.ycsb.db;

import io.atomix.utils.net.Address;

/**
 * Destination definition for Atomix cluster members.
 */
public class AtomixDestination {
  private final String id;
  private final Address address;

  public AtomixDestination(String id, Address address) {
    this.id = id;
    this.address = address;
  }

  public String getId() {
    return id;
  }

  public Address getAddress() {
    return address;
  }

  @Override
  public String toString() {
    return "AtomixDestination{" +
        "id='" + id + '\'' +
        ", address=" + address +
        '}';
  }
}
