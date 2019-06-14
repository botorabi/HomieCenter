#!/bin/sh

mvn install:install-file -Dfile=./patched-lib/spring-webflux-5.1.8.RELEASE-patched.jar -Dsources=./patched-lib/spring-webflux-5.1.8.RELEASE-patched-sources.jar -DgroupId=spring-webflux-boto -DartifactId=reactive-websocket -Dversion=5.1.8 -Dpackaging=jar -DgeneratePom=true
