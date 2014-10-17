/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.tmdb;

import static com.github.lucapino.sheetmaker.collectors.tmdb.DataRetrieverImpl.CONFIGURATION;
import com.github.lucapino.sheetmaker.model.Artwork;
import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.Certification;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.ProductionCompany;
import com.omertron.themoviedbapi.model.ProductionCountry;
import com.omertron.themoviedbapi.model.ReleaseInfo;
import com.omertron.themoviedbapi.model.movie.MovieDb;
import com.omertron.themoviedbapi.model.person.PersonCast;
import com.omertron.themoviedbapi.model.person.PersonCrew;
import com.omertron.themoviedbapi.model.type.ArtworkType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public class MovieImpl implements Movie {

    private final MovieDb movieData;
    private final List<Certification> certificationList;
    private final TheMovieDbApi api;

    public MovieImpl(MovieDb movieData, List<Certification> certificationList, TheMovieDbApi api) {
        this.movieData = movieData;
        this.certificationList = certificationList;
        this.api = api;
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
        // retrieve release in USA and get certification
        List<ReleaseInfo> releases = movieData.getReleases();
        for (ReleaseInfo release : releases) {
            if (release.getCountry().equals("US")) {
                return release.getCertification();
            }
        }
        return "";
    }

    @Override
    public String getMPAA() {
        String certificationCode = getCertification();
        for (Certification certification : certificationList) {
            if (certification.getCertification().equals(certificationCode)) {
                return certification.getMeaning();
            }
        }
        return "";
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
    
    @Override
    public List<Artwork> getPosters() {
        return getArtworks(getImdbId(), ArtworkType.POSTER);
    }

    @Override
    public List<Artwork> getBackdrops() {
        return getArtworks(getImdbId(), ArtworkType.BACKDROP);
    }

    private List<Artwork> getArtworks(String imdbID, ArtworkType artworkType) {
        List<Artwork> artworks = new ArrayList<>();
        try {
            MovieDb movie = api.getMovieInfoImdb(imdbID, null, "images");
            if (movie != null) {
                for (com.omertron.themoviedbapi.model.Artwork image : movie.getImages()) {
                    if (image.getArtworkType() == artworkType) {
                        switch (artworkType) {
                            case POSTER:
                                artworks.add(new PosterArtworkImpl(CONFIGURATION.getBaseUrl(), image));
                                break;
                            case BACKDROP:
                                artworks.add(new BackdropArtworkImpl(CONFIGURATION.getBaseUrl(), image));
                                break;
                        }
                    }
                }
            }
        } catch (MovieDbException ex) {
            // TODO: add logger
            ex.printStackTrace();
        }
        return artworks;
    }
}
