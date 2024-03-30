#!/bin/bash

cd ./consul-latest

nodeId=$1
nodeName="agent$nodeId"
configDir="config/$nodeName"
consulHost=localhost
consulPortGrpc="820$nodeId"
consulPortSerfLan="830$nodeId"
consulPortServer="840$nodeId"
consulPortHttp="850$nodeId"

read -r -d '' agentConfig << EOM
{
  "bootstrap_expect": 3,
  "client_addr": "0.0.0.0",
  "data_dir": "$configDir",
  "datacenter": "dc1",
  "node_name": "$nodeName",
  "leave_on_terminate": true,
  "log_level": "INFO",
  "rejoin_after_leave": true,
  "server": true,
  "ports": {
    "dns": -1,
    "grpc_tls": -1,
    "https": -1,
    "serf_wan": -1,
    "sidecar_min_port": 0,
    "sidecar_max_port": 0,
    "expose_min_port": 0,
    "expose_max_port": 0,
    "grpc": $consulPortGrpc,
    "serf_lan": $consulPortSerfLan,
    "server": $consulPortServer,
    "http": $consulPortHttp
  },
  "retry_join": [
      "localhost:8300",
      "localhost:8301",
      "localhost:8302"
  ]
}
EOM
rm -r $configDir
mkdir -p $configDir
echo -e "$agentConfig" > $configDir/config.json

./consul agent -config-dir=$configDir
