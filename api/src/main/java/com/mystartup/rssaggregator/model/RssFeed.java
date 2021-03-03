package com.mystartup.rssaggregator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@ToString(includeFieldNames = false)
@XmlRootElement(name = "rss")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class RssFeed {
    private Channel channel;
}
