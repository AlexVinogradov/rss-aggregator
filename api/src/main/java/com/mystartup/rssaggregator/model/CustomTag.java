package com.mystartup.rssaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Setter
@Getter
@AllArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value = CustomTagAdapter.class)
public class CustomTag {
    private String key;
    private String value;

}