/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

/**
 *
 * @author tagliani
 */
public class Constants {
    // Settings
    public final static String SETTINGS = "Settings";
    // SoundFormats
    public final static String SOUND_FORMATS = "SoundFormats";
    // Resolutions
    public final static String RESOLUTIONS = "Resolutions";
    // MediaFormats
    public final static String MEDIA_FORMATS = "MediaFormats";
    // VideoFormats
    public final static String VIDEO_FORMATS = "VideoFormats";
    // ImageDrawTemplate
    public final static String IMAGE_DRAW_TEMPLATE = "ImageDrawTemplate";
    // OutputImageSettings
    public final static String OUTPUT_IMAGE_SETTINGS = "OutputImageSettings";
    // Canvas
    public final static String CANVAS = "Canvas";

    /***************** TOKENS *****************/
    //    %MOVIEFILENAME%	 Current movie filename	 Spread.2009.mkv (for c:\movies\spread\spread.2009.mkv)
    //    %MOVIEFILENAMEWITHOUTEXT%	 Current movie filename without extension	 Spread.2009 (for c:\Movies\Spread\Spread.2009.mkv)
    //    %MOVIEFOLDER%	 Current movie folder name	 Spread (for c:\Movies\Spread\Spread.2009.mkv)
    //    %MOVIEPARENTFOLDER%	 Current movie parent folder name	 Movies (for c:\Movies\Spread\Spread.2009.mkv)
    //    %ORIGINALTITLE%	 Original movie title	 Inglourious Basterds
    //    %PLOT%	 Synopsis	 Some description of the movie.
    //    %TAGLINE%	 The Tagline	 Some tagline
    //    %COMMENTS%	 Free "joker" field for the user own data	 Some free test the user can place on the sheet
    //    %IMDBID%	 IMDb Id of the movie	 tt1186370
    //    %ALLSUBTITLES%	 List of distinct embedded/external subtitles (English names)	 English, Spanish,German *
    //    %ALLSUBTITLESTEXT%	 List of distinct embedded/external subtitles (native names)	 English, Espanol,Deutsch *
    //    %SUBTITLES1% … %SUBTITLES5%	 Individual embedded subtitles (English names)	 English
    //    %SUBTITLESTEXT%	 List of embedded subtitles (Native names)	 English, Francais, Deutsch *
    //    %EXTERNALSUBTITLESTEXT%	 List of embedded subtitles (Native names)	 English, Francais, Deutsch *
    //    %EXTERNALSUBTITLES%	 List of external subtitles (English names)	 English, French *
    //    %EXTERNALSUBTITLES1% … %EXTERNALSUBTITLES5%	 Individual external subtitles (English names)	 English
    //    %RATINGPERCENT%	 The rating as percent	68
    //    %RATINGSTARS%	 The rating stars (star image loaded from /Template/Settings/Rating/@Filename	 {the stars rating image}
    //    %SEASON%	 The autodetected season number for the current movie	4
    //    %EPISODE%	 The autodetected episode (or CD) number for the current movie	2
    //    %EPISODETITLE%	 The current episode name	 Fire + Water
    //    %EPISODEPLOT%	 Synopsis of the current episode	 Some description of the episode.
    //    %EPISODERELEASEDATE%	 Release date of the current episode	 12.03.2010 (formatted using collector's format)
    //    %EPISODELIST%	 List of episodes for the current season	 1, 2, 3 *
    //    %EPISODENAMESLIST%	 List of episodes titles for the current season	 Title One, Title Two *
    //    %EPISODEGUESTSTARS%	 List of guest stars for the current episode	 John B, John C *
    //    %EPISODEWRITERS%	 List of writers for the current episode	 John B, John C *
    //    %SOUNDFORMATTEXT%	 See below list of sound values supported. /Template/SoundFormats/SoundFormat/@Name Returns value from @Text attribute	 MP3
    //    %RESOLUTIONTEXT%	 See below list of resolution values supported. /Template/Resolutions/Resolution/@Name Returns value from @Text attribute	 1080P
    //    %VIDEOFORMATTEXT%	 See below list of video values supported. /Template/VideoFormats/VideoFormat/@Name Returns value from @Text attribute	 AVC
    //    %FRAMERATE%	 Formatted frame rate of the movie (to allow mapping to filenames). The ‘.’ character is replaced by the ‘_’ character.	 23_976
    //    %ASPECTRATIO%	 Formatted aspect ration (to allow mapping to filenames). The ‘.’ character is replaced by the ‘_’ character and the ‘:’ character is replaced by ‘-’.	 16-9 or 2_35-1 or 4-3
    //    %VIDEORESOLUTIONTEXT%	 The movie resolution	 1920x1080
    //    %DURATION%	 The detected (mediainfo) duration of the movie (minutes)	98
    //    %DURATIONSEC%	 The detected (mediainfo) duration of the movie (seconds)	5880
    //    %CONTAINERTEXT%	 The detected (mediainfo) container format (as it comes from MediaInfo)	 Matroska
    //    %LANGUAGECODE%	 The two letter ISO code of the language of the first audio stream	 en
    //    %LANGUAGE%	 The language of the first audio stream (always the English name)	 Spanish
    //    %LANGUAGES%	 The languages of all audio streams (always the English names)	 Spanish/English/Italian *
    //    %LANGUAGECODES%	 The two letter ISO code of the languages of all audio streams	 es/en/it *
    //    %CERTIFICATIONCOUNTRYCODE%	 The two letter code of the country selected in Options/IMDB as Certification Country (default value: us)	 es
    //    %LANGUAGES1% … %LANGUAGES5%	 Individual audio languages (English names)	 English
}
