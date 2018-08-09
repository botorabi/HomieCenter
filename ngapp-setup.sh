#!/bin/sh
cd $(dirname $0)/src/main/ngapp
rm -rf node_modules
npm install @angular/cli
npm update

