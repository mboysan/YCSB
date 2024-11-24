#!/bin/bash

: ${1?"Protocol must be provided"}
PROTOCOL=$1

TEST_GROUP_AND_TEST_NAME="${2:-"W0.T1"}"

echo "removing previous logs and metrics..."
rm /tmp/*.txt
rm /tmp/*.csv

echo "starting node-0"
./node.sh 0 $PROTOCOL $TEST_GROUP_AND_TEST_NAME > "/tmp/$TEST_GROUP_AND_TEST_NAME.node.0.logs.txt" 2>&1 &
echo "starting node-1"
./node.sh 1 $PROTOCOL $TEST_GROUP_AND_TEST_NAME > "/tmp/$TEST_GROUP_AND_TEST_NAME.node.1.logs.txt" 2>&1 &
echo "starting node-2"
./node.sh 2 $PROTOCOL $TEST_GROUP_AND_TEST_NAME > "/tmp/$TEST_GROUP_AND_TEST_NAME.node.2.logs.txt" 2>&1 &

echo "node outputs are located in /tmp/$TEST_GROUP_AND_TEST_NAME.node.<nodeId>.logs.txt" 2>&1 &
echo "metric for each node is located in /tmp/$TEST_GROUP_AND_TEST_NAME.store.<nodeId>.metrics.csv"
echo "cluster is up and running, type anything to stop the cluster..."

read -n 1 key

echo "cluster processes:"
ps -A | grep java

echo "stopping the cluster..."
pkill 'java'

sleep 10
echo "cluster processes after cleanup:"
ps -A | grep java
