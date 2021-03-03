package com.mystartup.rssaggregator.service;

import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import lombok.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Interface providing the RSS Aggregator CRUD functionalities to administrate RSS URLs
 * RBAC is ensured by Spring Security
 * Default implementation uses in-memory persistence. Custom persistence can be achieved by providing user-provided
 * implementation of this interface
 */
@Service
@PreAuthorize("isFullyAuthenticated()")
public interface RssUrlService {

    /**
     * Retrieve all currently configured RSS Urls
     * @return a list of RssUrl s
     */
    List<RssUrl> getAll();

    /**
     * Retrieve a specific RssUrl based on a given url string.
     * Checks are implemented in the default implementation.
     * @param url the string to search for in the current configurations
     * @return the RssUrl found
     * @throws RssAggregatorException when no URL could have been found in the current configurations
     */
    RssUrl get(@NonNull final String url) throws RssAggregatorException;

    /**
     * Add a new configuration or Update an existing one.
     *
     * @param rssUrl the RssUrl to add or update. Validations are enforced.
     * @throws RssAggregatorException if provided value is null or adding/updating the list of configurations failed.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void addOrUpdate(@NonNull final RssUrl rssUrl) throws RssAggregatorException;

    /**
     * Delete an existing configuration.
     * @param rssUrl the RssUrl to delete. Validations are enforced.
     * @throws RssAggregatorException if provided value is null or deleting from the list of configurations failed.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void delete(@NonNull final String rssUrl) throws RssAggregatorException;
}
