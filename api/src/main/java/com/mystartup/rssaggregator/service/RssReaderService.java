package com.mystartup.rssaggregator.service;

import com.mystartup.rssaggregator.model.Item;
import com.mystartup.rssaggregator.model.RssFeed;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import lombok.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

/**
 * Interface providing the RSS Aggregator functionalities to interact with the configured RSS URLs
 * RBAC is ensured by Spring Security
 * Default Implementation uses the RssUrlService interface to retrieve configured URLs.
 */
@Service
@PreAuthorize("isFullyAuthenticated()")
public interface RssReaderService {

    /**
     * Reads a specific RSS feed and return the deserialized feed into the RSSAggregator Feed object.
     * On top of parameter checks, the logic verifies that the given link against the existing configured RSS URLs.
     * Custom, non-standard (other then specified in RSS standards) keys are mapped in the customKeys attribute
     * of each item.
     *
     * @param feedUrl the feed URI to be read.
     * @return the RssFeed deserialized object
     * @throws RssAggregatorException when there was a deserialization issue or the provided parameter is invalid
     * (null or cannot be read parsed)
     */
    RssFeed readFeed(@NonNull final URI feedUrl) throws RssAggregatorException;

    /**
     * Reads all the configured RSS URLs and returns a list of deserialized feeds.
     * @return the list of RSS feeds read.
     * @throws RssAggregatorException when there are no URLs configured, or when issues while
     * reading/deserializing from existing URLs
     */
    List<RssFeed> readFeeds() throws RssAggregatorException;

    /**
     * Searches for a specific keyphrase in the items of each feed/channel. Custom keys are searched as well.
     * The search triggers a new read of all the feeds.
     *
     * @param keyphrase a non-null string to be searched.
     * @return a list of Items which contain the keyphrase.
     * @throws RssAggregatorException when there are validation errors on the keyphrase, no URLs configured,
     * or when issues while reading/deserializing from existing URLs
     */
    List<Item> searchFeeds(@NonNull final String keyphrase) throws RssAggregatorException;

    /**
     * Helper method to implement the logic of periodically reading information from the configured RSS URLs
     * A separate thread is created for each configuration. Each thread is polling constantly at the refresh
     * rate configured in each RSSUrl individually.
     * Default Implementation is to output the result in the log.
     */
    @PreAuthorize("hasRole('ADMIN')")
    void readPeriodically();
}
