#!/bin/bash

CODE_ROOT='/data/corpus'
DEPLOY_ROOT='/data/corpus-deploy'
DATA_ROOT='/data/chinese-poetry-simplified'
VERSION='1.0-SNAPSHOT'

cd ${CODE_ROOT}
git pull
mvn clean package

cd ${DEPLOY_ROOT}
rm -rf corpus-web-${VERSION}.jar corpus-index-${VERSION}.jar
cp ${CODE_ROOT}/corpus-index/target/corpus-index-${VERSION}.jar .
cp ${CODE_ROOT}/corpus-web/target/corpus-web-${VERSION}.jar .

rm -rf index/*
java -jar corpus-index-${VERSION}.jar -i ${DATA_ROOT} -o index

PID=$(head -n 1 pid.file)
while [[ -e /proc/${PID} ]]; do
  kill ${PID}
  sleep 2
done

echo 'previous service shutdown.'

sh startup.sh
