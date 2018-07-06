#!/bin/sh

mvn install:install-file -Dfile=./patched-lib/spring-webflux-5.0.7.RELEASE-patched.jar -Dsources=./patched-lib/spring-webflux-5.0.7.RELEASE-patched-sources.jar -DgroupId=spring-webflux-boto -DartifactId=reactive-websocket -Dversion=5.0.7 -Dpackaging=jar -DgeneratePom=true
