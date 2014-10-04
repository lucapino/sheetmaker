/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.tmdb;

import com.github.lucapino.sheetmaker.collectors.DataRetriever;
import com.github.lucapino.sheetmaker.model.Artwork;
import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.github.lucapino.sheetmaker.model.tv.Serie;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.ArtworkType;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.TmdbConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luca Tagliani
 */
public class DataRetrieverImpl implements DataRetriever {

    // TODO: manage external key
    public static String TMDB_API_KEY = System.getProperty("tmdb.api.key");
    public static String POSTER_THUMB_SIZE = "w92";
    public static String POSTER_SIZE = "original";
    public static String BACKDROP_THUMB_SIZE = "w300";
    public static String BACKDROP_SIZE = "original";

    private final TheMovieDbApi api;
    private final TmdbConfiguration configuration;

//    public static void main(String[] args) throws Exception {
//        DataRetrieverImpl app = new DataRetrieverImpl();
//        app.retrieve("L'era glaciale");
//    }
    public DataRetrieverImpl() throws Exception {
        // use themoviedb api to get info about movie
        api = new TheMovieDbApi(TMDB_API_KEY);
        configuration = api.getConfiguration();
//        Workbook wb = new HSSFWorkbook(new FileInputStream(new File("/home/tagliani/tmp/HD-report.xls")));
//        Sheet sheet = wb.getSheet("HD1");
//        int lastRow = sheet.getLastRowNum();
//        for (int i = 0; i < lastRow; i++) {
//            Row row = sheet.getRow(i);
//            String movieName = row.getCell(0).getStringCellValue();
//            retrieve(movieName);
//        }
    }

//    private void retrieve(String movieName) throws Exception {
//
//        File folder = new File("/home/tagliani/tmp/movies/" + movieName);
//        if (!folder.exists()) {
//            folder.mkdir();
//            TmdbResultsList<MovieDb> results = api.searchMovie(movieName, 0, null, false, 0);
//            List<MovieDb> movies = results.getResults();
//            for (MovieDb movie : movies) {
//                int movieId = movie.getId();
//                File subfolder = new File(folder, "" + movieId);
//                subfolder.mkdir();
//                // get poster and save it
//                
//                TmdbResultsList<Artwork> artworks = api.getMovieImages(movieId, null);
//                for (Artwork artwork : artworks.getResults()) {
//                    File bdFolder = new File(subfolder, "backdrops");
//                    bdFolder.mkdir();
//                    File psFolder = new File(subfolder, "posters");
//                    psFolder.mkdir();
//                    switch (artwork.getArtworkType()) {
//                        case BACKDROP:
//                            URL backdropURL = new URL(baseUrl + BACKDROP_SIZE + artwork.getFilePath());
//                            IOUtils.copyLarge(backdropURL.openStream(), new FileOutputStream(bdFolder.getPath() + artwork.getFilePath()));
//                            break;
//                        case POSTER:
//                            URL posterURL = new URL(baseUrl + POSTER_SIZE + artwork.getFilePath());
//                            IOUtils.copyLarge(posterURL.openStream(), new FileOutputStream(psFolder.getPath() + artwork.getFilePath()));
//                            break;
//                    }
//                }
//                System.out.println(movie.toString());
//            }
//        }
//    }
    @Override
    public Map getOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "TheMovieDB";
    }

    @Override
    public Movie retrieveMovieFromImdbID(String imdbID, String language) {
        MovieImpl movieInfo = null;
        try {
            MovieDb movieData = api.getMovieInfoImdb(imdbID, language);
            if (movieData != null) {
                movieInfo = new MovieImpl(movieData);
            }
        } catch (MovieDbException ex) {
            // TODO: add logger
            ex.printStackTrace();
        }
        return movieInfo;
    }

    @Override
    public List<Movie> retrieveMoviesFromTitle(String title, String language) {
        List<Movie> results = new ArrayList<>();
        try {
            List<MovieDb> movies = api.searchMovie(title, 0, language, false, 0).getResults();
            for (MovieDb movie : movies) {
                // we need to use this api to retrieve the full info
                results.add(new MovieImpl(api.getMovieInfo(movie.getId(), language)));
            }
        } catch (MovieDbException ex) {
            // TODO: add logger
            ex.printStackTrace();
        }
        return results;
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
        return getArtworks(imdbID, ArtworkType.POSTER);
    }

    @Override
    public List<Artwork> getBackdrops(String imdbID) {
        return getArtworks(imdbID, ArtworkType.BACKDROP);
    }
    
    private List<Artwork> getArtworks(String imdbID, ArtworkType artworkType) {
        List<Artwork> artworks = new ArrayList<>();
        try {
            MovieDb movieData = api.getMovieInfoImdb(imdbID, null);
            if (movieData != null) {
                List<com.omertron.themoviedbapi.model.Artwork> images = api.getMovieImages(movieData.getId(), null).getResults();
                for (com.omertron.themoviedbapi.model.Artwork image : images) {
                    if (image.getArtworkType() == artworkType) {
                        switch(artworkType) {
                            case POSTER:
                                artworks.add(new PosterArtworkImpl(configuration.getBaseUrl(), image));
                                break;
                            case BACKDROP:
                                artworks.add(new BackdropArtworkImpl(configuration.getBaseUrl(), image));
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
