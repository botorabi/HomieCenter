    Copyright (c) 2018 by Botorabi. All rights reserved.
    https://github.com/botorabi/HomieCenter

    License: MIT License (MIT)
    Read the LICENSE text in main directory for more details.

    First Created:     May 2018
    Author:            Botorabi (botorabi AT gmx DOT net)

[![Maintainability](https://api.codeclimate.com/v1/badges/3e56cc0c08d3b552019c/maintainability)](https://codeclimate.com/github/botorabi/HomieCenter/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/3e56cc0c08d3b552019c/test_coverage)](https://codeclimate.com/github/botorabi/HomieCenter/test_coverage)
[![Build Status](https://travis-ci.org/botorabi/HomieCenter.svg?branch=master)](https://travis-ci.org/botorabi/HomieCenter)

# Homie Center

**Homie Center** is a web application dedicated to home automation. It provides a simple control center for devices connected to a FRITZ!Box (see https://avm.de).
Homie Center is implemented using Spring Boot and Angular and runs on every device capable of running Java such as Raspberry Pi.


# Build

On a fresh clone of the repository use the script **ngapp-setup.sh** for installing the Angular dependencies. Make sure that the command **npm** is available on your system.

The Maven rule for packaging (mvn package) will build the entire web application along the Spring Boot and Angular projects. During the development you may want to 
also use the following scripts:

Use the **ngapp-dev-serve.sh** to start the Angular development web server. Finally, use the **ngapp-build.sh** to build the Angular project for production.


# Running the Web Application

As expected from a Spring Boot application, running it is as simple as issuing the following command after a successful build:

  java -jar target/homiecenter-< VERSION >.tar


The application settings such as a custom FRITZ!Box URL can be set in file 'homiecenter.properties' which must be located in the execution directory.

