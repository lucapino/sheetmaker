/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.parsers.mediainfo;

import com.github.lucapino.sheetmaker.parsers.InfoRetriever;
import com.github.lucapino.sheetmaker.parsers.MovieInfo;

/**
 *
 * @author tagliani
 */
public class MediaInfoRetriever implements InfoRetriever {

    
    
    @Override
    public MovieInfo getMovieInfo(String filePath) {
        MovieInfo result = null;
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.Open(filePath);
        
        return result;
        
    }
    
}
