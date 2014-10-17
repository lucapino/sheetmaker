/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.collectors.movieplayer;

import static com.github.lucapino.sheetmaker.collectors.movieplayer.DataRetrieverImpl.MP_API_KEY;
import com.github.lucapino.sheetmaker.model.Artwork;
import com.github.lucapino.sheetmaker.model.tv.Season;
import com.github.lucapino.sheetmaker.model.tv.Serie;
import it.movieplayer.Images;
import it.movieplayer.Movieplayer;
import it.movieplayer.Poster_;
import it.movieplayer.Wallpaper;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Luca Tagliani
 */
public class SerieImpl implements Serie {

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Season> getSeasons() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Season getSeason(int season) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSeasonsNumber() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getOverview() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    @Override
    public List<Artwork> getPosters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Artwork> getBackdrops() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

//    private List<Artwork> getImages(String imdbID, String artworkType) {
//        List<Artwork> result = new ArrayList<>();
//        Movieplayer movieData = target.path("movie/" + imdbID)
//                .queryParam("api_key", MP_API_KEY)
//                .queryParam("type", artworkType)
//                .request(MediaType.APPLICATION_JSON_TYPE)
//                .get(Movieplayer.class);
//        if (movieData != null) {
//            result = fillArtwork(movieData, artworkType);
//        }
//        return result;
//    }

}
