/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.movieplayer;

import com.github.lucapino.sheetmaker.model.Artwork;
import it.movieplayer.Wallpaper;
import java.awt.Image;

/**
 *
 * @author tagliani
 */
class BackdropArtworkImpl implements Artwork {

    private final String type = "backdrop";
    
    private final Wallpaper wallpaper;

    public BackdropArtworkImpl(Wallpaper wallpaper) {
        this.wallpaper = wallpaper;
    }
    
    @Override
    public String getImageURL() {
        return wallpaper.getUrl();
    }

    @Override
    public String getThumbURL() {
        return wallpaper.getThumb();
    }

    @Override
    public String getType() {
        return type;
    }

}
