package site.ycsb.db;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.ycsb.ByteIterator;
import site.ycsb.Status;
import site.ycsb.StringByteIterator;
import site.ycsb.measurements.Measurements;
import site.ycsb.workloads.CoreWorkload;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;

public class EtcdKVClientIntegrationTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(EtcdKVClient.class);

  private static final String TABLE = "sometable";

  private EtcdKVClient client;

  @BeforeClass
  public static void beforeClass() throws Exception {
    LOGGER.info("simplelogger test");
  }

  @Before
  public void setUp() throws Exception {
    client = new EtcdKVClient();

    Properties properties = new Properties();
    properties.put("cluster.members", "infra0=http://localhost:9990,infra1=http://localhost:9991,infra2=http://localhost:9992");

    Measurements.setProperties(properties);
    final CoreWorkload workload = new CoreWorkload();
    workload.init(properties);

    client.setProperties(properties);
    client.init();
  }

  @Test
  public void testEtcdClient() {
    final String testKey = "someKey";

    // insert
    Map<String, String> expectedValues = new HashMap<>();
    String field1 = "field_1";
    String value1 = "value_1";
    expectedValues.put(field1, value1);
    Map<String, ByteIterator> result = StringByteIterator.getByteIteratorMap(expectedValues);
    client.insert(TABLE, testKey, result);

    // read
    result.clear();
    Status status = client.read(TABLE, testKey, null, result);
    assertEquals(Status.OK, status);
    assertEquals(1, result.size());
    assertEquals(expectedValues.toString(), result.get(testKey).toString());

    // update(the same field)
    expectedValues.clear();
    result.clear();
    String newVal = "value_new";
    expectedValues.put(field1, newVal);
    result = StringByteIterator.getByteIteratorMap(expectedValues);
    client.update(TABLE, testKey, result);
    assertEquals(1, result.size());

    // Verify result
    result.clear();
    status = client.read(TABLE, testKey, null, result);
    assertEquals(Status.OK, status);
    // here we only have one field: field_1
    assertEquals(1, result.size());
    assertEquals(expectedValues.toString(), result.get(testKey).toString());

    // delete
    status = client.delete(TABLE, testKey);
    assertEquals(Status.OK, status);

    // Verify result
    result.clear();
    status = client.read(TABLE, testKey, null, result);
    // NoNode return ERROR
    assertEquals(Status.OK, status);
    assertNull(result.get(testKey).toString());
  }
}