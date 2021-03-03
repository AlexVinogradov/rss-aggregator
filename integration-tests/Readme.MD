# Integration Tests

## General

The integration tests module aims at testing the functionalities with by configuring a real Spring context and targetting real RSS URLs.

## Configuration

The configuration class IntegrationTestSpringConfig extends the API default config RssAggregatorConfig and is used to “bootstrap” the Spring beans. This can be used as a reference on how to integrate the RSS Aggregator in a Spring managed project (Spring boot project i.e.)


###### Beans

On top of already existing beans we add two custom ones:

- securityConfiguration: to provide in-memory authentication
- RssUrlService: we override the default bean still keeping the default implementation but initializing the Configuration with 2 RSS URL configurations.

## Testing

For testing we make use of Junit4 and spring-security-test module for impersonating users.
Context configuration is specified by ContextConfiguration annotation and real beans are injected in the test classes.
Real RSS feeds are targeted and validation is done at Channel level.

## Building

To build the project and run the tests we can do a 
```sh
mvn clean install
```

Note: As we have a dependency on the api sub-module, please make sure to build it first. 
