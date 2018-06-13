#!/bin/sh
cd $(dirname $0)/src/main/ngapp
npm install @angular/cli
ng build --prod

