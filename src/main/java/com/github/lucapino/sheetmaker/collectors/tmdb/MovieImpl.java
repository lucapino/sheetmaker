/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.tmdb;

import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.PersonCast;
import com.omertron.themoviedbapi.model.PersonCrew;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public class MovieImpl implements Movie {

    private final MovieDb movieData;

    public MovieImpl(MovieDb movieData) {
        this.movieData = movieData;
    }

    @Override
    public String getTitle() {
        return movieData.getTitle();
    }

    @Override
    public String getReleaseDate() {
        return movieData.getReleaseDate();
    }

    @Override
    public String getTagline() {
        return movieData.getTagline();
    }

    @Override
    public String getPlot() {
        return movieData.getOverview();
    }

    @Override
    public String getDuration() {
        return "" + movieData.getRuntime();
    }

    @Override
    public List<String> getGenres() {
        List<String> results = new ArrayList<>();
        List<Genre> genres = movieData.getGenres();
        for (Genre genre : genres) {
            results.add(genre.getName());
        }
        return results;
    }

    @Override
    public List<String> getActors() {
        List<String> results = new ArrayList<>();
        List<PersonCast> actors = movieData.getCast();
        for (PersonCast actor : actors) {
            results.add(actor.getName());
        }
        return results;
    }

    @Override
    public List<String> getDirectors() {
        List<String> results = new ArrayList<>();
        List<PersonCrew> persons = movieData.getCrew();
        for (PersonCrew person : persons) {
            if (person.getJob().toLowerCase().equals("director")) {
                results.add(person.getName());
            }
        }
        return results;
    }

    @Override
    public String getId() {
        return "" + movieData.getId();
    }

    @Override
    public String getImdbId() {
        return movieData.getImdbID();
    }

    @Override
    public String getOriginalTitle() {
        return movieData.getOriginalTitle();
    }

}
