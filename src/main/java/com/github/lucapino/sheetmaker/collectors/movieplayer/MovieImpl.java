/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.movieplayer;

import com.github.lucapino.sheetmaker.model.movie.Movie;
import it.movieplayer.Movieplayer;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public class MovieImpl implements Movie {

    Movieplayer movieData;

    public MovieImpl(Movieplayer movieData) {
        this.movieData = movieData;
    }

    @Override
    public String getTitle() {
        return movieData.getName();
    }

    @Override
    public String getReleaseDate() {
        return "";
    }

    @Override
    public String getTagline() {
        return "";
    }

    @Override
    public String getPlot() {
        return movieData.getPlot();
    }

    @Override
    public String getDuration() {
        return movieData.getDuration();
    }

    @Override
    public List<String> getGenres() {
        return movieData.getGenres();
    }

    @Override
    public List<String> getActors() {
        return movieData.getActors();
    }

    @Override
    public List<String> getDirectors() {
        return movieData.getDirectors();
    }

    @Override
    public String getId() {
        return movieData.getId();
    }

    @Override
    public String getImdbId() {
        return movieData.getImdbid();
    }

    @Override
    public String getOriginalTitle() {
        return movieData.getOriginal();
    }

}
