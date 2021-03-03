package com.mystartup.rssaggregator.model;

import com.mystartup.rssaggregator.service.exceptions.RssAggregatorException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.net.URI;

@Getter
@ToString
@EqualsAndHashCode
public class RssUrl {

    public RssUrl(URI uri, Integer refreshIntervalMinutes) throws RssAggregatorException {
        this.uri = uri;
        this.refreshIntervalMinutes = validatedRefreshInterval(refreshIntervalMinutes);
    }

    private URI uri;
    private Integer refreshIntervalMinutes;

    public RssUrl(URI uri) {
        this.uri = uri;
        this.refreshIntervalMinutes = 1;
    }

    private Integer validatedRefreshInterval(final Integer interval) throws RssAggregatorException {
        if(interval <= 0){
            throw new RssAggregatorException("Invalid refresh interval (minutes). Please give a integer value greater than 0");
        }
        return interval;
    }
}
