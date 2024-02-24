#!/bin/bash

echo "starting node-0"
./node.sh 0 > /tmp/etcd_node0.txt 2>&1 &
echo "starting node-1"
./node.sh 1 > /tmp/etcd_node1.txt 2>&1 &
echo "starting node-2"
./node.sh 2 > /tmp/etcd_node2.txt 2>&1 &

echo "node outputs are located in /tmp/etcd_node<nodeId>.txt"
echo "cluster is up and running, type anything to stop the cluster..."

read -n 1 key

echo "cluster processes:"
ps -A | grep etcd

echo "stopping the cluster..."
pkill 'etcd'

sleep 10
echo "cluster processes after cleanup:"
ps -A | grep etcd
