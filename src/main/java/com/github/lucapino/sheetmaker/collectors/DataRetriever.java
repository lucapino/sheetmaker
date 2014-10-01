/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors;

import com.github.lucapino.sheetmaker.model.Artwork;
import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.github.lucapino.sheetmaker.model.tv.Serie;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luca Tagliani
 */
public interface DataRetriever {

    Map getOptions();

    String getName();

    // Movie
    // retrieve from id
    Movie retrieveMovieFromId(String id);

    // retrieve from name
    List<Movie> retrieveMoviesFromTitle(String title);

    // TV Serie
    // retrieve from id
    Serie retrieveTvSerieFromId(String id);

    // retrieve from name
    List<Serie> retrieveTvSerieFromName(String name);
    
    // images
    
    List<Artwork> getPosters(String id);
    
    List<Artwork> getBackdrops(String id);

}
