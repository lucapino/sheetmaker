/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

import org.jdom2.Element;

/**
 *
 * @author tagliani
 */
public class TemplateElement {

    private static final String NAME = "Name";
    private static final String TEXT = "Text";
    private static final String IMAGE = "Image";

    private final String name;
    private final String text;
    private final String image;

    TemplateElement(Element child) {
        this.name = child.getAttributeValue(NAME);
        this.text = child.getAttributeValue(TEXT);
        this.image = child.getAttributeValue(IMAGE);
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }

}
