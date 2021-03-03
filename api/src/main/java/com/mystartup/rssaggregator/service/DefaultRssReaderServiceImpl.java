package com.mystartup.rssaggregator.service;

import com.mystartup.rssaggregator.model.Item;
import com.mystartup.rssaggregator.model.RssFeed;
import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public final class DefaultRssReaderServiceImpl implements RssReaderService {

    RssUrlService rssUrlService;

    Unmarshaller unmarshaller;

    public DefaultRssReaderServiceImpl(@Autowired RssUrlService rssUrlService, @Autowired Unmarshaller unmarshaller) {
        this.rssUrlService = rssUrlService;
        this.unmarshaller = unmarshaller;
    }

    @Override
    public RssFeed readFeed(final URI feedUri) throws RssAggregatorException {
        if(feedUri == null){
            String errorMessage = "Provided URL is null!";
            log.error(errorMessage);
            throw new RssAggregatorException(errorMessage);
        }
        try {
            RssUrl configuredRssUrl = rssUrlService.get(feedUri.toString());
            return (RssFeed) unmarshaller.unmarshal(getSource(configuredRssUrl.getUri()));
        } catch (IOException | IllegalArgumentException e) {
            String errorMessage = "Provided invalid URL!";
            log.error(errorMessage, e);
            throw new RssAggregatorException(errorMessage);
        } catch (XmlMappingException e) {
            String errorMessage = "Could not read correctly from URL!";
            log.error(errorMessage, e);
            throw new RssAggregatorException(errorMessage);
        }

    }

    @Override
    public List<RssFeed> readFeeds() throws RssAggregatorException {
        List<RssFeed> feeds = new ArrayList<>();
        List<RssUrl> urls = rssUrlService.getAll();

        if (urls.isEmpty()) {
            String errorMessage = "No URLs configured!";
            log.debug(errorMessage);
            throw new RssAggregatorException(errorMessage);
        }
        for (RssUrl rssUrl : urls) {
            try {
                feeds.add(this.readFeed(rssUrl.getUri()));
            } catch (RssAggregatorException e) {
                log.debug(String.format("Issue while reading feed from url %s", rssUrl));
                throw e;
            }
        }
        return feeds;
    }

    @Override
    public List<Item> searchFeeds(final String keyphrase) throws RssAggregatorException {
        if (keyphrase == null || keyphrase.isEmpty()) {
            String errorMessage = "Search string cannot be null or empty!";
            log.error(errorMessage);
            throw new RssAggregatorException(errorMessage);
        }
        List<RssFeed> feeds = readFeeds();
        return feeds.stream().flatMap((RssFeed rssFeed) ->
                rssFeed.getChannel().getItems().stream()
                        .filter(Objects::nonNull)
                        .filter(item -> item.getSearchValues().toLowerCase().contains(keyphrase.toLowerCase()))).collect(Collectors.toList());
    }

    @Override
    public void readPeriodically() {
        List<RssUrl> rssUrlList = rssUrlService.getAll();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(rssUrlList.size());
        rssUrlList.forEach(rssUrl -> {
            Runnable task = () -> {
                try {
                    RssFeed rssFeed = readFeed(rssUrl.getUri());
                    log.info(String.format("Read URL configuration: %s, retrieved RSS feed: %s", rssUrl, rssFeed.toString()));
                } catch (RssAggregatorException e) {
                    log.error(String.format("Error reading URL: %s", rssUrl), e);
                }
            };
            executor.scheduleWithFixedDelay(task, 0, rssUrl.getRefreshIntervalMinutes(), TimeUnit.MINUTES);
        });
    }

    private Source getSource(URI feedUri) throws IOException {
        DataSource source = new URLDataSource(feedUri.toURL());
        return new StreamSource(source.getInputStream());
    }

}
