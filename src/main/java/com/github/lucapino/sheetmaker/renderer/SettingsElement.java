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
public class SettingsElement {

    private static final String SEPARATOR = "Separator";
    private static final String MAXIMUM_VALUES = "MaximumValues";

    private final String name;
    private final String separator;
    private final String maximumValues;

    public SettingsElement(Element settingsElement) {
        this.name = settingsElement.getName();
        this.separator = settingsElement.getAttributeValue(SEPARATOR);
        this.maximumValues = settingsElement.getAttributeValue(MAXIMUM_VALUES);
    }

    public String getName() {
        return name;
    }

    public String getSeparator() {
        return separator;
    }

    public String getMaximumValues() {
        return maximumValues;
    }

}
