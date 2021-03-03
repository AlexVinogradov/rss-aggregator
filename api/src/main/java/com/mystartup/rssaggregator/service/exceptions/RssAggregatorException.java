package com.mystartup.rssaggregator.service.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RssAggregatorException extends Exception {
    private static final long serialVersionUID = 1L;
    public RssAggregatorException(String errorMessage) {
        super(errorMessage);
    }
}
