/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

import java.util.Map;
import org.jdom2.Element;

/**
 *
 * @author tagliani
 */
public class TemplateSettings {

    private static final String RATING = "Rating";
    private static final String RATING_FILENAME = "FileName";

    private Map<String, SettingsElement> settingsElements;
    private String starsRatingFilename;

    public TemplateSettings(Element settingsElement) {
        for (Element setting : settingsElement.getChildren()) {
            SettingsElement element = new SettingsElement(setting);
            settingsElements.put(setting.getName(), element);
        }
        starsRatingFilename = settingsElement.getChild(RATING).getAttributeValue(RATING_FILENAME);
    }

    public Map<String, SettingsElement> getSettingsElements() {
        return settingsElements;
    }

    public String getStarsRating() {
        return starsRatingFilename;
    }

}
