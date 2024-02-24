#!/bin/bash

cd ./etcd-latest

nodeId=$1
nodeName="infra$nodeId"
etcdHost=localhost
etcdPeerPort="998$nodeId"
etcdClientPort="999$nodeId"
etcdMyPeerUrl="http://$etcdHost:$etcdPeerPort"
etcdMyClientUrl="http://$etcdHost:$etcdClientPort"
etcdPeerUrls="infra0=http://$etcdHost:9980,infra1=http://$etcdHost:9981,infra2=http://$etcdHost:9982"

./etcd --name "$nodeName" --initial-advertise-peer-urls "$etcdMyPeerUrl" \
  --listen-peer-urls "$etcdMyPeerUrl" \
  --listen-client-urls "$etcdMyClientUrl" \
  --advertise-client-urls "$etcdMyClientUrl" \
  --initial-cluster-token my-etcd-cluster-1 \
  --initial-cluster "$etcdPeerUrls" \
  --initial-cluster-state new

