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
    // es.: C:\Movies\scifi\Gravity.2014.mkv
    //      /media/Elements/Movies/scifi/Gravity.2014.mkv

    // C:\Movies\scifi
    // /media/Elements/Movies/scifi
    String getTitlePath();

    // Gravity.2014.mkv
    String getMovieFileName();
    
    // scifi
    String getMovieFolder();

    // Movies
    String getParentMovieFolder();
    
    // [Italian, English, French, Spanish, German]
    List<String> getAllSubtitles();
    
    // [Italiano, English, Français, Español, Deutsch]
    List<String> getAllLocalizedSubtitles();
    
    // [Italian, English]
    List<String> getEmbeddedSubtitles();
    
    // [Italiano, English]
    List<String> getEmbeddedLocalizedSubtitles();
    
    // [French, Spanish, German]
    List<String> getExternalSubtitles();
    
    // [Français, Español, Deutsch]
    List<String> getExternalLocalizedSubtitles();

    // MKV
    String getMediaformat();

    // DTS
    String getSoundFormat();
    
    // 1080P
    String getResolution();

    // AVC
    String getVideoFormat();
    
    // 23.976
    String getFrameRate();

    // 16:9 or 2.35:1 or 4.3
    String getAspectRatio();
    
    // 1920x1080
    String getVideoResolution();

    // 977 Kbps
    String getVideoBitrate();
    
    // A_DTS
    String getAudioCodec();
    
    // 2
    String getAudioChannels();
    
    // 128 Kbps
    String getAudioBitrate();
    
    // 134
    String getDuration();
    
    // 234.54 KB or 781.54 MB or 4.34 GB
    String getFileSize();
    
    // Matroska
    String getContainer();
    
    // it
    String getLanguageCode();
    
    // Italian
    String getLanguage();
    
    // [Italian, English, Spanish]
    List<String> getAllLanguages();
    
    // [it, en, es]
    List<String> getAllLanguageCodes();
    
    // Info of all audio tracks, including default one    
    List<AudioInfo> getAllAudioInfo();
    
    void close();
    
}
