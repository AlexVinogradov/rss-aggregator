package com.mystartup.rssaggregator.model;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CustomTagAdapter extends XmlAdapter<Element, CustomTag> {

    @Override
    public Element marshal(CustomTag key) {
        //Marshaling not needed for current RssAggregator functionalities
        throw new UnsupportedOperationException();
    }

    @Override
    public CustomTag unmarshal(Element element) {
        return new CustomTag(element.getLocalName(), element.getTextContent());
    }


}
