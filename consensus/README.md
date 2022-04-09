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

- Run mvn with release profile:
```
mvn -pl site.ycsb:ycsb -am clean package -DskipTests -P ycsb-release
```
- Binaries will be located in `distribution/target/ycsb-<version>.tar.gz`.
```
cd distribution/target
```
- Unpack the tar.gz file
```
mkdir ycsb
tar xvf *.tar.gz --strip 1 -C ycsb
```
- Change dir
```
cd ycsb
```
- Copy the application properties file for your database bindings to the root of the unpacked dir.
```
# Our application.properties will be copied to conf:
cp -R ../../../consensus/conf .
```
- make the `ycsb.sh` script executable:
```
chmod +x bin/ycsb.sh
```
- Finally, run the script:
```
export JAVA_OPTS=-Dlog4j.configuration=file:<path-to-log4j-configuration>
./bin/ycsb.sh run consensus -s -P conf/application.properties -P workloads/workloada
```