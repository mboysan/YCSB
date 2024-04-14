#!/bin/bash

cd ./consensus-latest

: ${1?"NodeId must be provided"}
: ${2?"Protocol must be provided"}

nodeId=$1
protocol=$2
nodePort="3333$nodeId"
storePort="3334$nodeId"

java -Dname=node$nodeId -cp consensus.jar com.mboysan.consensus.KVStoreServerCLI \
  --store port=$storePort \
  --node node.id=$nodeId port=$nodePort destinations=0-localhost:33330,1-localhost:33331,2-localhost:33332 \
  protocol=$protocol
