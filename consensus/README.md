//TODO: will be revised

compile:
```
mvn -pl site.ycsb:consensus-binding -am clean package -DskipTests
```

run:
```
./bin/ycsb.sh run consensus -s -P consensus/conf/consensus.properties -P workloads/workloada
```
