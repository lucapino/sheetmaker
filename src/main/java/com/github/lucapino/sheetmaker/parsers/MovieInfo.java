/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.parsers;

import java.util.List;

/**
 *
 * @author tagliani
 */
public interface MovieInfo {

    // Titolo	Tipo	Dimensione	Durata	Video	Risoluzione	FPS	Proporzioni	Audio
    String getTitle();

    String getType();

    int getSize();

    int getDuration();

    String getVideoCodec();

    String getResolution();

    String getFrameRate();

    String getAspectRatio();

    List<AudioInfo> getAudioInfo();

    List<String> getSubtitles();

}
