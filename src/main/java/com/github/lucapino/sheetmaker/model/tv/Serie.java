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
public interface Serie {

    int getId();
    
    String getName();

    List<Season> getSeasons();

    Season getSeason(int season);

    int getSeasonsNumber();

    String getOverview();

// images
    
    List<Artwork> getPosters();
    
    List<Artwork> getBackdrops();
}
