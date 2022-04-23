# About

## Compilation and Testing Locally
- Compile:
```
mvn -pl site.ycsb:consensus-binding -am clean package -DskipTests
```
- Execute a workload:
```
./bin/ycsb.sh run consensus -s -P consensus/conf/application.properties -P workloads/workloada
```

---

## Release and execute

Run mvn with release profile:
```
mvn -pl site.ycsb:ycsb -am clean package -DskipTests -P ycsb-release
```
Binaries will be located in `distribution/target/ycsb-<version>.tar.gz`.
```
cd distribution/target
```
Unpack the tar.gz file
```
mkdir ycsb
tar xvf *.tar.gz --strip 1 -C ycsb
```
Change dir
```
cd ycsb
```
Copy the application properties file for your database bindings to the root of the unpacked dir.
```
# Our application.properties will be copied to conf:
cp -R ../../../consensus/conf .
```
Make the `ycsb.sh` script executable:
```
chmod +x bin/ycsb.sh
```
Finally, run the script:
```
export JAVA_OPTS=-Dlog4j.configuration=file:<path-to-log4j-configuration>
./bin/ycsb.sh run consensus -s -P conf/application.properties -P workloads/workloada
```

To export the measurements as csv, `measurementtype=raw` can be used.
```
./bin/ycsb.sh run consensus -s -p measurementtype=raw -P consensus/conf/application.properties -P workloads/workloada
```

Alternatively, measurements can be exported as graphite compatible values:
```
./bin/ycsb.sh run consensus -s -p measurementtype=raw -p measurement.raw.graphite=true -P consensus/conf/application.properties -P workloads/workloada
```

Following are all the additional optional properties to include if `measurementtype=raw` has been specified:
- `measurement.raw.output_file`: Output file to write the raw output measurements
(can be same or different than the `exportfile` property).
- `measurement.raw.prefix`: prefix to be added to the measurement.
- `measurement.raw.graphite`: (true\false), if set to true, export the metric as graphite supported format.

**Note:** [consensus-infra](https://github.com/mboysan/consensus-infra) uses the following execution format:
```
./bin/ycsb.sh run consensus -s -p measurementtype=raw -p measurement.raw.output_file=metrics.txt -p exportfile=metrics.txt -P consensus/conf/application.properties -P workloads/workloada
```
