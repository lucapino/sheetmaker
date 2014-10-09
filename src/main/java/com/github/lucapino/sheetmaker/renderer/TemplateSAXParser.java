/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Luca Tagliani
 */
public class TemplateSAXParser extends DefaultHandler {

    private String tabs = "";

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attributes)
            throws SAXException {
        tabs += "    ";
        String msg = tabs + "Start " + qName + " -> ";
        for (int i = 0; i < attributes.getLength(); i++) {
            msg += "\"" + attributes.getQName(i) + "\" = '" + attributes.getValue(i) + "' ";
        }
        System.out.println(msg);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        System.out.println(tabs + "End " + qName);
        tabs = tabs.substring(4);
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
//        System.out.println("Characters: " + Arrays.toString(ch));
        String value = new String(ch, start, length).trim();
        if (value.length() > 0) {
            System.out.println("value -> " + value);
        }
    }

    @Override
    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
    }

}
