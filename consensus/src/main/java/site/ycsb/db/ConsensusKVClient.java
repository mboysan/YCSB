package site.ycsb.db;

import com.mboysan.consensus.KVOperationException;
import com.mboysan.consensus.KVStoreClient;
import com.mboysan.consensus.configuration.Configuration;
import com.mboysan.consensus.configuration.TcpTransportConfig;
import com.mboysan.consensus.vanilla.VanillaTcpClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.DBException;
import site.ycsb.Status;
import site.ycsb.StringByteIterator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * <a href="https://github.com/mboysan/consensus">consensus</a> binding for YCSB.
 */
public class ConsensusKVClient extends DB {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsensusKVClient.class);

  private KVStoreClient kvStoreClient;

  @Override
  public void init() throws DBException {
    TcpTransportConfig transportConfig = Configuration.newInstance(TcpTransportConfig.class, getProperties());
    LOGGER.info("loaded conf={}", transportConfig);
    VanillaTcpClientTransport transport = new VanillaTcpClientTransport(transportConfig);
    kvStoreClient = new KVStoreClient(transport);
    try {
      kvStoreClient.start();
    } catch (IOException e) {
      throw new DBException(e);
    }
  }

  @Override
  public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
    try {
      if (fields == null) {
        String value = kvStoreClient.get(key);
        result.put(key, new StringByteIterator(value));
      } else {
        for (String field : fields) {
          String value = kvStoreClient.get(field);
          result.put(field, new StringByteIterator(value));
        }
      }
      return Status.OK;
    } catch (KVOperationException e) {
      LOGGER.error("[read] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status scan(String table, String startkey, int recordcount, Set<String> fields,
                     Vector<HashMap<String, ByteIterator>> result) {
    try {
      if (fields == null || fields.isEmpty()) {
        Set<String> keys = kvStoreClient.iterateKeys();
        HashMap<String, ByteIterator> cur = new HashMap<>();
        for (String key : keys) {
          String value = kvStoreClient.get(key);
          cur.put(key, new StringByteIterator(value));
        }
        result.add(cur);
        return Status.OK;
      } else {
        return Status.NOT_IMPLEMENTED;
      }
    } catch (KVOperationException e) {
      LOGGER.error("[scan] error at table={}, startkey={}, recordcount={}, err={}",
          table, startkey, recordcount, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status update(String table, String key, Map<String, ByteIterator> values) {
    try {
      for (String keyToUpdate : values.keySet()) {
        kvStoreClient.set(keyToUpdate, values.get(keyToUpdate).toString());
      }
      return Status.OK;
    } catch (KVOperationException e) {
      LOGGER.error("[update] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status insert(String table, String key, Map<String, ByteIterator> values) {
    try {
      for (String keyToUpdate : values.keySet()) {
        kvStoreClient.set(keyToUpdate, values.get(keyToUpdate).toString());
      }
      return Status.OK;
    } catch (KVOperationException e) {
      LOGGER.error("[insert] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

  @Override
  public Status delete(String table, String key) {
    try {
      kvStoreClient.delete(key);
      return Status.OK;
    } catch (KVOperationException e) {
      LOGGER.error("[delete] error at table={}, key={}, err={}", table, key, e.getMessage(), e);
      return Status.ERROR;
    }
  }

}
