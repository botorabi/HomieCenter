#!/bin/sh

if [ "$#" -ne 1 ]; then
    echo "Use: distribute-package.sh <version string>"
    exit -1
fi

cd $(dirname $0)
CWD=$PWD

VERSION=$1
DIST_DIR=homiecenter-${VERSION}
rm -rf ${DIST_DIR}*

cd ${CWD}/..

echo "Building Angular App..."
./ngapp-build.sh

echo "Building, testing, and packaging the Web App..."
mvn clean test package

cd ${CWD}

tar xf template.tar.bz2
mv template ${DIST_DIR}

cp ../target/homiecenter-*.jar ${DIST_DIR}/homiecenter/homiecenter.jar

sed -i -e "s/@VERSION@/${VERSION}/g" ${DIST_DIR}/README.txt

tar -cf ${DIST_DIR}.tar ${DIST_DIR}
bzip2 ${DIST_DIR}.tar

rm -rf ${DIST_DIR} template

echo "Distribution package ready: ${DIST_DIR}.tar.bz2"

