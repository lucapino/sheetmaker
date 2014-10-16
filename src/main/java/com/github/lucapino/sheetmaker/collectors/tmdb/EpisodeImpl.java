/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.tmdb;

import com.github.lucapino.sheetmaker.model.tv.Episode;
import com.github.lucapino.sheetmaker.model.tv.Season;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.person.PersonCast;
import com.omertron.themoviedbapi.model.person.PersonCrew;
import com.omertron.themoviedbapi.model.tv.TVCredits;
import com.omertron.themoviedbapi.model.tv.TVEpisode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public class EpisodeImpl implements Episode {

    private final TVEpisode episode;
    private final Season season;
    private final TheMovieDbApi api;

    public EpisodeImpl(TVEpisode episode, Season season, TheMovieDbApi api) {
        this.episode = episode;
        this.season = season;
        this.api = api;
    }

    @Override
    public String getTitle() {
        return episode.getName();
    }

    @Override
    public int getNumber() {
        return episode.getEpisodeNumber();
    }

    @Override
    public String getOverview() {
        return episode.getOverview();
    }

    @Override
    public List<String> getActors() {
        List<String> actors = new ArrayList<>();
        try {
            TVCredits credits = api.getTvEpisodeCredits(season.getSerie().getId(), episode.getSeasonNumber(), episode.getEpisodeNumber(), null);
            List<PersonCast> cast = credits.getCast();
            for (PersonCast person : cast) {
                actors.add(person.getName());
            }
        } catch (MovieDbException ex) {
            ex.printStackTrace();
        }
        return actors;
    }

    @Override
    public List<String> getDirectors() {
        List<String> directors = new ArrayList<>();
        try {
            TVCredits credits = api.getTvEpisodeCredits(season.getSerie().getId(), episode.getSeasonNumber(), episode.getEpisodeNumber(), null);
            List<PersonCrew> crew = credits.getCrew();
            for (PersonCrew person : crew) {
                if (person.getJob().toLowerCase().equals("director")) {
                    directors.add(person.getName());
                }
            }
        } catch (MovieDbException ex) {
            ex.printStackTrace();
        }
        return directors;
    }

    @Override
    public List<String> getGuestStars() {
        List<String> guestStars = new ArrayList<>();
        try {
            TVCredits credits = api.getTvEpisodeCredits(season.getSerie().getId(), episode.getSeasonNumber(), episode.getEpisodeNumber(), null);
            List<PersonCast> guests = credits.getGuestStar();
            for (PersonCast person : guests) {
                guestStars.add(person.getName());
            }
        } catch (MovieDbException ex) {
            ex.printStackTrace();
        }
        return guestStars;
    }

    @Override
    public int getId() {
        return episode.getId();
    }

    @Override
    public Season getSeason() {
        return season;
    }

}
