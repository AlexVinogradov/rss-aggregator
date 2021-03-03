package com.mystartup.rssaggregator.service;


import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = IntegrationTestSpringConfig.class)
public class RssUrlAdministrationIntegrationTests {

    public static final String ANOTHER_COM_VALID_RSS = "http://www.another.com/valid/rss";
    public static final String BAD_URL = "bad url";
    public static final String TECHREPUBLIC_COM_RSSFEEDS_ARTICLES = "https://www.techrepublic.com/rssfeeds/articles";

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    RssUrlService rssUrlService;


    @Test
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void getAllNominalCase() {
        List<RssUrl> urls = rssUrlService.getAll();
        Assert.assertEquals(2, urls.size());
        Assert.assertTrue(urls.stream().anyMatch(url -> url.getUri().toString().equalsIgnoreCase(TECHREPUBLIC_COM_RSSFEEDS_ARTICLES)));
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void getAllUnAuthenticatedShouldFail() {
        rssUrlService.getAll();
    }

    @Test
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void getSpecificUrlNominalCase() throws RssAggregatorException {
        RssUrl retrievedUrl = rssUrlService.get(TECHREPUBLIC_COM_RSSFEEDS_ARTICLES);
        Assert.assertEquals(TECHREPUBLIC_COM_RSSFEEDS_ARTICLES, retrievedUrl.getUri().toString());
        Assert.assertEquals(Integer.valueOf(10), retrievedUrl.getRefreshIntervalMinutes());
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void getSpecificUrlUnauthenticatedShouldFail() throws RssAggregatorException {
        rssUrlService.get(TECHREPUBLIC_COM_RSSFEEDS_ARTICLES);
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void getSpecificUrlNonExistingShouldFail() throws RssAggregatorException {
        rssUrlService.get("https://www.nicematin.com/ville/sophia-antipolis/rss");
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void getSpecificUrlBadURLShouldFail() throws RssAggregatorException {
        rssUrlService.get(BAD_URL);
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addOrUpdateNominalCase() throws RssAggregatorException, URISyntaxException {
        RssUrl rssUrl = new RssUrl(new URI(ANOTHER_COM_VALID_RSS), 15);
        rssUrlService.addOrUpdate(rssUrl);
        List<RssUrl> urls = rssUrlService.getAll();
        Assert.assertEquals(3, urls.size());
        Assert.assertTrue(urls.stream().anyMatch(url -> url.getUri().toString().equalsIgnoreCase("http://www.another.com/valid/rss")));
    }

    @Test(expected = URISyntaxException.class)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addOrUpdateBadUrlShouldFail() throws RssAggregatorException, URISyntaxException {
        RssUrl rssUrl = new RssUrl(new URI(BAD_URL), 15);
        rssUrlService.addOrUpdate(rssUrl);
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addOrUpdateNullUrlShouldFail() throws RssAggregatorException, URISyntaxException {
        rssUrlService.addOrUpdate(null);
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void addOrUpdateUnauthenticatedShouldFail() throws RssAggregatorException, URISyntaxException {
        RssUrl rssUrl = new RssUrl(new URI(ANOTHER_COM_VALID_RSS), 15);
        rssUrlService.addOrUpdate(rssUrl);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void addOrUpdateAuthenticatedBadRoleShouldFail() throws RssAggregatorException, URISyntaxException {
        RssUrl rssUrl = new RssUrl(new URI(ANOTHER_COM_VALID_RSS), 15);
        rssUrlService.addOrUpdate(rssUrl);
    }



    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteNominalCase() throws RssAggregatorException, URISyntaxException {
        RssUrl rssUrl = new RssUrl(new URI(ANOTHER_COM_VALID_RSS), 15);
        rssUrlService.addOrUpdate(rssUrl);

        rssUrlService.delete(ANOTHER_COM_VALID_RSS);
        List<RssUrl> urls = rssUrlService.getAll();
        Assert.assertEquals(2, urls.size());
        Assert.assertFalse(urls.stream().anyMatch(url -> url.getUri().toString().equalsIgnoreCase("http://www.another.com/valid/rss")));
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteNonExistingShouldFail() throws RssAggregatorException {
        rssUrlService.delete("http://www.non-configured.com/valid/rss");
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteBadUrlShouldFail() throws RssAggregatorException {
        rssUrlService.delete(BAD_URL);
    }

    @Test(expected = RssAggregatorException.class)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteNullUrlShouldFail() throws RssAggregatorException {
        rssUrlService.delete(null);
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void deleteUnauthenticatedShouldFail() throws RssAggregatorException {
        rssUrlService.delete(ANOTHER_COM_VALID_RSS);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "john", roles = {"VIEWER"})
    public void deleteAuthenticatedBadRoleShouldFail() throws RssAggregatorException {
        rssUrlService.delete(ANOTHER_COM_VALID_RSS);
    }

}
