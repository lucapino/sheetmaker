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

    String getAudioCodec();

    String getChannels();

    String getSamplingRate();
}
