package site.ycsb.db;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
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
 * <a href="https://etcd.io/">etcd</a> binding for YCSB.
 */
public class EtcdKVClient extends DB {

  private static final Logger LOGGER = LoggerFactory.getLogger(EtcdKVClient.class);

  private KV kvStoreClient;

  @Override
  public void init() {
    Properties properties = getProperties();
    LOGGER.info("properties={}", properties);
    String clusterMembers = properties.getProperty("cluster.members");
    List<EtcdDestination> destinations = convertPropsToDestinationsList(clusterMembers);
    String[] endpoints = new String[destinations.size()];
    for (int i = 0; i < destinations.size(); i++) {
      endpoints[i] = destinations.get(i).getAddress();
    }

    Client client = Client.builder().endpoints(endpoints).build();
    this.kvStoreClient = client.getKVClient();
  }

  @Override
  public void cleanup() throws DBException {
    kvStoreClient.close();
    super.cleanup();
  }

  @Override
  public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
    try {
      String value = null;
      List<KeyValue> kvs = kvStoreClient.get(toBs(key)).get().getKvs();
      if (kvs != null && !kvs.isEmpty()) {
        value = toStr(kvs.get(0).getValue());
      }
      result.put(key, new StringByteIterator(value));
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[read] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status scan(String table, String startkey, int recordcount, Set<String> fields,
                     Vector<HashMap<String, ByteIterator>> result) {
    return Status.NOT_IMPLEMENTED;
  }

  @Override
  public Status update(String table, String key, Map<String, ByteIterator> values) {
    try {
      kvStoreClient.put(toBs(key), toBs(values.toString())).get();
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[update] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status insert(String table, String key, Map<String, ByteIterator> values) {
    try {
      kvStoreClient.put(toBs(key), toBs(values.toString())).get();
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[insert] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status delete(String table, String key) {
    try {
      kvStoreClient.delete(toBs(key)).get();
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[delete] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  private static ByteSequence toBs(String str) {
    return ByteSequence.from(str.getBytes());
  }

  private static String toStr(ByteSequence bs) {
    return bs.toString();
  }

  private static List<EtcdDestination> convertPropsToDestinationsList(String destinationProps) {
    Objects.requireNonNull(destinationProps);
    List<EtcdDestination> destinations = new ArrayList<>();
    destinationProps = destinationProps.replaceAll("\\s+", "");    // remove whitespace
    String[] dests = destinationProps.split(",");
    for (String dest : dests) {
      String[] idIp = dest.split("=");
      String id = idIp[0];
      String address = idIp[1];
      destinations.add(new EtcdDestination(id, address));
    }
    return destinations;
  }

}
