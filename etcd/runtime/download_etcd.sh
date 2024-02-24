#!/bin/bash

ETCD_VER=v3.5.12
GITHUB_URL=https://github.com/etcd-io/etcd/releases/download
DOWNLOAD_URL=${GITHUB_URL}
INSTALL_DIR="./etcd-latest"

rm -f /tmp/etcd-${ETCD_VER}-linux-amd64.tar.gz
rm -rf ${INSTALL_DIR} && mkdir -p ${INSTALL_DIR}

curl -L ${DOWNLOAD_URL}/${ETCD_VER}/etcd-${ETCD_VER}-linux-amd64.tar.gz -o /tmp/etcd-${ETCD_VER}-linux-amd64.tar.gz
tar xzvf /tmp/etcd-${ETCD_VER}-linux-amd64.tar.gz -C ${INSTALL_DIR} --strip-components=1
rm -f /tmp/etcd-${ETCD_VER}-linux-amd64.tar.gz

cd ${INSTALL_DIR}
curl -O "https://raw.githubusercontent.com/etcd-io/etcd/main/LICENSE"

touch version.txt
echo "${ETCD_VER}" > version.txt

./etcd --version