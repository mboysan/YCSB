package site.ycsb.db;

import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.map.DistributedMap;
import io.atomix.core.map.DistributedMapBuilder;
import io.atomix.utils.net.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.DBException;
import site.ycsb.Status;
import site.ycsb.StringByteIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

/**
 * <a href="https://atomix.io/">atomix</a> binding for YCSB.
 */
public class AtomixKVClient extends DB {

  private static final Logger LOGGER = LoggerFactory.getLogger(AtomixKVClient.class);

  private Atomix atomix;
  private DistributedMap<String, String> kvStore;

  @Override
  public void init() throws DBException {
    Properties properties = getProperties();
    LOGGER.info("properties={}", properties);
    String clientId = properties.getProperty("client.id", "client1");
    String clientAddress = properties.getProperty("client.address");
    String clusterMembers = properties.getProperty("cluster.members");

    List<AtomixDestination> destinations = convertPropsToDestinationsList(clusterMembers);
    Node[] nodes = new Node[destinations.size()];
    for (int i = 0; i < destinations.size(); i++) {
      nodes[i] = Node.builder()
          .withId(destinations.get(i).getId())
          .withAddress(destinations.get(i).getAddress())
          .build();
    }

    atomix = Atomix.builder()
        .withMemberId(clientId)
        .withAddress(Address.from(clientAddress))
        .withMembershipProvider(BootstrapDiscoveryProvider.builder()
            .withNodes(nodes)
            .build())
        .build();
    atomix.start().join();

    String mapName = properties.getProperty("map.name", "somemap");
    boolean cacheEnabled = Boolean.parseBoolean(properties.getProperty("map.cacheEnabled", "false"));
    DistributedMapBuilder<String, String> mapBuilder = atomix.mapBuilder(mapName);
    if(cacheEnabled) {
      mapBuilder.withCacheEnabled();
    }
    this.kvStore = mapBuilder.build();
  }

  @Override
  public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
    try {
      if (fields == null) {
        String value = kvStore.get(key);
        result.put(key, new StringByteIterator(value));
      } else {
        for (String field : fields) {
          String value = kvStore.get(field);
          result.put(field, new StringByteIterator(value));
        }
      }
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[read] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status scan(String table, String startkey, int recordcount, Set<String> fields,
                     Vector<HashMap<String, ByteIterator>> result) {
    try {
      if (fields == null || fields.isEmpty()) {
        Set<String> keys = kvStore.keySet();
        HashMap<String, ByteIterator> cur = new HashMap<>();
        for (String key : keys) {
          String value = kvStore.get(key);
          cur.put(key, new StringByteIterator(value));
        }
        result.add(cur);
        return Status.OK;
      } else {
        return Status.NOT_IMPLEMENTED;
      }
    } catch (Exception e) {
      LOGGER.error("[scan] error at table={}, startkey={}, recordcount={}, err={}",
          table, startkey, recordcount, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status update(String table, String key, Map<String, ByteIterator> values) {
    try {
      for (String keyToUpdate : values.keySet()) {
        kvStore.put(keyToUpdate, values.get(keyToUpdate).toString());
      }
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[update] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status insert(String table, String key, Map<String, ByteIterator> values) {
    try {
      for (String keyToUpdate : values.keySet()) {
        kvStore.put(keyToUpdate, values.get(keyToUpdate).toString());
      }
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[insert] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status delete(String table, String key) {
    try {
      kvStore.remove(key);
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[delete] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  public static List<AtomixDestination> convertPropsToDestinationsList(String destinationProps) {
    Objects.requireNonNull(destinationProps);
    List<AtomixDestination> destinations = new ArrayList<>();
    destinationProps = destinationProps.replaceAll("\\s+", "");    // remove whitespace
    String[] dests = destinationProps.split(",");
    for (String dest : dests) {
      String[] idIp = dest.split("-");
      String id = idIp[0];
      String address = idIp[1];
      destinations.add(new AtomixDestination(id, Address.from(address)));
    }
    return destinations;
  }

}
