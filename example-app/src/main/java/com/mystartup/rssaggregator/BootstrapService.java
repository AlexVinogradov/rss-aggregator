package com.mystartup.rssaggregator;

import com.mystartup.rssaggregator.service.RssReaderService;
import com.mystartup.rssaggregator.service.RssUrlService;
import lombok.Getter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


@Getter
public class BootstrapService {
    private final RssReaderService rssReaderService;
    private final RssUrlService rssConfigurator;
    private final AuthenticationManager authenticationManager;
    private final ConfigurableApplicationContext context;

    public BootstrapService(UsernamePasswordAuthenticationToken userDetails) {
        context = getAnnotationConfigApplicationContext();
        authenticationManager = context.getBean(AuthenticationManager.class);
        rssReaderService = context.getBean(RssReaderService.class);
        rssConfigurator = context.getBean(RssUrlService.class);
        login(userDetails);
    }

    private AnnotationConfigApplicationContext getAnnotationConfigApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(MainConfig.class);
        context.refresh();
        return context;
    }

    private void login(UsernamePasswordAuthenticationToken userDetails) {
        Authentication auth = authenticationManager.authenticate(userDetails);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
    }
}
