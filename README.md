# Raccoon The Third
Raccoon The Third is a scalable Discord bot application that I have developed for personal use. This is a public, base version of the application which I use as a base for the bot that works on my Discord server. This project consists of 3 repositories:
- [Server](https://github.com/ksk98/RaccoonTheThird "Raccoon server") used to host the dockerized bot
- [Client](https://github.com/ksk98/RacoonClient "Raccoon client") that provides functionalities such as browsing server logs, reading discord messages and speaking as bot
- [Shared library](https://github.com/ksk98/RacoonShared "Raccoon shared") that is used both by the server and client

## Features
- Swing based interface
- Secure communication with the server
- Cachable connection credentials
- Reading and sending messages from bot's perspective
- Server logs monitoring

## Usage
To build a jar, run `createJar.bat` or manually run `gradlew jar` in the root project directory.

To make connection with the server, obtain its certificate file and place it in the same directory as the jar file. Certificate should be called `Raccoon.cert`.
If you need to replace the certificate file, make sure to also remove `local_keystore.jks` that the client creates in its directory.
