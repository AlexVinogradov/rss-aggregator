# Example-App

## General

The example sub-module aims at providing an main class integrating the rss aggregator api in a non-spring managed environment.

## Configuration

The configuration class MainConfig extends the API default config RssAggregatorConfig and is used to “bootstrap” the Spring beans.

###### Beans

The BootstrapService class takes care of creating the AnnotationConfigApplicationContext, injecting the configuration and providing in-memory authentication. The BootstrapService class takes care of authenticating the user as well.
On top of already existing beans we add two custom ones:

- securityConfiguration: to provide in-memory authentication
- RssUrlService: we override the default bean still keeping the default implementation but initializing the Configuration with a RSS URL configuration.

## Testing

For testing the main class can be configured with the desired api calls and ran.

## Building

To build the project we can do a 
```sh
mvn clean install
```

Note: As we have a dependency on the api sub-module, please make sure to build it first. 
