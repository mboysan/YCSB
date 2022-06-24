package site.ycsb.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.ycsb.ByteArrayByteIterator;
import site.ycsb.ByteIterator;
import site.ycsb.DBException;

import java.util.HashMap;
import java.util.Properties;

public class AtomixKVClientTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AtomixKVClientTest.class);

  public static void main(String[] args) throws DBException {
    LOGGER.info("simplelogger test");
    AtomixKVClient client = new AtomixKVClient();

    Properties properties = new Properties();
    properties.put("client.address", "localhost:9999");
    properties.put("cluster.members", "member1-localhost:9991,member2-localhost:9992,member3-localhost:9993");
    client.setProperties(properties);

    client.init();
    test(client);
  }

  private static void test(AtomixKVClient client) {
    HashMap<String, ByteIterator> values = new HashMap<>();
    values.put("a", new ByteArrayByteIterator(new byte[]{1, 2, 3, 4}));
    client.insert("table", "key", values);
  }
}