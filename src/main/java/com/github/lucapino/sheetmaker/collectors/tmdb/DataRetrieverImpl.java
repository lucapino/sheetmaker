/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.tmdb;

import com.github.lucapino.sheetmaker.collectors.DataRetriever;
import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.github.lucapino.sheetmaker.model.tv.Serie;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResults;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Luca Tagliani
 */
public class DataRetrieverImpl implements DataRetriever {

    // TODO: manage external key
    public static String TMDB_API_KEY = System.getProperty("tmdb.api.key");
    public static String POSTER_SIZE = "w92"; // "original";
    public static String BACKDROP_SIZE = "w300"; // "original";

    private TmdbApi api;
    private String baseUrl;


    public DataRetrieverImpl() throws Exception {
        // use themoviedb api to get info about movie
        api = new TmdbApi(TMDB_API_KEY);
        baseUrl = api.getConfiguration().getBaseUrl();
        Workbook wb = new HSSFWorkbook(new FileInputStream(new File("/home/tagliani/tmp/HD-report.xls")));
        Sheet sheet = wb.getSheet("HD1");
        int lastRow = sheet.getLastRowNum();
        for (int i = 0; i < lastRow; i++) {
            Row row = sheet.getRow(i);
            String movieName = row.getCell(0).getStringCellValue();
            retrieve(movieName);
        }
    }

    private void retrieve(String movieName) throws Exception {

        File folder = new File("/home/tagliani/tmp/movies/" + movieName);
        if (!folder.exists()) {
            folder.mkdir();
            TmdbSearch search = api.getSearch();
            MovieResults results = search.searchMovie(movieName, 0, null, false, 0);
            List<MovieDb> movies = results.getResults();
            for (MovieDb movie : movies) {
                int movieId = movie.getId();
                File subfolder = new File(folder, "" + movieId);
                subfolder.mkdir();
                // get poster and save it
                MovieDb innerMovie = api.getMovies().getMovie(movieId, null, TmdbMovies.MovieMethod.values());

                List<Artwork> artworks = innerMovie.getImages();
                for (Artwork artwork : artworks) {
                    File bdFolder = new File(subfolder, "backdrops");
                    bdFolder.mkdir();
                    File psFolder = new File(subfolder, "posters");
                    psFolder.mkdir();
                    switch (artwork.getArtworkType()) {
                        case BACKDROP:
                            URL backdropURL = new URL(baseUrl + BACKDROP_SIZE + artwork.getFilePath());
                            IOUtils.copyLarge(backdropURL.openStream(), new FileOutputStream(bdFolder.getPath() + artwork.getFilePath()));
                            break;
                        case POSTER:
                            URL posterURL = new URL(baseUrl + POSTER_SIZE + artwork.getFilePath());
                            IOUtils.copyLarge(posterURL.openStream(), new FileOutputStream(psFolder.getPath() + artwork.getFilePath()));
                            break;
                    }
                }
                System.out.println(innerMovie.toString());
            }
        }
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
}
