#!/bin/sh
cd $(dirname $0)/src/main/ngapp
npm install @angular/cli
npm update
./node_modules/.bin/ng build --prod

