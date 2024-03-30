package site.ycsb.db;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
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
 * <a href="https://developer.hashicorp.com/consul/">consul</a> binding for YCSB.
 */
public class ConsulKVClient extends DB {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsulKVClient.class);

  private ConsulClient kvStoreClient;

  @Override
  public void init() {
    Properties properties = getProperties();
    LOGGER.info("properties={}", properties);
    String clusterMembers = properties.getProperty("cluster.members");
    List<ConsulDestination> destinations = convertPropsToDestinationsList(clusterMembers);
    if (destinations.size() > 1) {
      throw new IllegalArgumentException("Multiple destinations are not supported");
    }
    ConsulDestination destination = destinations.get(0);
    this.kvStoreClient = new ConsulClient(destination.getAddress(), destination.getPort());
  }

  @Override
  public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
    try {
      LOGGER.debug("read record [k={}]", key);
      Response<GetValue> keyValueResponse = kvStoreClient.getKVValue(key);
      GetValue getValue = keyValueResponse.getValue();
      String value = null;
      if (getValue != null) {
        value = getValue.getDecodedValue();
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
      String valuesStr = values.toString();
      LOGGER.debug("update record [k={}] [v={}]", key, valuesStr);
      kvStoreClient.setKVValue(key, valuesStr);
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[update] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status insert(String table, String key, Map<String, ByteIterator> values) {
    try {
      String valuesStr = values.toString();
      LOGGER.debug("insert record [k={}] [v={}]", key, valuesStr);
      kvStoreClient.setKVValue(key, valuesStr);
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[insert] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status delete(String table, String key) {
    try {
      LOGGER.debug("delete record [k={}]", key);
      kvStoreClient.deleteKVValue(key);
      return Status.OK;
    } catch (Exception e) {
      LOGGER.error("[delete] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  private static List<ConsulDestination> convertPropsToDestinationsList(String destinationProps) {
    Objects.requireNonNull(destinationProps);
    List<ConsulDestination> destinations = new ArrayList<>();
    destinationProps = destinationProps.replaceAll("\\s+", "");    // remove whitespace
    String[] dests = destinationProps.split(",");
    for (String dest : dests) {
      String[] idIp = dest.split("=");
      String id = idIp[0];
      String addrPort = idIp[1];
      String[] addrPortArray = addrPort.split(":");
      String address = addrPortArray[0];
      int port = Integer.parseInt(addrPortArray[1]);
      destinations.add(new ConsulDestination(id, address, port));
    }
    return destinations;
  }

}
