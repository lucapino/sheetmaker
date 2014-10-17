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
    // retrieve from IMDB id
    Movie retrieveMovieFromImdbID(String imdbID, String language);

    // retrieve from name
    List<Movie> retrieveMoviesFromTitle(String title, String language);

    // TV Serie
    // retrieve from IMDB id
    Serie retrieveTvSerieFromImdbID(String imdbID, String language);

    // retrieve from name
    List<Serie> retrieveTvSerieFromName(String name, String language);
    
    

}
