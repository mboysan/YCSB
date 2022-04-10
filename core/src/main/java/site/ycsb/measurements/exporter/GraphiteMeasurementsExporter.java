package site.ycsb.measurements.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 * Write measurements in the format expected by graphite.
 */
public class GraphiteMeasurementsExporter implements MeasurementsExporter {

  private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
  private static final String DASH = "-";

  private final PrintWriter writer;

  public GraphiteMeasurementsExporter(OutputStream out) {
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
      writer.println(sanitize(metric) + " " + sanitize(value) + " " + measurement);
    } else {
      writer.println(sanitize(metric + " " + measurement) + " " + value + " " + System.currentTimeMillis());
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
}
