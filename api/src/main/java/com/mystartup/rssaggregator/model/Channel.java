package com.mystartup.rssaggregator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@ToString
@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.NONE)
public class Channel {

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "language")
    private String language;

    @XmlElement(name = "link")
    private String link;

    @XmlElement(name = "copyright")
    private String copyright;

    @XmlElement(name = "category")
    private List<String> categories;

    @XmlElement(name = "lastBuildDate")
    private String lastBuildDate;

    @XmlElement(name = "pubDate")
    private String pubDate;

    @XmlElement(name = "webMaster")
    private String webMaster;

    @XmlElement(name = "ttl")
    private Integer ttl;

    @XmlElement(name = "item")
    private List<Item> items;

}
