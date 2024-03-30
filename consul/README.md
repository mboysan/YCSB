# About

## Running a Test etcd Cluster Locally
See the following commands to run a test etcd cluster locally:
```
cd ./consul/runtime

# to download the latest consul release
./download_consul.sh

# to start the consul cluster
./start_test_cluster.sh
```
After executing the commands above, you'll have a 3-node consul cluster running on your local machine.

## Compilation and Testing Locally
- Compile:
```
mvn -pl site.ycsb:consul-binding -am clean package -DskipTests
```
- Execute a workload:
```
./bin/ycsb.sh run consul -s -P consul/conf/application.properties -P workloads/workloada
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
cp -R ../../../consul/conf .
```
Make the `ycsb.sh` script executable:
```
chmod +x bin/ycsb.sh
```
Finally, run the script:
```
export JAVA_OPTS=-Dlog4j.configuration=file:<path-to-log4j-configuration>
./bin/ycsb.sh run consul -s -P conf/application.properties -P workloads/workloada
```

To export the measurements as csv, `measurementtype=raw` can be used.
```
./bin/ycsb.sh run consul -s -p measurementtype=raw -P conf/application.properties -P workloads/workloada
```

Alternatively, measurements can be exported as graphite compatible values:
```
./bin/ycsb.sh run consul -s -p measurementtype=raw -p measurement.raw.graphite=true -P conf/application.properties -P workloads/workloada
```

Following are all the additional optional properties to include if `measurementtype=raw` has been specified:
- `measurement.raw.output_file`: Output file to write the raw output measurements
(can be same or different than the `exportfile` property).
- `measurement.raw.prefix`: prefix to be added to the measurement.
- `measurement.raw.graphite`: (true\false), if set to true, export the metric as graphite supported format.
- `measurement.raw.separator`: set a separator for measurements. If `measurement.raw.graphite` set to true blank space
is used by default, otherwise, the default value is comma to support csv output.

**Note:** [consensus-infra](https://github.com/mboysan/consensus-infra) uses the following execution format:
```
./bin/ycsb.sh run consul -s -p measurementtype=raw -p measurement.raw.output_file=metrics.txt -p measurement.raw.separator=, -p exportfile=metrics.txt -P conf/application.properties -P workloads/workloada
```
