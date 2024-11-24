#!/bin/bash

pushd ../..

TEST_GROUP_AND_TEST_NAME="${1:-"W0.T1"}"
WORKLOAD="${2:-"workloads/workload_rw_10k"}"

./bin/ycsb.sh run consensus -s \
  -p measurementtype=raw \
  -p measurement.raw.output_file="/tmp/$TEST_GROUP_AND_TEST_NAME.client.raw.csv" \
  -p measurement.raw.separator=, \
  -p exportfile="/tmp/$TEST_GROUP_AND_TEST_NAME.client.summary.txt" \
  -p threadcount=1 \
  -P consensus/conf/application.properties \
  -P $WORKLOAD

popd
