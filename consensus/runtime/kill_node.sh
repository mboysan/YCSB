#!/bin/bash

nodeId=$1
kill "$(jps -v | grep "node$nodeId" | cut -d " " -f 1)"
