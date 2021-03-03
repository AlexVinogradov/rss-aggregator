package com.mystartup.rssaggregator.service;


import com.mystartup.rssaggregator.model.Item;
import com.mystartup.rssaggregator.model.RssFeed;
import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = IntegrationTestSpringConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RssFeedAggregatorIntegrationTests {

    @Autowired
    RssReaderService reader;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    RssUrlService rssUrlService;

    @Test
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void readFeedNominalCase() throws RssAggregatorException, URISyntaxException {
        RssFeed feed = reader.readFeed(new URI("https://www.wired.com/feed/category/science/latest/rss"));
        Assert.assertNotNull(feed.getChannel());
        Assert.assertEquals("Science Latest", feed.getChannel().getTitle());
        Assert.assertEquals("Channel Description", feed.getChannel().getDescription());
        Assert.assertEquals("en", feed.getChannel().getLanguage());
        Assert.assertEquals("https://www.wired.com/category/science/latest", feed.getChannel().getLink());
        Assert.assertEquals("© Condé Nast 2021", feed.getChannel().getCopyright());
        Assert.assertFalse(feed.getChannel().getItems().isEmpty());
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void readFeedUnauthenticatedShouldFail() throws RssAggregatorException, URISyntaxException {
        reader.readFeed(new URI("https://www.wired.com/feed/category/science/latest/rss"));
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void readFeedNonConfiguredShouldFail() throws RssAggregatorException, URISyntaxException {
        reader.readFeed(new URI("https://www.nicematin.com/ville/antibes/rss"));
    }

    @Test(expected = URISyntaxException.class)
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void readFeedInvalidUrlShouldFail() throws RssAggregatorException, URISyntaxException {
        reader.readFeed(new URI("invalid url"));
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void readFeedNullUrlShouldFail() throws RssAggregatorException, URISyntaxException {
        reader.readFeed(null);
    }




    @Test
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void readAllFeedsNominalCase() throws RssAggregatorException, URISyntaxException {
        List<RssFeed> feeds = reader.readFeeds();
        Assert.assertEquals(2, feeds.size());

        RssFeed feed1 = feeds.get(0);
        Assert.assertNotNull(feed1.getChannel());
        Assert.assertEquals("Articles on TechRepublic", feed1.getChannel().getTitle());
        Assert.assertEquals("Articles on TechRepublic", feed1.getChannel().getDescription());
        Assert.assertEquals("en", feed1.getChannel().getLanguage());
        Assert.assertEquals("https://www.techrepublic.com/", feed1.getChannel().getLink());
        Assert.assertEquals("© 2021 ZDNET, A RED VENTURES COMPANY. ALL RIGHTS RESERVED.", feed1.getChannel().getCopyright());
        Assert.assertEquals("Technology", feed1.getChannel().getCategories().get(0));
        Assert.assertEquals(Integer.valueOf(2), feed1.getChannel().getTtl());
        Assert.assertFalse(feed1.getChannel().getItems().isEmpty());

        RssFeed feed2 = feeds.get(1);
        Assert.assertNotNull(feed2.getChannel());
        Assert.assertEquals("Science Latest", feed2.getChannel().getTitle());
        Assert.assertEquals("Channel Description", feed2.getChannel().getDescription());
        Assert.assertEquals("en", feed2.getChannel().getLanguage());
        Assert.assertEquals("https://www.wired.com/category/science/latest", feed2.getChannel().getLink());
        Assert.assertEquals("© Condé Nast 2021", feed2.getChannel().getCopyright());
        Assert.assertFalse(feed2.getChannel().getItems().isEmpty());
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void readAllFeedsUnauthenticatedShouldFail() throws RssAggregatorException, URISyntaxException {
        reader.readFeeds();
    }




    @Test
    @WithMockUser(username = "john")
    public void searchAllFeedsNominalCase() throws RssAggregatorException {
        List<Item> retrievedItems = reader.searchFeeds("a");
        Assert.assertFalse(retrievedItems.isEmpty());
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "john")
    public void searchAllFeedsNullValue() throws RssAggregatorException {
        reader.searchFeeds(null);
    }




    @Test
    @WithMockUser(username = "john", roles = {"ADMIN"})
    public void readPeriodicallyNominalCase() {
        reader.readPeriodically();
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void readPeriodicallyNotAbleToTriggerIfNotAdmin() {
        reader.readPeriodically();
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void readPeriodicallyNotAbleToTriggerUnauthenticated() {
        reader.readPeriodically();
    }



    @Test(expected = BadCredentialsException.class)
    public void test_real_authentication_bad_credentials() {
        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken("badUser", "badPassword");
        authManager.authenticate(authReq);
    }

    @Test
    public void test_real_authentication_good_credentials() {
        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken("admin", "admin123");
        authManager.authenticate(authReq);
    }

}
