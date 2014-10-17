/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.model.movie;

import com.github.lucapino.sheetmaker.model.Artwork;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public interface Movie {

    // Inglorious Basterds
    String getTitle();

    // Inglourious Basterds
    String getOriginalTitle();

    // Some description of the movie
    String getPlot();

    // Some tagline
    String getTagline();

    // 2009
    String getYear();

    // [John Wayne, Jane Fonda]
    List<String> getActors();

    // [Comedy, Drama]
    List<String> getGenres();

    // [Martin Scorsese]
    List<String> getDirectors();

    // PG-13
    String getCertification();

    // 23/08/2009 -> format using options
    String getReleaseDate();

    // Rated XYZ for abc
    String getMPAA();
    
    // tt1186370
    String getImdbId();
    
    // [Germany, USA]
    List<String> getCountries();
    
    // [Warner Bros., Pixar]
    List<String> getStudios();
    
    // 68 (/100)
    String getRatingPercent();
    
    // 134
    String getRuntime();

    // images
    
    List<Artwork> getPosters();
    
    List<Artwork> getBackdrops();
}
