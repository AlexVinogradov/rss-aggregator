package com.mystartup.rssaggregator.service;

import com.mystartup.rssaggregator.configuration.RssAggregatorConfig;
import com.mystartup.rssaggregator.model.RssUrl;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class IntegrationTestSpringConfig extends RssAggregatorConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void securityConfiguration(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder().encode("admin123")).roles("USER", "ADMIN");
    }

    @Bean
    @Override
    public RssUrlService rssUrlService() throws RssAggregatorException, URISyntaxException {
        ArrayList<RssUrl> rssUrlList = new ArrayList<>();
        rssUrlList.add(new RssUrl(new URI("https://www.techrepublic.com/rssfeeds/articles"), 10));
        rssUrlList.add(new RssUrl(new URI("https://www.wired.com/feed/category/science/latest/rss"), 5));
        return new DefaultRssUrlServiceImpl(rssUrlList);
    }
}
