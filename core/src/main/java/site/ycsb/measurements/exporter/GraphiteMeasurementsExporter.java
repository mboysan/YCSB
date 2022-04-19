package site.ycsb.measurements.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Write measurements in the format expected by graphite.
 */
public class GraphiteMeasurementsExporter implements MeasurementsExporter {

  private static final String METRICS_PREFIX_PROPERTY = "metricsprefix";
  private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
  private static final String DASH = "-";

  private final String metricsPrefix;
  private final PrintWriter writer;

  public GraphiteMeasurementsExporter(OutputStream out, Properties props) {
    this.metricsPrefix = props.getProperty(METRICS_PREFIX_PROPERTY);
    this.writer = new PrintWriter(out);
  }

  @Override
  public void write(String metric, String measurement, int i) throws IOException {
    this.write(metric, measurement, i + "");
  }

  @Override
  public void write(String metric, String measurement, long i) throws IOException {
    this.write(metric, measurement, i + "");
  }

  @Override
  public void write(String metric, String measurement, double d) throws IOException {
    this.write(metric, measurement, d + "");
  }

  private void write(String metric, String measurement, String value) {
    if (isTimeSeriesMeasurement(measurement)) {
      // measurement is time
      writer.println(prefix(metricsPrefix, sanitize(metric)) + " " + sanitize(value) + " " + measurement);
    } else {
      writer.println(prefix(metricsPrefix, sanitize(metric + " " + measurement)) + " " + value + " "
          + System.currentTimeMillis());
    }
  }

  @Override
  public void close() throws IOException {
    writer.close();
  }

  private static boolean isTimeSeriesMeasurement(String measurement) {
    try {
      Long.parseLong(measurement);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Trims the string and replaces all whitespace characters with the provided symbol.
   * Taken from com.codahale.metrics.graphite
   */
  private static String sanitize(String string) {
    return WHITESPACE.matcher(string.trim()).replaceAll(DASH).toLowerCase();
  }

  private static String prefix(String prefix, String string) {
    if (prefix == null || prefix.trim().length() == 0) {
      return string;
    }
    return prefix + "." + string;
  }
}
