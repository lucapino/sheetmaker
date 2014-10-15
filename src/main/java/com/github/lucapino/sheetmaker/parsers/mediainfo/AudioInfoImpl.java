/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.parsers.mediainfo;

import com.github.lucapino.sheetmaker.parsers.AudioInfo;

/**
 *
 * @author tagliani
 */
public class AudioInfoImpl implements AudioInfo {

    private final int audioStreamNumber;
    private final MovieInfoImpl movieInfo;

    public AudioInfoImpl(MovieInfoImpl movieInfo, int audioStreamNumber) {
        this.audioStreamNumber = audioStreamNumber;
        this.movieInfo = movieInfo;
    }

    @Override
    public String getSoundFormat() {
        return movieInfo.getSoundFormat(audioStreamNumber);
    }

    @Override
    public String getAudioCodec() {
        return movieInfo.getAudioCodec(audioStreamNumber);
    }

    @Override
    public String getAudioChannels() {
        return movieInfo.getAudioChannels(audioStreamNumber);
    }

    @Override
    public String getAudioBitrate() {
        return movieInfo.getAudioBitrate(audioStreamNumber);
    }

    @Override
    public String getLanguageCode() {
        return movieInfo.getLanguageCode(audioStreamNumber);
    }

    @Override
    public String getLanguage() {
        return movieInfo.getLanguage(audioStreamNumber);
    }

}
