/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.tmdb;

import static com.github.lucapino.sheetmaker.collectors.tmdb.DataRetrieverImpl.CONFIGURATION;
import com.github.lucapino.sheetmaker.model.Artwork;
import com.github.lucapino.sheetmaker.model.tv.Season;
import com.github.lucapino.sheetmaker.model.tv.Serie;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.tv.TVSeason;
import com.omertron.themoviedbapi.model.tv.TVSeasonBasic;
import com.omertron.themoviedbapi.model.tv.TVSeries;
import com.omertron.themoviedbapi.model.type.ArtworkType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public class SerieImpl implements Serie {

    private final TVSeries serie;
    private final TheMovieDbApi api;
    
    SerieImpl(TVSeries serie, TheMovieDbApi api) {
        this.serie = serie;
        this.api = api;
    }

    @Override
    public String getName() {
        return serie.getName();
    }

    @Override
    public List<Season> getSeasons() {
        List<Season> seasons = new ArrayList<>();
        try {
            List<TVSeasonBasic> basicSeasons = serie.getSeasons();
            for (TVSeasonBasic basicSeason : basicSeasons) {
                seasons.add(new SeasonImpl(api.getTvSeason(serie.getId(), basicSeason.getSeasonNumber(), null), this, api));
            }
        } catch (MovieDbException ex) {
            // TODO: add logger
            ex.printStackTrace();
        }
        return seasons;
    }

    @Override
    public Season getSeason(int seasonNumber) {
        Season season = null;
        try {
            TVSeason tvSeason = api.getTvSeason(serie.getId(), seasonNumber, null);
            season = new SeasonImpl(tvSeason, this, api);
        } catch (MovieDbException ex) {
            // TODO: add logger
            ex.printStackTrace();
        }
        return season;
    }

    @Override
    public int getSeasonsNumber() {
        return serie.getNumberSeasons();
    }

    @Override
    public String getOverview() {
        return serie.getOverview();
    }

    @Override
    public int getId() {
        return serie.getId();
    }
    
    @Override
    public List<Artwork> getPosters() {
        return getArtworks(getId(), ArtworkType.POSTER);
    }

    @Override
    public List<Artwork> getBackdrops() {
        return getArtworks(getId(), ArtworkType.BACKDROP);
    }

    private List<Artwork> getArtworks(int id, ArtworkType artworkType) {
        List<Artwork> artworks = new ArrayList<>();
        try {
            List<com.omertron.themoviedbapi.model.Artwork> tmdbArtworks = api.getTvImages(id, null).getResults();
            if (tmdbArtworks != null) {
                for (com.omertron.themoviedbapi.model.Artwork image : tmdbArtworks) {
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
