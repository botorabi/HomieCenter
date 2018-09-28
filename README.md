    Copyright (c) 2018 by Botorabi. All rights reserved.
    https://github.com/botorabi/HomieCenter

    License: MIT License (MIT)
    Read the LICENSE text in main directory for more details.

    First Created:     May 2018
    Author:            Botorabi (botorabi AT gmx DOT net)
                       https://vr-fun.net


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

Use the **ngapp-build-dev.sh** for building the Angular project during development. Finally, use the **ngapp-build.sh** to build the Angular project for production.

Use the script **distribute-package.sh** in directory distribution in order to build the application and create a complete package ready for distribution.


# Running the Web Application

As expected from a Spring Boot application, running it is as simple as issuing the following command after a successful build:

  java -jar target/homiecenter-< VERSION >.tar


The application settings such as a custom FRITZ!Box URL can be set in file 'homiecenter.properties' which must be located in the execution directory.


# Screenshots

Here are a few screenshots showing Homie Center's user interface.

**An earlier version of the UI.**

![Screenshot 1](https://user-images.githubusercontent.com/11502867/45612070-0946db00-ba62-11e8-98f4-774b60e96581.png)


**New version of the UI showing the support for Heat Controller**

![Screenshot 2](https://user-images.githubusercontent.com/11502867/45612071-0946db00-ba62-11e8-9e41-2ad34bbe0ac7.png)


**A piece of mobile view**

![Screenshot 3](https://user-images.githubusercontent.com/11502867/45612072-0946db00-ba62-11e8-8199-31377908f522.png)

