/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.model.tv;

import com.github.lucapino.sheetmaker.model.Person;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public interface Episode {

    String getTitle();

    int getNumber();

    String getOverview();

    List<Person> getCast();

    List<Person> getDirectors();

    // TODO: add images
}
