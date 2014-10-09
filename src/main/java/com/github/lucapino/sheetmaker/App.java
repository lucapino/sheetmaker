/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker;

import com.github.lucapino.sheetmaker.renderer.TemplateSAXParser;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Luca Tagliani
 */
public class App {

    public static void main(String[] args) {
        App app = new App();
        app.parse();
    }

    public void parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {

            InputStream xmlInput = this.getClass().getResourceAsStream("/templates/default/Template.xml");
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new TemplateSAXParser();
            saxParser.parse(xmlInput, handler);

        } catch (Throwable err) {
            err.printStackTrace();
        }

    }
}
