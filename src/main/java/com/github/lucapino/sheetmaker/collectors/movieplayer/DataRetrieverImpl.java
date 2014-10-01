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
import it.movieplayer.Movieplayer;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Luca Tagliani
 */
public class DataRetrieverImpl implements DataRetriever {

    public static String MP_API_KEY = System.getProperty("mp.api.key");
    public static String POSTER_SIZE = "w92"; // "original";
    public static String BACKDROP_SIZE = "w300"; // "original";

    public void retrieveMovieplayer(String name) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://movieplayer.it/api/v1/");
        Movieplayer data = target.path("movie/search")
                .queryParam("q", name)
                .queryParam("api_key", MP_API_KEY)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Movieplayer.class);
        System.out.println(data);

    }

    @Override
    public Map getOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Movie retrieveMovieFromId(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Movie> retrieveMoviesFromTitle(String title) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Serie retrieveTvSerieFromId(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Serie> retrieveTvSerieFromName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Artwork> getPosters(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Artwork> getBackdrops(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
