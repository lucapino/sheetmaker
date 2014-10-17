/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.github.lucapino.sheetmaker.model.tv.Episode;
import com.github.lucapino.sheetmaker.parsers.AudioInfo;
import com.github.lucapino.sheetmaker.parsers.MovieInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author tagliani
 */
public class TemplateFilter {

    // %COVER%, %PATH%, %BACKGROUND%, %FANART1%, %FANART2%, %FANART3% are managed directly by renderer
    public static Map<String, String> createTokenMap(Movie movie, MovieInfo movieInfo, Episode episode) {
        Map<String, String> result = new HashMap<>();
        result.put("%TITLEPATH%", movieInfo.getTitlePath());
        //    %MOVIEFILENAME%	 Current movie filename	 Spread.2009.mkv (for c,\movies\spread\spread.2009.mkv)
        result.put("%MOVIEFILENAME%", movieInfo.getMovieFileName());

        //    %MOVIEFILENAMEWITHOUTEXT%	 Current movie filename without extension	 Spread.2009 (for c,\Movies\Spread\Spread.2009.mkv)
        result.put("%MOVIEFILENAMEWITHOUTEXT%", FilenameUtils.getBaseName(movieInfo.getMovieFileName()));

        //    %MOVIEFOLDER%	 Current movie folder name	 Spread (for c,\Movies\Spread\Spread.2009.mkv)
        result.put("%MOVIEFOLDER%", movieInfo.getMovieFolder());

        //    %MOVIEPARENTFOLDER%	 Current movie parent folder name	 Movies (for c,\Movies\Spread\Spread.2009.mkv)
        result.put("%MOVIEPARENTFOLDER%", movieInfo.getParentMovieFolder());

        result.put("%TITLE%", movie.getTitle());

        //    %ORIGINALTITLE%	 Original movie title	 Inglourious Basterds
        result.put("%ORIGINALTITLE%", movie.getOriginalTitle());

        //    %PLOT%	 Synopsis	 Some description of the movie.
        result.put("%PLOT%", movie.getPlot());

        //    %TAGLINE%	 The Tagline	 Some tagline
        result.put("%TAGLINE%", movie.getTagline());

        //    %COMMENTS%	 Free "joker" field for the user own data	 Some free test the user can place on the sheet
        //TODO
        result.put("%COMMENTS%", "");

        result.put("%YEAR%", movie.getYear());

        result.put("%RUNTIME%", movie.getRuntime());

        String actors = movie.getActors().toString();
        result.put("%ACTORS%", actors.substring(1, actors.length() - 1));

        String genres = movie.getGenres().toString();
        result.put("%GENRES%", genres.substring(1, genres.length() - 1));

        String directors = movie.getDirectors().toString();
        result.put("%DIRECTORS%", directors.substring(1, directors.length() - 1));

        result.put("%CERTIFICATION%", movie.getCertification());

        result.put("%RELEASEDATE%", movie.getReleaseDate());

        result.put("%MPAA%",
                movie.getMPAA()
        );

        //    %IMDBID%	 IMDb Id of the movie	 tt1186370
        result.put("%IMDBID%",
                movie.getImdbId()
        );

        result.put("%CERTIFICATIONTEXT%",
                movie.getCertification()
        );
        String countries = movie.getCountries().toString();
        result.put("%COUNTRIES%", countries.substring(1, countries.length() - 1));

        String studios = movie.getStudios().toString();
        result.put("%STUDIOS%", studios.substring(1, studios.length() - 1));

        //    %ALLSUBTITLES%	 List of distinct embedded/external subtitles (English names)	 English, Spanish,German *
        String allSubtitles = movieInfo.getAllSubtitles().toString();
        result.put("%ALLSUBTITLES%", allSubtitles.substring(1, allSubtitles.length() - 1));

        //    %ALLSUBTITLESTEXT%	 List of distinct embedded/external subtitles (native names)	 English, Espanol,Deutsch *
        String allLocalizedSubtitles = movieInfo.getAllLocalizedSubtitles().toString();
        result.put("%ALLSUBTITLESTEXT%", allLocalizedSubtitles.substring(1, allLocalizedSubtitles.length() - 1));

        String embeddedSubtitles = movieInfo.getEmbeddedSubtitles().toString();
        result.put("%SUBTITLES%", embeddedSubtitles.substring(1, embeddedSubtitles.length() - 1));

        //    %SUBTITLES1% … %SUBTITLES5%	 Individual embedded subtitles (English names)	 English
        //    %SUBTITLESTEXT%	 List of embedded subtitles (Native names)	 English, Francais, Deutsch *
        String embeddedLocalizedSubtitles = movieInfo.getEmbeddedLocalizedSubtitles().toString();
        result.put("%SUBTITLESTEXT%", embeddedLocalizedSubtitles.substring(1, embeddedLocalizedSubtitles.length() - 1));

        //    %EXTERNALSUBTITLESTEXT%	 List of external subtitles (Native names)	 English, Francais, Deutsch *
        String externalLocalizedSubtitles = movieInfo.getExternalLocalizedSubtitles().toString();
        result.put("%EXTERNALSUBTITLESTEXT%", externalLocalizedSubtitles.substring(1, externalLocalizedSubtitles.length() - 1));

        //    %EXTERNALSUBTITLES%	 List of external subtitles (English names)	 English, French *
        String externalSubtitles = movieInfo.getExternalSubtitles().toString();
        result.put("%EXTERNALSUBTITLES%", externalSubtitles.substring(1, externalSubtitles.length() - 1));

        //    %EXTERNALSUBTITLES1% … %EXTERNALSUBTITLES5%	 Individual external subtitles (English names)	 English
        // TODO
        //    %RATING%	 The rating of the movie (x of 10)	 6.8/10
        result.put("%RATING%", (Double.valueOf(movie.getRatingPercent()) / 10) + "/10");

        //    %RATINGPERCENT%	 The rating as percent	68
        result.put("%RATINGPERCENT%", movie.getRatingPercent());

        if (episode != null) {
            /**
             * ************** Start TV Shows ********************
             */
            //    %SEASON%	 The autodetected season number for the current movie	4
            result.put("%SEASON%", "" + episode.getSeason().getNumber());

            //    %EPISODE%	 The autodetected episode (or CD) number for the current movie	2
            result.put("%EPISODE%", "" + episode.getNumber());

            //    %EPISODETITLE%	 The current episode name	 Fire + Water
            result.put("%EPISODETITLE%", episode.getTitle());

            //    %EPISODEPLOT%	 Synopsis of the current episode	 Some description of the episode.
            result.put("%EPISODEPLOT%", episode.getOverview());

        //    %EPISODERELEASEDATE%	 Release date of the current episode	 12.03.2010 (formatted using collector's format)
            // TODO
            result.put("%EPISODERELEASEDATE%", "");

            //    %EPISODELIST%	 List of episodes for the current season	 1, 2, 3 *
            List<Episode> episodes = episode.getSeason().getEpisodes();
            List<String> episodeList = new ArrayList<>();
            for (Episode currentEpisode : episodes) {
                episodeList.add("" + currentEpisode.getNumber());
            }
            String res = episodeList.toString();
            result.put("%EPISODELIST%", res.substring(1, res.length() - 1));

            //    %EPISODENAMESLIST%	 List of episodes titles for the current season	 Title One, Title Two *
            episodes = episode.getSeason().getEpisodes();
            List<String> episodeNameList = new ArrayList<>();
            for (Episode currentEpisode : episodes) {
                episodeNameList.add(currentEpisode.getTitle());
            }
            res = episodeNameList.toString();
            result.put("%EPISODENAMESLIST%", res.substring(1, res.length() - 1));

            //    %EPISODEGUESTSTARS%	 List of guest stars for the current episode	 John B, John C *
            List<String> episodeGuestsList = episode.getGuestStars();
            String episodeGuests = episodeGuestsList.toString();
            result.put("%EPISODEGUESTSTARS%", episodeGuests.substring(1, episodeGuests.length() - 1));

        //    %EPISODEWRITERS%	 List of writers for the current episode	 John B, John C *
            // TODO
            result.put("%EPISODEWRITERS%", "");
        }
        /**
         * ************** End TV Shows ********************
         */
        //    %MEDIAFORMATTEXT%	 See below list of media values supported. Loaded from /Template/MediaFormats/MediaFormat/@Name Returns value from @Text attribute	 MKV
        result.put("%MEDIAFORMATTEXT%", movieInfo.getMediaformat());

        //    %SOUNDFORMATTEXT%	 See below list of sound values supported. /Template/SoundFormats/SoundFormat/@Name Returns value from @Text attribute	 MP3
        result.put("%SOUNDFORMATTEXT%", movieInfo.getSoundFormat());

        //    %RESOLUTIONTEXT%	 See below list of resolution values supported. /Template/Resolutions/Resolution/@Name Returns value from @Text attribute	 1080P
        result.put("%RESOLUTIONTEXT%", movieInfo.getResolution());

        //    %VIDEOFORMATTEXT%	 See below list of video values supported. /Template/VideoFormats/VideoFormat/@Name Returns value from @Text attribute	 AVC
        result.put("%VIDEOFORMATTEXT%", movieInfo.getVideoFormat());

        result.put("%FRAMERATETEXT%", movieInfo.getFrameRate());

        //    %FRAMERATE%	 Formatted frame rate of the movie (to allow mapping to filenames). The ‘.’ character is replaced by the ‘_’ character.	 23_976
        result.put("%FRAMERATE%", movieInfo.getFrameRate().replace(".", "_"));

        result.put("%ASPECTRATIOTEXT%", movieInfo.getAspectRatio());

        //    %ASPECTRATIO%	 Formatted aspect ration (to allow mapping to filenames). The ‘.’ character is replaced by the ‘_’ character and the ‘,’ character is replaced by ‘-’.	 16-9 or 2_35-1 or 4-3
        result.put("%ASPECTRATIO%", movieInfo.getAspectRatio().replace(":", "-"));

        result.put("%VIDEORESOLUTION%", movieInfo.getVideoResolution());

        //    %VIDEORESOLUTIONTEXT%	 The movie resolution	 1920x1080
        result.put("%VIDEORESOLUTIONTEXT%", movieInfo.getVideoResolution());

        // TODO
        result.put("%VIDEOCODECTEXT%", "");
        result.put("%VIDEOBITRATETEXT%", movieInfo.getVideoBitrate());

        result.put("%AUDIOCODECTEXT%", movieInfo.getAllAudioInfo().get(0).getAudioCodec());

        result.put("%AUDIOCHANNELSTEXT%", movieInfo.getAudioChannels());

        result.put("%AUDIOBITRATETEXT%", movieInfo.getAudioBitrate());

        // TODO, convert in minutes
        result.put("%DURATIONTEXT%", movieInfo.getDuration());

        //    %DURATION%	 The detected (mediainfo) duration of the movie (minutes)	98
        result.put("%DURATION%", movieInfo.getDuration());

        //    %DURATIONSEC%	 The detected (mediainfo) duration of the movie (seconds)	5880
        result.put("%DURATIONSEC%", "" + (Integer.valueOf(movieInfo.getDuration()) * 60));

        // TODO, convert in human kB, MB, GB
        result.put("%FILESIZETEXT%", movieInfo.getFileSize());

        //    %CONTAINERTEXT%	 The detected (mediainfo) container format (as it comes from MediaInfo)	 Matroska
        result.put("%CONTAINERTEXT%", movieInfo.getContainer());

        //    %LANGUAGECODE%	 The two letter ISO code of the language of the first audio stream	 en
        result.put("%LANGUAGECODE%", movieInfo.getLanguageCode());

        //    %LANGUAGE%	 The language of the first audio stream (always the English name)	 Spanish
        result.put("%LANGUAGE%", movieInfo.getLanguage());

        //    %LANGUAGES%	 The languages of all audio streams (always the English names)	 Spanish/English/Italian *
        List<String> languages = new ArrayList<>();
        for (AudioInfo audioInfo : movieInfo.getAllAudioInfo()) {
            languages.add(audioInfo.getLanguage());
        }
        String languagesString = languages.toString();
        result.put("%LANGUAGES%", languagesString.substring(1, languagesString.length() - 1));

        //    %LANGUAGECODES%	 The two letter ISO code of the languages of all audio streams	 es/en/it *
        List<String> languagesCodes = new ArrayList<>();

        for (AudioInfo audioInfo : movieInfo.getAllAudioInfo()) {
            languagesCodes.add(audioInfo.getLanguageCode().toLowerCase());
        }
        String languagesCodesString = languagesCodes.toString();
        result.put("%LANGUAGECODES%", languagesCodesString.substring(1, languagesCodesString.length() - 1));

        //    %CERTIFICATIONCOUNTRYCODE%	 The two letter code of the country selected in Options/IMDB as Certification Country (default value, us)	 es
        result.put("%CERTIFICATIONCOUNTRYCODE%", "us");

        //    %LANGUAGES1% … %LANGUAGES5%	 Individual audio languages (English names)	 English                
        // TODO
        result.put("%LANGUAGES1%", "");
        return result;
    }

}
