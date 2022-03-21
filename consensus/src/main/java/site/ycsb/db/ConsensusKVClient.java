package site.ycsb.db;

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
    return Status.OK;
  }

  @Override
  public Status scan(String table, String startkey, int recordcount, Set<String> fields,
                     Vector<HashMap<String, ByteIterator>> result) {
    return Status.OK;
  }

  @Override
  public Status update(String table, String key, Map<String, ByteIterator> values) {
    return Status.OK;
  }

  @Override
  public Status insert(String table, String key, Map<String, ByteIterator> values) {
    return Status.OK;
  }

  @Override
  public Status delete(String table, String key) {
    return Status.OK;
  }
}
