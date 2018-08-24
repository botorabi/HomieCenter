#!/bin/sh

echo "Creating distribution package for Homie Center"

cd $(dirname $0)
CWD=$PWD
VERSION=""

if [ "$#" -eq 1 ]; then
  VERSION=$1
  echo "Using command line argument for version information: ${VERSION}"
else
  echo "Retrieving version information from pom.xml file..."
  VERSION=$(cd ..; mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\[')
  echo "Using version information: ${VERSION}"
fi

if [ "${VERSION}" = "@" ] || [ -z "${VERSION}" ]; then
  echo "Could not determine the version information, define it by command line!"
  echo " Use: distribute-package.sh <version string>"
  exit -1
fi

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

