/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.model.movie;

import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public interface Movie {

    String getTitle();

    String getReleaseDate();

    String getTagline();

    String getPlot();

    String getDuration();

    List<String> getGenres();

    List<String> getActors();

    List<String> getDirectors();

    String getId();

    String getImdbId();

    String getOriginalTitle();
    
    Float getRating();

}
