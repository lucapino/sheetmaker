/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.movieplayer;

import com.github.lucapino.sheetmaker.model.Artwork;
import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.github.lucapino.sheetmaker.model.tv.Serie;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author tagliani
 */
public class DataRetrieverImplNGTest {

    DataRetrieverImpl instance;

    @BeforeClass
    public void setUpClass() throws Exception {
        instance = new DataRetrieverImpl();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of getOptions method, of class DataRetrieverImpl.
     */
    @Test(enabled = false)
    public void testGetOptions() {
        System.out.println("getOptions");
        Map expResult = null;
        Map result = instance.getOptions();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class DataRetrieverImpl.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "Movieplayer.it";
        String result = instance.getName();
        assertEquals(result, expResult);
    }

    /**
     * Test of retrieveMovieFromImdbID method, of class DataRetrieverImpl.
     */
    @Test
    public void testRetrieveMovieFromImdbID() {
        System.out.println("retrieveMovieFromImdbID");
        String imdbID = "tt0268380";
        String language = "it";
        Movie result = instance.retrieveMovieFromImdbID(imdbID, language);
        assertEquals(result.getTitle().toLowerCase(), "l'era glaciale");
        assertEquals(result.getImdbId(), "tt0268380");
        assertEquals(result.getId(), "65");
    }

    /**
     * Test of retrieveMoviesFromTitle method, of class DataRetrieverImpl.
     */
    @Test
    public void testRetrieveMoviesFromTitle() {
        System.out.println("retrieveMoviesFromTitle");
        String title = "l'era glaciale";
        String language = "it";
        List<Movie> results = instance.retrieveMoviesFromTitle(title, language);
        for (Movie result : results) {
            assertTrue(result.getTitle().toLowerCase().contains("l'era glaciale"));
        }
    }

    /**
     * Test of retrieveTvSerieFromImdbID method, of class DataRetrieverImpl.
     */
    @Test(enabled = false)
    public void testRetrieveTvSerieFromImdbID() {
        System.out.println("retrieveTvSerieFromImdbID");
        String imdbID = "";
        String language = "";
        DataRetrieverImpl instance = new DataRetrieverImpl();
        Serie expResult = null;
        Serie result = instance.retrieveTvSerieFromImdbID(imdbID, language);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of retrieveTvSerieFromName method, of class DataRetrieverImpl.
     */
    @Test(enabled = false)
    public void testRetrieveTvSerieFromName() {
        System.out.println("retrieveTvSerieFromName");
        String name = "";
        String language = "";
        DataRetrieverImpl instance = new DataRetrieverImpl();
        List expResult = null;
        List result = instance.retrieveTvSerieFromName(name, language);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPosters method, of class DataRetrieverImpl.
     */
    @Test(enabled = true)
    public void testGetPosters() {
        System.out.println("getPosters");
        String imdbID = "tt0268380";
        List<Artwork> result = instance.getPosters(imdbID);
        for (Artwork artwork : result) {
            System.out.println("Type : " + artwork.getType());
            System.out.println("Thumb: " + artwork.getThumbURL());
            System.out.println("Image: " + artwork.getImageURL());
        }
        
    }

    /**
     * Test of getBackdrops method, of class DataRetrieverImpl.
     */
    @Test(enabled = true)
    public void testGetBackdrops() {
        System.out.println("getBackdrops");
        String imdbID = "tt0268380";
        List<Artwork> result = instance.getBackdrops(imdbID);
        for (Artwork artwork : result) {
            System.out.println("Type : " + artwork.getType());
            System.out.println("Thumb: " + artwork.getThumbURL());
            System.out.println("Image: " + artwork.getImageURL());
        }
    }

}
