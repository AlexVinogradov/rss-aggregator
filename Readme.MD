# RSS Aggregator

[![CI](https://github.com/AlexVinogradov/rss-aggregator/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/AlexVinogradov/rss-aggregator/actions/workflows/main.yml)

RSS Aggregator is a tool that helps manage your RSS Feeds with some features to help you navigate thru them.

## Features

- Create/Read/Update/Delete RSS configurations (A configuration is composed of RSS url and refresh time interval).
- Read a given RSS feed or all configured RSS feeds.
- Search a keyword among all items in all configured RSS feeds.
- Periodically read feeds content based on the RSS configurations.
- Authentication and Role Based Access Control (RBAC).
- Easy integration with Spring managed applications (Boot etc.)


## Tech

RSS Aggregator uses a number of open source projects to work properly:

- Spring Security - For authentication and authorisation
- Spring OXM - Spring's Object/XML Mapping support (for unmarshalling)
- Lombok - Boilerplate code reduction
- SLJ4J - Logging
- JUnit - Integration and unit tests.

## Structure

The project is structured as a Maven Reactor project with 3 sub-modules:

- Api - The api itself containing the interfaces, implementations and logic, data model, unit-tests and Spring configuration needed for the beans.
- Integration-tests - Sub-module containing tests that spawn a real Spring context. The purpose is to test the functionalities end to end including security enforcing.
- Example - We provide a integration example in a non-Spring managed project. Integration with Spring managed project will come soon.

## Building

Triggering the install command at maven reactor level will build in order the api, run the integration tests and build the example jar.

Doing a simple
```sh
mvn clean install
```
Sub-modules can be built separatelly but they are dependent on the API sub-module. You will need to trigger a rebuild on it first.
More information about each submodule:
| Sub-module | README |
| ------ | ------ |
| API | [api/README.md][api] |
| Integration-Tests | [integration-tests/README.md][int-tests] |
| Example-app| [example-app/README.md][example] |


   [api]: <https://github.com/AlexVinogradov/rss-aggregator/blob/master/api/Readme.MD>
   [int-tests]: <https://github.com/AlexVinogradov/rss-aggregator/blob/master/integration-tests/Readme.MD>
   [example]: <https://github.com/AlexVinogradov/rss-aggregator/blob/master/example-app/Readme.MD>