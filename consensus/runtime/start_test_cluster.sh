#!/bin/bash

: ${1?"Protocol must be provided"}
PROTOCOL=$1

echo "starting node-0"
./node.sh 0 $PROTOCOL > /tmp/consensus_node0.txt 2>&1 &
echo "starting node-1"
./node.sh 1 $PROTOCOL > /tmp/consensus_node1.txt 2>&1 &
echo "starting node-2"
./node.sh 2 $PROTOCOL > /tmp/consensus_node2.txt 2>&1 &

echo "node outputs are located in /tmp/consensus_node<nodeId>.txt"
echo "cluster is up and running, type anything to stop the cluster..."

read -n 1 key

echo "cluster processes:"
ps -A | grep java

echo "stopping the cluster..."
pkill 'java'

sleep 10
echo "cluster processes after cleanup:"
ps -A | grep java
