/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.parsers;

/**
 *
 * @author tagliani
 */
public interface AudioInfo {

    // DTS
    String getSoundFormat();
    
    // A_DTS
    String getAudioCodec();
    
    // 2
    String getAudioChannels();
    
    // 128 Kbps
    String getAudioBitrate();
}
