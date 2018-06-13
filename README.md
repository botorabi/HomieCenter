    Copyright (c) 2017-2018 by Botorabi. All rights reserved.
    https://github.com/botorabi/HomieCenter

    License: MIT License (MIT)
    Read the LICENSE text in main directory for more details.

    First Created:     May 2018
    Author:            Botorabi (botorabi AT gmx DOT net)

# Homie Center

**Homie Center** is a web application dedicated to home automation. It provides a simple control center for devices connected to a FRITZ!Box (see https://avm.de).
Homie Center is implemented using Spring Boot and Angular and runs on every device capable of running Java such as Raspberry Pi.


# Build

On a fresh clone of the repository use the script **ngapp-setup.sh** for installing the Angular dependencies. Make sure that the commands npm and ng are available on your system.

The Maven rule for packaging (mvn package) will build the entire web application along the Spring Boot and Angular projects. During the development you may want to 
also use the following scripts:

Use the **ngapp-dev-serve.sh** to start the Angular development web server. Finally, use the **ngapp-build.sh** to build the Angular project for production.


# Running the Web Application

As expected from a Spring Boot application, running it is as simple as issuing the following command after a successful build:

  java -jar target/homiecenter-< VERSION >.tar


The application will try to connect to your FRITZ!Box via the URL **http : // fritz.box** (whithout the blanks), this URL is the most common one accessing a FRITZ!Box from your local network.
It is also possible to define a custom URL by using the environment variable **homiecenter.fritzbox.url**. Here is an example for a custom URL:

  export  homiecenter_fritzbox_url=http://< my custom FRITZ!Box IP >:< my custom port >
