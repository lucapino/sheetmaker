/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.tmdb;

import static com.github.lucapino.sheetmaker.collectors.tmdb.DataRetrieverImpl.POSTER_SIZE;
import static com.github.lucapino.sheetmaker.collectors.tmdb.DataRetrieverImpl.POSTER_THUMB_SIZE;
import com.github.lucapino.sheetmaker.model.Artwork;

/**
 *
 * @author tagliani
 */
class PosterArtworkImpl implements Artwork {

    private com.omertron.themoviedbapi.model.Artwork image;
    private String baseURL;
    
    public PosterArtworkImpl(String baseURL, com.omertron.themoviedbapi.model.Artwork image) {
        this.image = image;
        this.baseURL = baseURL;
    }

    @Override
    public String getImageURL() {
        return baseURL + POSTER_SIZE + image.getFilePath();
    }

    @Override
    public String getType() {
        return "poster";
    }

    @Override
    public String getThumbURL() {
        return baseURL + POSTER_THUMB_SIZE + image.getFilePath();
    }
    
    

}
