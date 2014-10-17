/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.model.tv;

import com.github.lucapino.sheetmaker.model.Artwork;
import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public interface Season {

    int getId();
    
    Serie getSerie();
    
    String getName();

    int getNumber();

    List<Episode> getEpisodes();

    int getEpisodesNumber();

    List<String> getEpisodeTitles();

    Episode getEpisode(int number);

    Episode getEpisode(String episodeTitle);
    
    String getOverview();

    // images
    
    List<Artwork> getPosters();
    
    List<Artwork> getBackdrops();
}
