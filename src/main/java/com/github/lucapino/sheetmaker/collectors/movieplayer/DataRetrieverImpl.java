/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.movieplayer;

import com.github.lucapino.sheetmaker.collectors.DataRetriever;
import com.github.lucapino.sheetmaker.model.Artwork;
import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.github.lucapino.sheetmaker.model.tv.Serie;
import it.movieplayer.Images;
import it.movieplayer.Movieplayer;
import it.movieplayer.Poster_;
import it.movieplayer.Wallpaper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 *
 * @author Luca Tagliani
 */
public class DataRetrieverImpl implements DataRetriever {

    public static String MP_API_KEY = System.getProperty("mp.api.key");
    public static String POSTER_PREVIEW_SIZE = "w92";
    public static String BACKDROP_PREVIEW_SIZE = "w300";
    public static String POSTER_SIZE = "original";
    public static String BACKDROP_SIZE = "original";

    private final Client client;
    private final WebTarget target;

    public DataRetrieverImpl() {
        ClientConfig cc = new ClientConfig().register(new JacksonFeature());
        client = ClientBuilder.newClient(cc);
//        client = ClientBuilder.newClient();
        target = client.target("http://movieplayer.it/api/v1/");
    }

    @Override
    public Map getOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "Movieplayer.it";
    }

    @Override
    public Movie retrieveMovieFromImdbID(String imdbID, String language) {
        Movie movieInfo = null;
        Movieplayer movieData = target.path("movie/" + imdbID)
                .queryParam("api_key", MP_API_KEY)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Movieplayer.class);
        if (movieData != null) {
            movieInfo = new MovieImpl(movieData);
        }
        return movieInfo;
    }

    @Override
    public List<Movie> retrieveMoviesFromTitle(String title, String language) {
        List<Movie> movieInfoList = new ArrayList<>();
        Map<String, List<Movieplayer>> movieDataMap = target.path("movie/search")
                .queryParam("q", title)
                .queryParam("api_key", MP_API_KEY)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<Map<String, List<Movieplayer>>>() {
                });
        if (!movieDataMap.isEmpty()) {
            // if present there is only one instance
            List<Movieplayer> movieDataList = movieDataMap.values().iterator().next();
            for (Movieplayer movieData : movieDataList) {
                movieInfoList.add(new MovieImpl(movieData));
            }
        }
        return movieInfoList;
    }

    @Override
    public Serie retrieveTvSerieFromImdbID(String imdbID, String language) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Serie> retrieveTvSerieFromName(String name, String language) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Artwork> getPosters(String imdbID) {
        return getImages(imdbID, "poster");
    }

    @Override
    public List<Artwork> getBackdrops(String imdbID) {
        return getImages(imdbID, "wallpaper");
    }

    private List<Artwork> fillArtwork(Movieplayer movieData, String artworkType) {
        List<Artwork> artworks = new ArrayList<>();
        Images images = movieData.getImages();
        switch (artworkType) {
            case "poster":
                for (Poster_ poster : images.getPoster()) {
                    artworks.add(new PosterArtworkImpl(poster));
                }
                break;
            case "backdrop":
                for (Wallpaper wallpaper : images.getWallpaper()) {
                    artworks.add(new BackdropArtworkImpl(wallpaper));
                }
                break;
        }

        return artworks;
    }

    private List<Artwork> getImages(String imdbID, String artworkType) {
        List<Artwork> result = new ArrayList<>();
        Movieplayer movieData = target.path("movie/" + imdbID)
                .queryParam("api_key", MP_API_KEY)
                .queryParam("type", artworkType)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Movieplayer.class);
        if (movieData != null) {
            result = fillArtwork(movieData, artworkType);
        }
        return result;
    }

}
