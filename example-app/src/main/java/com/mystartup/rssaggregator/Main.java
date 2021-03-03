package com.mystartup.rssaggregator;

import com.mystartup.rssaggregator.model.Item;
import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws RssAggregatorException, URISyntaxException {

        UsernamePasswordAuthenticationToken credentials =
                new UsernamePasswordAuthenticationToken("admin", "admin123");

        BootstrapService service = new BootstrapService(credentials);

        List<Item> retrievedItems = service.getRssReaderService().searchFeeds("Science");

        System.out.println(retrievedItems);

        service.getRssConfigurator().addOrUpdate(new RssUrl(new URI("https://www.nicematin.com/ville/sophia-antipolis/rss"), 1));

        service.getRssReaderService().readPeriodically();
        
        service.getRssConfigurator().getAll();

    }


}
