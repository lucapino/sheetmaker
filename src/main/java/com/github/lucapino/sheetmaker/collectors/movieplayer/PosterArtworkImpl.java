/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.movieplayer;

import com.github.lucapino.sheetmaker.model.Artwork;
import it.movieplayer.Poster_;
import java.awt.Image;

/**
 *
 * @author tagliani
 */
class PosterArtworkImpl implements Artwork {

    private final String type = "poster";
    
    private final Poster_ poster;

    public PosterArtworkImpl(Poster_ poster) {
        this.poster = poster;
    }
    
    @Override
    public String getImageURL() {
        return poster.getUrl();
    }

    @Override
    public String getThumbURL() {
        return poster.getThumb();
    }

    @Override
    public String getType() {
        return type;
    }

}
