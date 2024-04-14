#!/bin/bash

CONSENSUS_VERSION=1.10
DOWNLOAD_URL="https://raw.githubusercontent.com/mboysan/mvn-repo/releases/consensus/consensus-distribution-${CONSENSUS_VERSION}-jar-with-dependencies.jar"
INSTALL_DIR="./consensus-latest"

mkdir -p ${INSTALL_DIR}
cd ${INSTALL_DIR}

wget $DOWNLOAD_URL -O consensus.jar

touch version.txt
echo "${CONSENSUS_VERSION}" > version.txt

