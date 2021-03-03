package com.mystartup.rssaggregator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class Item {

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "guid")
    private String guid;

    @XmlElement(name = "link")
    private String link;

    @XmlElement(name = "author")
    private String author;

    @XmlElement(name = "category")
    private List<String> category;

    @XmlElement(name = "pubDate")
    private String pubDate;

    @XmlElement(name = "enclosure")
    private String enclosure;

    @XmlElement(name = "comments")
    private List<String> comments;

    @XmlElement(name = "channel")
    private Channel channel;

    @XmlAnyElement
    private List<CustomTag> customTags;

    /**
     * Used to retrieve the String containing the searchable values.
     * This can be used to exclude/include a specific value
     *
     * @return the string containing the item's searchable values.
     */
    public String getSearchValues() {
        List<String> customTagValues = getCustomTags().stream().map(CustomTag::getValue).collect(Collectors.toList());

        Stream<String> valuesIncludedForSearch = Stream.of(getTitle(), getDescription(), getGuid(), getLink(),
                getAuthor(), String.valueOf(getCategory()), getPubDate(), getEnclosure(), String.valueOf(getComments()),
                String.valueOf(getChannel()), String.valueOf(customTagValues));

        return valuesIncludedForSearch
                .filter(fieldValue -> fieldValue != null && !fieldValue.isEmpty() && !fieldValue.equals("null"))
                .collect(Collectors.joining(","));
    }
}
