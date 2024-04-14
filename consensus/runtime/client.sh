#!/bin/bash

cd ./consensus-latest

: ${1?"NodeId must be provided"}

nodeId=$1
java -cp consensus.jar com.mboysan.consensus.KVStoreClientCLI destinations=$nodeId-localhost:3334$nodeId
