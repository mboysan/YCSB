#!/bin/bash

CONSUL_VERSION=1.18.1
DOWNLOAD_URL="https://releases.hashicorp.com/consul/${CONSUL_VERSION}/consul_${CONSUL_VERSION}_linux_amd64.zip"
INSTALL_DIR="./consul-latest"

mkdir -p ${INSTALL_DIR}
cd ${INSTALL_DIR}

wget $DOWNLOAD_URL
unzip consul_${CONSUL_VERSION}_linux_amd64.zip

touch version.txt
echo "${CONSUL_VERSION}" > version.txt

./consul --version
