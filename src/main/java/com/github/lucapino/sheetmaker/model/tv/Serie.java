/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.model.tv;

import java.util.List;

/**
 *
 * @author Luca Tagliani
 */
public interface Serie {

    String getName();

    List<Season> getSeasons();

    Season getSeason(int season);

    int getSeasonsNumber();

    String getOverview();

    List<String> getActors();

    List<String> getDirectors();
// TODO: add images
}
