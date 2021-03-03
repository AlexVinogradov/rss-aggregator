package com.mystartup.rssaggregator.service;

import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultRssUrlServiceImplTest {

    RssUrlService rssUrlService;
    RssUrl rssUrl;

    @BeforeEach
    void init() throws RssAggregatorException, URISyntaxException {
        rssUrl = new RssUrl(new URI("http://www.valid.com/rss/feed"), 5);
        ArrayList<RssUrl> urls = new ArrayList<>();
        urls.add(rssUrl);
        rssUrlService = new DefaultRssUrlServiceImpl(urls);
    }

    @Test
    @DisplayName("Retrieve all configured RSS Urls, nominal case")
    void getAllNominalCase() {
        List<RssUrl> rssUrlList = rssUrlService.getAll();
        assertEquals(1, rssUrlList.size());
        assertEquals(Collections.singletonList(rssUrl), rssUrlList);
    }

    @Test
    @DisplayName("Attempt to initialize with null values")
    void initNullList() {
        Exception thrownException = Assertions.assertThrows(NullPointerException.class, () -> new DefaultRssUrlServiceImpl(null));
        assertEquals("rssUrlList is marked non-null but is null", thrownException.getMessage());

        ArrayList<RssUrl> nullList= new ArrayList<>();
        nullList.add(null);
        rssUrlService = new DefaultRssUrlServiceImpl(nullList);
        assertTrue(rssUrlService.getAll().isEmpty());
    }

    @Test
    @DisplayName("Retrieve a  RSS Url configuration by the url string, nominal case")
    void getNominalCase() throws RssAggregatorException {
        RssUrl rssUrlConfiguration = rssUrlService.get("http://www.valid.com/rss/feed");
        assertEquals(rssUrl, rssUrlConfiguration);
    }

    @Test
    @DisplayName("Try to get a non-configured RSS url")
    void getNonExisting() {
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> rssUrlService.get("http://nonexistent"));
        assertEquals("URL could not be found in current configuration.", thrownException.getMessage());
    }

    @Test
    @DisplayName("Try to add a non-configured RSS url, nominal case")
    void addNominalCase() throws URISyntaxException, RssAggregatorException {
        RssUrl rssUrlToAdd = new RssUrl(new URI("http://www.another.com/valid/rss"));

        rssUrlService.addOrUpdate(rssUrlToAdd);

        Assertions.assertTrue(rssUrlService.getAll().contains(rssUrlToAdd));

    }

    @Test
    @DisplayName("Try to update a already configured RSS url, nominal case")
    void updateExisting() throws RssAggregatorException {
        RssUrl modified = new RssUrl(rssUrl.getUri(), rssUrl.getRefreshIntervalMinutes() + 10);
        rssUrlService.addOrUpdate(modified);
        Assertions.assertEquals(1, rssUrlService.getAll().size());
        Assertions.assertEquals(Integer.valueOf(rssUrl.getRefreshIntervalMinutes() + 10), rssUrlService.get(modified.getUri().toString()).getRefreshIntervalMinutes());
    }

    @Test
    @DisplayName("Try to update a already configured RSS url with invalid refresh interval")
    void updateExistingInvalid() throws RssAggregatorException {
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> new RssUrl(rssUrl.getUri(), rssUrl.getRefreshIntervalMinutes() - 10));
        Assertions.assertEquals("Invalid refresh interval (minutes). Please give a integer value greater than 0", thrownException.getMessage());
    }

    @Test
    @DisplayName("Try to delete a already configured RSS url, nominal case")
    void delete() throws RssAggregatorException {
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> rssUrlService.delete("http://nonexistent"));
        Assertions.assertEquals("Error while deleting http://nonexistent, it is not a known configuration", thrownException.getMessage());
    }

    @Test
    @DisplayName("Try to delete a invalid RSS url")
    void deleteInvalid() throws RssAggregatorException {
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> rssUrlService.delete("invalid url"));
        Assertions.assertEquals("Invalid URL! The provided url invalid url is invalid", thrownException.getMessage());
    }
}