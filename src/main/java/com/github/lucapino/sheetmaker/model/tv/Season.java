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
public interface Season {

    Serie getSerie();
    
    String getName();

    int getNumber();

    List<Episode> getEpisodes();

    int getEpisodeNumber();

    List<String> getEpisodeTitles();

    Episode getEpisode(int number);

    Episode getEpisode(String episodeTitle);
    
    String getOverview();

    List<Person> getCast();
    
    List<Person> getDirectors();

    // TODO: add images
}
