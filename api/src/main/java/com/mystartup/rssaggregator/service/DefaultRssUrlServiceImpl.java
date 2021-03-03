package com.mystartup.rssaggregator.service;

import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public final class DefaultRssUrlServiceImpl implements RssUrlService {

    private List<RssUrl> rssUrlList;

    public DefaultRssUrlServiceImpl(@NonNull final ArrayList<RssUrl> rssUrlList) {
        this.rssUrlList = rssUrlList;
    }

    @Override
    public List<RssUrl> getAll() {
        return rssUrlList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public RssUrl get(final String url) throws RssAggregatorException {
        return getRssUrlFromConfiguration(url).orElseThrow(() -> new RssAggregatorException("URL could not be found in current configuration."));
    }

    @Override
    public void addOrUpdate(final RssUrl rssUrl) throws RssAggregatorException {
        if(rssUrl == null){
            String errorMessage = "Add RSS url configuration cannot be called with null!";
            log.error(errorMessage);
            throw new RssAggregatorException(errorMessage);
        }
        final boolean urlAlreadyConfigured = getRssUrlFromConfiguration(rssUrl.getUri().toString()).isPresent();
        if (urlAlreadyConfigured) {
            log.info("Url already existing in current configuration. Will update");
            update(rssUrl);
        } else {
            log.info("Adding URL to list");
            rssUrlList.add(rssUrl);
        }
    }

    @Override
    public void delete(final String rssUrl) throws RssAggregatorException {
        if(rssUrl == null){
            String errorMessage = "Delete RSS url configuration cannot be called with null!";
            log.error(errorMessage);
            throw new RssAggregatorException(errorMessage);
        }
        try {
            new URL(rssUrl);
        } catch (MalformedURLException e) {
            throw new RssAggregatorException(String.format("Invalid URL! The provided url %s is invalid",rssUrl));
        }

        log.info("Deleting URL if existing in current configuration.");

        boolean removed = rssUrlList.removeIf(existingUrl -> existingUrl.getUri() != null && existingUrl.getUri().toString().equalsIgnoreCase(rssUrl));
        if(!removed){
            throw new RssAggregatorException(String.format("Error while deleting %s, it is not a known configuration",rssUrl));
        }
    }

    private void update(@NonNull final RssUrl modifiedRssUrl) {
        rssUrlList = rssUrlList.stream()
                .filter(existingUrl -> existingUrl.getUri() != null)
                .map(existingUrl -> existingUrl.getUri().equals(modifiedRssUrl.getUri()) ? modifiedRssUrl : existingUrl)
                .collect(Collectors.toList());
    }

    private Optional<RssUrl> getRssUrlFromConfiguration(String url) {
        return rssUrlList.stream()
                .filter(Objects::nonNull)
                .filter(existingUrl -> existingUrl.getUri() != null)
                .filter(existingUrl -> existingUrl.getUri().toString().equalsIgnoreCase(url)).findAny();
    }
}
