package com.mystartup.rssaggregator.service;

import com.mystartup.rssaggregator.model.Channel;
import com.mystartup.rssaggregator.model.CustomTag;
import com.mystartup.rssaggregator.model.Item;
import com.mystartup.rssaggregator.model.RssFeed;
import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRssReaderServiceImplTest {

    @Mock
    RssUrlService rssUrlService;
    @Mock
    Unmarshaller unmarshaller;

    RssReaderService rssReaderService;
    RssFeed rssFeed;

    @BeforeEach
    void init() {
        rssReaderService = new DefaultRssReaderServiceImpl(rssUrlService, unmarshaller);

        rssFeed = new RssFeed();
        Channel rssChannel = new Channel();
        Item item = new Item();
        item.setTitle("item title");
        item.setDescription("item description");
        item.setCustomTags(Collections.singletonList(new CustomTag("customKey", "customValue")));
        rssChannel.setTitle("channel title");
        rssChannel.setDescription("channel description");
        rssChannel.setCategories(Collections.singletonList("category1"));
        rssChannel.setItems(Collections.singletonList(item));
        rssFeed.setChannel(rssChannel);
    }

    @Test
    @DisplayName("Reading a RSS feed, nominal case.")
    void readFeedNominalCase() throws URISyntaxException, RssAggregatorException, IOException {
        StreamSource streamSource = buildStreamSource("http://www.valid.com/rss/feed");
        when(unmarshaller.unmarshal(ArgumentMatchers.argThat(new SourceMatcher(streamSource)))).thenReturn(rssFeed);

        when(rssUrlService.get("http://www.valid.com/rss/feed")).thenReturn(new RssUrl(new URI("http://www.valid.com/rss/feed")));
        RssFeed actualRssFeed = rssReaderService.readFeed(new URI("http://www.valid.com/rss/feed"));
        assertEquals(rssFeed, actualRssFeed);
    }

    @Test
    @DisplayName("Reading a wrong RSS feed, exception is expected with the proper message.")
    void readFeedWrongLink() throws URISyntaxException, RssAggregatorException {
        when(rssUrlService.get("badUri")).thenReturn(new RssUrl(new URI("badUri")));
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> rssReaderService.readFeed(new URI("badUri")));
        assertEquals("Provided invalid URL!", thrownException.getMessage());
    }

    @Test
    @DisplayName("Reading a RSS feed, unmarshaller throwing exception")
    void readFeedUnmarshallingException() throws IOException, RssAggregatorException, URISyntaxException {
        when(rssUrlService.get("http://www.valid.com/rss/feed")).thenReturn(new RssUrl(new URI("http://www.valid.com/rss/feed")));
        doThrow(UnmarshallingFailureException.class).when(unmarshaller).unmarshal(any());
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> rssReaderService.readFeed(new URI("http://www.valid.com/rss/feed")));
        assertEquals("Could not read correctly from URL!", thrownException.getMessage());
    }

    @Test
    @DisplayName("Reading all configured RSS feeds, nominal case")
    void readFeedsNominalCase() throws URISyntaxException, RssAggregatorException, IOException {
        List<RssUrl> rssUrlList = Collections.singletonList(new RssUrl(new URI("http://www.valid.com/rss/feed"), 2));
        when(rssUrlService.getAll()).thenReturn(rssUrlList);
        when(rssUrlService.get("http://www.valid.com/rss/feed")).thenReturn(rssUrlList.get(0));
        StreamSource streamSource = buildStreamSource("http://www.valid.com/rss/feed");
        when(unmarshaller.unmarshal(ArgumentMatchers.argThat(new SourceMatcher(streamSource)))).thenReturn(rssFeed);

        List<RssFeed> feeds = rssReaderService.readFeeds();
        Assertions.assertTrue(feeds.equals(Collections.singletonList(rssFeed)));
    }

    @Test
    @DisplayName("Attempting to reading feeds when there are no configured RSS urls")
    void readFeedsZeroConfigured() {
        List<RssUrl> rssUrlList = Collections.EMPTY_LIST;
        when(rssUrlService.getAll()).thenReturn(rssUrlList);
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> rssReaderService.readFeeds());
        assertEquals("No URLs configured!", thrownException.getMessage());
    }

    @Test
    @DisplayName("Attempting to reading feeds when RSS url is empty string")
    void readFeedsThrowExceptionWhenEmptyRssAddress() throws URISyntaxException, RssAggregatorException {
        List<RssUrl> rssUrlList = Collections.singletonList(new RssUrl(new URI(""), 2));
        when(rssUrlService.getAll()).thenReturn(rssUrlList);
        when(rssUrlService.get("")).thenReturn(rssUrlList.get(0));
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> rssReaderService.readFeeds());
        assertEquals("Provided invalid URL!", thrownException.getMessage());
    }

    @Test
    @DisplayName("Attempting to reading feeds when bad RSS url string")
    void readFeedsThrowExceptionWhenBadRssAddress() throws URISyntaxException, RssAggregatorException {
        List<RssUrl> rssUrlList = Collections.singletonList(new RssUrl(new URI("badURL"), 2));
        when(rssUrlService.getAll()).thenReturn(rssUrlList);
        when(rssUrlService.get("badURL")).thenReturn(rssUrlList.get(0));
        Exception thrownException = Assertions.assertThrows(RssAggregatorException.class, () -> rssReaderService.readFeeds());
        assertEquals("Provided invalid URL!", thrownException.getMessage());
    }

    @Test
    @DisplayName("Searching a customValue in the configured list of RSS feed, nominal case")
    void searchFeedsNominalCase() throws IOException, RssAggregatorException, URISyntaxException {
        List<RssUrl> rssUrlList = Collections.singletonList(new RssUrl(new URI("http://www.valid.com/rss/feed"), 2));
        when(rssUrlService.getAll()).thenReturn(rssUrlList);
        when(rssUrlService.get("http://www.valid.com/rss/feed")).thenReturn(rssUrlList.get(0));
        StreamSource streamSource = buildStreamSource("http://www.valid.com/rss/feed");
        when(unmarshaller.unmarshal(ArgumentMatchers.argThat(new SourceMatcher(streamSource)))).thenReturn(rssFeed);
        List<Item> retrievedItems = rssReaderService.searchFeeds("customValue");
        Assertions.assertTrue(retrievedItems.equals(Collections.singletonList(rssFeed.getChannel().getItems().get(0))));
    }

    @Test
    @DisplayName("Searching a non present customValue in the configured list of RSS feed, nominal case")
    void searchFeedsNotPresentValue() throws IOException, RssAggregatorException, URISyntaxException {
        List<RssUrl> rssUrlList = Collections.singletonList(new RssUrl(new URI("http://www.valid.com/rss/feed"), 2));
        when(rssUrlService.getAll()).thenReturn(rssUrlList);
        when(rssUrlService.get("http://www.valid.com/rss/feed")).thenReturn(rssUrlList.get(0));
        StreamSource streamSource = buildStreamSource("http://www.valid.com/rss/feed");
        when(unmarshaller.unmarshal(ArgumentMatchers.argThat(new SourceMatcher(streamSource)))).thenReturn(rssFeed);
        List<Item> retrievedItems = rssReaderService.searchFeeds("not existing value");
        Assertions.assertTrue(retrievedItems.isEmpty());
    }

    @Test
    @DisplayName("Attempting to search null and empty values")
    void searchFeedsNullAndEmptyString() {
        Exception nullCaseException = Assertions.assertThrows(RssAggregatorException.class, () -> rssReaderService.searchFeeds(""));
        assertEquals("Search string cannot be null or empty!", nullCaseException.getMessage());
        Exception emptyCaseException = Assertions.assertThrows(RssAggregatorException.class, () -> rssReaderService.searchFeeds(null));
        assertEquals("Search string cannot be null or empty!", emptyCaseException.getMessage());
    }

    public static class SourceMatcher implements ArgumentMatcher<StreamSource> {

        private final StreamSource actualSource;

        public SourceMatcher(final StreamSource actualSource) {
            this.actualSource = actualSource;
        }

        @SneakyThrows
        @Override
        public boolean matches(final StreamSource givenSource) {
            return IOUtils.contentEquals(actualSource.getInputStream(), givenSource.getInputStream());
        }
    }

    private StreamSource buildStreamSource(String urlString) throws IOException {
        DataSource source = new URLDataSource(new URL(urlString));
        StreamSource streamSource = new StreamSource(source.getInputStream());
        return streamSource;
    }

}