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
import com.omertron.themoviedbapi.model.ProductionCompany;
import com.omertron.themoviedbapi.model.ProductionCountry;
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
    public String getImdbId() {
        return movieData.getImdbID();
    }

    @Override
    public String getOriginalTitle() {
        return movieData.getOriginalTitle();
    }

    @Override
    public String getYear() {
        return movieData.getReleaseDate().split("-")[0];
    }

    @Override
    public String getCertification() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMPAA() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getCountries() {
        List<String> results = new ArrayList<>();
        for (ProductionCountry productionCountry : movieData.getProductionCountries()) {
            results.add(productionCountry.getName());
        }
        return results;
    }

    @Override
    public List<String> getStudios() {
        List<String> results = new ArrayList<>();
        for (ProductionCompany productionCompany : movieData.getProductionCompanies()) {
            results.add(productionCompany.getName());
        }
        return results;
    }

    @Override
    public String getRatingPercent() {
        return "" + (movieData.getVoteAverage() * 10);
    }

    @Override
    public String getRuntime() {
        return "" + movieData.getRuntime();
    }
}
