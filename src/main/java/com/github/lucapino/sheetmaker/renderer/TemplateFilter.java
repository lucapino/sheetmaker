/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.github.lucapino.sheetmaker.parsers.MovieInfo;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author tagliani
 */
public class TemplateFilter {

    private final Movie movie;
    private final MovieInfo movieInfo;

    public TemplateFilter(Movie movie, MovieInfo movieInfo) {
        this.movie = movie;
        this.movieInfo = movieInfo;
    }

    public String filter(String token) {
        // detect modifier 
        // {UPPER}
        // {LOWER}
        // {TITLECASE}
        boolean upper = token.endsWith("{UPPER}");
        boolean lower = token.endsWith("{LOWER}");
        boolean titleCase = token.endsWith("{TITLECASE}");
        if (upper || lower || titleCase) {
            token = token.substring(0, token.indexOf("{"));
        }
        String result = "";
        switch (token) {
            case "%COUNTRIES%":
                break;
            case "%RUNTIME%":
                result = movie.getDuration();
                break;
            case "%RELEASEDATE%":
                result = movie.getReleaseDate();
                break;
            case "%CERTIFICATION":
                break;
            case "%CERTIFICATIONTEXT%":
                break;
            case "%YEAR%":
                break;
            case "%MPAA%":
                break;
            case "%EXTERNALSUBTITLES1%":
                break;
            case "%EXTERNALSUBTITLESTEXT%":
                break;
            case "%EXTERNALSUBTITLES%":
                break;
            case "%SUBTITLES%":
                break;
            case "%TITLE%":
                result = movie.getTitle();
                break;
            case "%DURATIONTEXT%":
                // TODO: convert in minutes
                result = movie.getDuration();
                break;
            case "%VIDEORESOLUTIONTEXT%":
                break;
            case "%FRAMERATETEXT%":
                result = movieInfo.getFrameRate();
                break;
            case "%AUDIOCODECTEXT%":
                result = movieInfo.getAudioInfo().get(0).getAudioCodec();
                break;
            case "%ASPECTRATIOTEXT%":
                result = movieInfo.getAspectRatio();
                break;
            case "%VIDEOBITRATETEXT%":
                break;
            case "%AUDIOCHANNELSTEXT%":
                break;
            case "%AUDIOBITRATETEXT%":
                break;
            case "%FILESIZETEXT%":
                break;
            case "%RATING%":
                break;
            case "%PLOT%":
                break;
            case "%STUDIOS%":
                break;
            case "%FANART1%":
                break;
            case "%FANART2%":
                break;
            case "%FANART3%":
                break;
            case "%BACKGROUND%":
                break;
            case "%COVER%":
                break;
            case "%VIDEOFORMAT%":
                break;
            case "%CONTAINERTEXT%":
                break;
            case "%VIDEOCODECTEXT%":
                break;
            case "%MEDIAFORMATTEXT%":
                break;
            case "%MEDIAFORMAT%":
                break;
            case "%RESOLUTION%":
                break;
            case "%SOUNDFORMAT%":
                break;
            case "%SUBTITLESTEXT%":
                break;
            case "%ACTORS%":
                break;
            case "%DIRECTORS%":
                break;
            case "%GENRES%":
                break;
            case "%TITLEPATH%":
                break;
            case "%PATH%":
                break;
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
            default:
        }
        // apply modifier
        if (upper) {
            result = result.toUpperCase();
        } else if (lower) {
            result = result.toLowerCase();
        } else if (titleCase) {
            result = WordUtils.capitalize(result);
        }
        return result;
    }

}
