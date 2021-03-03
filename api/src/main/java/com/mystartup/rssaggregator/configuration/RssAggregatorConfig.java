package com.mystartup.rssaggregator.configuration;

import com.mystartup.rssaggregator.model.CustomTagAdapter;
import com.mystartup.rssaggregator.service.DefaultRssReaderServiceImpl;
import com.mystartup.rssaggregator.service.DefaultRssUrlServiceImpl;
import com.mystartup.rssaggregator.service.RssReaderService;
import com.mystartup.rssaggregator.service.RssUrlService;
import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Configuration
@ComponentScan("com.mystartup.rssaggregator")
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class RssAggregatorConfig extends GlobalMethodSecurityConfiguration {

    @Bean
    public RssReaderService rssReaderService() throws URISyntaxException, RssAggregatorException, MalformedURLException {
        return new DefaultRssReaderServiceImpl(rssUrlService(), unmarshaller());
    }

    @Bean
    public RssUrlService rssUrlService() throws RssAggregatorException, MalformedURLException, URISyntaxException {
        return new DefaultRssUrlServiceImpl(new ArrayList<>());
    }

    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setPackagesToScan("com.mystartup.rssaggregator.model");
        unmarshaller.supports(RssReaderService.class);
        unmarshaller.setAdapters(new CustomTagAdapter());
        return unmarshaller;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }
}
