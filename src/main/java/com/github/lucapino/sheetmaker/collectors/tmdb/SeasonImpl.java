/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.tmdb;

import com.github.lucapino.sheetmaker.model.tv.Episode;
import com.github.lucapino.sheetmaker.model.tv.Season;
import com.github.lucapino.sheetmaker.model.tv.Serie;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.tv.TVSeason;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public class SeasonImpl implements Season {

    private final TVSeason season;
    private final TheMovieDbApi api;
    private final Serie serie;

    public SeasonImpl(TVSeason season, Serie serie, TheMovieDbApi api) {
        this.season = season;
        this.api = api;
        this.serie = serie;
    }

    @Override
    public Serie getSerie() {
        return serie;
    }

    @Override
    public String getName() {
        return season.getName();
    }

    @Override
    public int getNumber() {
        return season.getSeasonNumber();
    }

    @Override
    public List<Episode> getEpisodes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getEpisodesNumber() {
        return season.getEpisodes().size();
    }

    @Override
    public List<String> getEpisodeTitles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Episode getEpisode(int number) {
        Episode episode = null;
        try {
            episode = new EpisodeImpl(api.getTvEpisode(serie.getId(), season.getSeasonNumber(), number, null), this, api);
        } catch (MovieDbException ex) {
            // TODO: add logger
            ex.printStackTrace();
        }
        return episode;
    }

    @Override
    public Episode getEpisode(String episodeTitle) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getOverview() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getId() {
        return season.getId();
    }

}
