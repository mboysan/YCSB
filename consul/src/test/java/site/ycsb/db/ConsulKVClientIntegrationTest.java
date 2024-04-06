package site.ycsb.db;

import org.junit.Test;
import site.ycsb.ByteIterator;
import site.ycsb.Status;
import site.ycsb.StringByteIterator;
import site.ycsb.WorkloadException;
import site.ycsb.measurements.Measurements;
import site.ycsb.workloads.CoreWorkload;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertEquals;

public class ConsulKVClientIntegrationTest {

  private static final String TABLE = "sometable";

  private ConsulKVClient client;

  @Test
  public void testInitClientWithInvalidProperties() {
    Properties properties = new Properties();
    properties.put("raft.consistency", "undefined");
    client = new ConsulKVClient();
    client.setProperties(properties);
    try {
      client.init();
      fail();
    } catch (IllegalArgumentException ignore) {}
  }

  private void setUpClient() throws WorkloadException {
    client = new ConsulKVClient();

    Properties properties = new Properties();
    properties.put("cluster.members", "agent0=localhost:8500");

    Measurements.setProperties(properties);
    final CoreWorkload workload = new CoreWorkload();
    workload.init(properties);

    client.setProperties(properties);
    client.init();
  }

  @Test
  public void testConsulClient() throws WorkloadException {
    setUpClient();

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
