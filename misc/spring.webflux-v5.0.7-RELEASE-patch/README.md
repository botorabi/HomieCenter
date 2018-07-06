    Copyright (c) 2018 by Botorabi. All rights reserved.
    https://github.com/botorabi/HomieCenter

    License: MIT License (MIT)
    Read the LICENSE text in main directory for more details.

    First Created:     July 2018
    Author:            Botorabi (botorabi AT gmx DOT net)

# Patch for Spring Webflux 5.0.7.RELEASE

Spring Webflux 5.0.7.RELEASE has a hard-coded size limitation for reactive WebSocket packets which may make streaming multi-media contents such as mjpg streams impossible.

This patch provides support for increased WebSocket packet size (5 MBytes instead of 64 KBytes).
This is a temporary solution, we hope that in future releases the packet size limit will be subject of Spring framework configuration.

Install the patched Webflux library in your local maven repository by issuing the provided shell script 'install-webflux-package.sh'. Then include the library in project's pom.xml as first dependency, see below.

    <dependencies>
        <dependency>
            <groupId>spring-webflux-boto</groupId>
            <artifactId>reactive-websocket</artifactId>
    	    <version>5.0.7</version>
    	</dependency>

    	<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter</artifactId>
        </dependency>
	...

