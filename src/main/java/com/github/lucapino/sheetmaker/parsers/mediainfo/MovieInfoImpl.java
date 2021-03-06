/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.parsers.mediainfo;

import com.github.lucapino.sheetmaker.parsers.AudioInfo;
import com.github.lucapino.sheetmaker.parsers.MovieInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Luca Tagliani
 */
public class MovieInfoImpl implements MovieInfo {

    private final MediaInfo mediaInfo;
    private final File mediaFile;

    public MovieInfoImpl(MediaInfo mediaInfo, String realfilePath) {
        this.mediaInfo = mediaInfo;
        String mediaInfoFilePath = mediaInfo.Get(MediaInfo.StreamKind.General, 0, "CompleteName", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
        if (mediaInfoFilePath.equals(realfilePath)) {
            this.mediaFile = new File(mediaInfoFilePath);
        } else {
            this.mediaFile = new File(realfilePath);
        }

    }

    @Override
    public String getTitlePath() {
        return mediaFile.getParent();
    }

    @Override
    public String getMovieFileName() {
        return mediaFile.getName();
    }

    @Override
    public String getMovieFolder() {
        return mediaFile.getParentFile().getName();
    }

    @Override
    public String getParentMovieFolder() {
        return mediaFile.getParentFile().getParentFile().getName();
    }

//    # internal subtitles
//    my @sub_ary=map{$_->{type} =~ /text/i ? $_->{Language} : () } @{$media_info->{Mediainfo}->{File}->{track}};
//    $provider_hash->{SUBTITLES}=\@sub_ary;
//    my $counter=1;
//    Logger($config_options,sprintf("Found %d Subtitles",$#sub_ary),"DEBUG");
//
//    foreach (@sub_ary) {
//            $provider_hash->{"SUBTITLES$counter"}=lc($_);
//            $counter++;
//    }
//
//    $provider_hash->{EXTERNALSUBTITLES}		= '';
//    $provider_hash->{EXTERNALSUBTITLESTEXT}		= '';
    @Override
    public List<String> getAllSubtitles() {
        Set<String> result = new HashSet<>();
        result.addAll(getEmbeddedSubtitles());
        result.addAll(getExternalSubtitles());
        return new ArrayList(result);
    }

    @Override
    public List<String> getAllLocalizedSubtitles() {
        Set<String> subtitles = new HashSet<>();
        int number = mediaInfo.Count_Get(MediaInfo.StreamKind.Text);
        for (int i = 0; i < number; i++) {
            String countryCode = mediaInfo.Get(MediaInfo.StreamKind.Text, i, "Language", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
            subtitles.add(localizeLanguage(countryCode));
        }
        return new ArrayList(subtitles);
    }

    @Override
    public List<String> getEmbeddedLocalizedSubtitles() {
        Set<String> subtitles = new HashSet<>();
        int number = mediaInfo.Count_Get(MediaInfo.StreamKind.Text);
        for (int i = 0; i < number; i++) {
            String countryCode = mediaInfo.Get(MediaInfo.StreamKind.Text, i, "Language", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
            subtitles.add(localizeLanguage(countryCode));
        }
        return new ArrayList(subtitles);
    }

    @Override
    public List<String> getEmbeddedSubtitles() {
        Set<String> subtitles = new HashSet<>();
        int number = mediaInfo.Count_Get(MediaInfo.StreamKind.Text);
        for (int i = 0; i < number; i++) {
            subtitles.add(mediaInfo.Get(MediaInfo.StreamKind.Text, i, "Language/String", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
        }
        return new ArrayList(subtitles);
    }

    @Override
    public List<String> getExternalSubtitles() {
        // TODO: implement scan of files alongside movie file
        return new ArrayList<>();
    }

    @Override
    public List<String> getExternalLocalizedSubtitles() {
        // TODO: implement scan of files alongside movie file
        return new ArrayList<>();
    }

    @Override
    public String getMediaformat() {
        return mediaInfo.Get(MediaInfo.StreamKind.General, 0, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public String getSoundFormat() {
        return getSoundFormat(0);
    }

    @Override
    public String getResolution() {
//        # figure out the resolution.
//        # if the aspect ratio is 4:3 take the width/4*3 to get the resolution
//        # if the aspect ratio is 16:9 its width/16*9 
//        # else its something odd like 2.35:1 (still 16/9 more or less) just key off of the width
//        # typical resolutions are
//        #  288P 480I 480P 576I 576P 720I 720P 1080I 1080P
//
        String resolution = "";
        String suffix = mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "ScanType", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name).substring(0, 1);
//        my $suffix	= lc(substr($media_info->{Mediainfo}->{File}->{track}->[1]->{Scan_type},0,1)) ;
        String videoWidth = mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "Width", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
//        my $width	= lc($media_info->{Mediainfo}->{File}->{track}->[1]->{Width}) ;
//        $width =~ s/\D//g;
//
        if (getAspectRatio().equals("4:3")) {
            resolution = (Integer.valueOf(videoWidth) / 4 * 3) + suffix;
//        if ($provider_hash->{ASPECTRATIOTEXT} eq "4:3" )  { 
//                Logger($config_options,"Calculating Resolution for apectratio 4:3 width=$width suffix=$suffix","DEBUG");
//                $provider_hash->{RESOLUTION} = sprintf ("%d%s",($width/4*3),$suffix); 
//        } 
        } else if (getAspectRatio().equals("16:9") || getAspectRatio().equals("16x9")) {
            resolution = (Integer.valueOf(videoWidth) / 16 * 9) + suffix;
//        elsif ($provider_hash->{ASPECTRATIOTEXT} eq "16x9" ) {
//                Logger($config_options,"Calculating Resolution for apectratio 16:9 width=$width suffix=$suffix","DEBUG");
//                $provider_hash->{RESOLUTION} = sprintf ("%d%s",($width/16*9),$suffix); 
//        }
        } else if (videoWidth.equals("1920")) {
            resolution = "1080" + suffix;
//        elsif ($width == 1920) { 
//                Logger($config_options,"Calculating Resolution for width=$width suffix=$suffix","DEBUG");
//                $provider_hash->{RESOLUTION} = sprintf ("1080%s",$suffix); 
//        }
        } else if (videoWidth.equals("1280")) {
            resolution = "720" + suffix;
//        elsif ($width == 1280) { 
//                Logger($config_options,"Calculating Resolution for width=$width suffix=$suffix","DEBUG");
//                $provider_hash->{RESOLUTION} = sprintf ("720%s",$suffix); 
//        }
        } else if (videoWidth.equals("720")) {
            resolution = "576" + suffix;
//        elsif ($width == 720) { 
//                Logger($config_options,"Calculating Resolution for width=$width suffix=$suffix","DEBUG");
//                $provider_hash->{RESOLUTION} = sprintf ("576%s",$suffix); 
//        }
        } else if (videoWidth.equals("640")) {
            resolution = "480" + suffix;
//        elsif ($width == 640) { 
//                Logger($config_options,"Calculating Resolution for width=$width suffix=$suffix","DEBUG");
//                $provider_hash->{RESOLUTION} = sprintf ("480%s",$suffix); 
//        }
        }
//        else { $provider_hash->{RESOLUTION} = "" ; }
//
        return resolution;
//        Logger($config_options,"Resolution has been determined to be [36m".$provider_hash->{RESOLUTION},"DEBUG");
    }

    @Override
    public String getVideoFormat() {
//        # supported values		Divx, xvid, wmv, avc, mpeg 
        String videoFormat = mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
        videoFormat = videoFormat.replaceAll("mpeg-4 visual", "divx");
//        $provider_hash->{VIDEOFORMAT}					= lc($media_info->{Mediainfo}->{File}->{track}->[1]->{Format});
//        $provider_hash->{VIDEOFORMAT}					=~ s/mpeg-4 visual/divx/i;
        return videoFormat;
    }

    @Override
    public String getFrameRate() {
        String frameRate = mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "FrameRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
        frameRate = frameRate.replaceAll("\\w*fps", "");
//    $provider_hash->{FRAMERATETEXT}				= $media_info->{Mediainfo}->{File}->{track}->[1]->{Frame_rate};
//    $provider_hash->{FRAMERATETEXT}				=~ s/\w*fps//;
        return frameRate;
    }

    @Override
    public String getAspectRatio() {
//    $provider_hash->{ASPECTRATIOTEXT}			= $media_info->{Mediainfo}->{File}->{track}->[1]->{Display_aspect_ratio};
        return mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "DisplayAspectRatio", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public String getVideoResolution() {
        String videoWidth = mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "Width", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
        String videoHeight = mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "Height", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
//    $provider_hash->{VIDEORESOLUTIONTEXT}	= sprintf("%sx%s",$media_info->{Mediainfo}->{File}->{track}->[1]->{Width},$media_info->{Mediainfo}->{File}->{track}->[1]->{Height});
//    $provider_hash->{VIDEORESOLUTIONTEXT} =~ s/pixels//g;
//    $provider_hash->{VIDEORESOLUTIONTEXT} =~ s/\s//g;
        return String.format("%sx%s", videoWidth, videoHeight);
    }

    @Override
    public String getVideoBitrate() {
//    $provider_hash->{VIDEOBITRATETEXT}		= $media_info->{Mediainfo}->{File}->{track}->[1]->{Bit_rate}; 
        return mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "BitRate_Nominal", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public String getAudioCodec() {
        return getAudioCodec(0);
    }

    @Override
    public String getAudioChannels() {
        return getAudioChannels(0);
    }

    @Override
    public String getAudioBitrate() {
        return getAudioBitrate(0);
    }

    @Override
    public String getDuration() {
//    $provider_hash->{DURATIONTEXT}				= $media_info->{Mediainfo}->{File}->{track}->[1]->{Duration};
        return mediaInfo.Get(MediaInfo.StreamKind.Video, 0, "Duration", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public String getFileSize() {
        return "" + FileUtils.sizeOf(mediaFile);
    }

    @Override
    public String getContainer() {
//    $provider_hash->{CONTAINERTEXT}				= lc($media_info->{Mediainfo}->{File}->{track}->[0]->{Format});
        return mediaInfo.Get(MediaInfo.StreamKind.General, 0, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public String getLanguageCode() {
        return getLanguageCode(0);
    }

    @Override
    public String getLanguage() {
        return getLanguage(0);
    }

    @Override
    public List<String> getAllLanguages() {
        List<String> languages = new ArrayList<>();
        int number = mediaInfo.Count_Get(MediaInfo.StreamKind.Audio);
        for (int i = 0; i < number; i++) {
            languages.add(mediaInfo.Get(MediaInfo.StreamKind.Audio, i, "Language/String", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
        }
        return languages;
    }

    @Override
    public List<String> getAllLanguageCodes() {
        List<String> languages = new ArrayList<>();
        int number = mediaInfo.Count_Get(MediaInfo.StreamKind.Audio);
        for (int i = 0; i < number; i++) {
            languages.add(mediaInfo.Get(MediaInfo.StreamKind.Audio, i, "Language", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
        }
        return languages;
    }

    @Override
    public List<AudioInfo> getAllAudioInfo() {
        List<AudioInfo> result = new ArrayList<>();
        int number = mediaInfo.Count_Get(MediaInfo.StreamKind.Audio);
        for (int i = 0; i < number; i++) {
            result.add(new AudioInfoImpl(this, number));
        }
        return result;
    }

    String localizeLanguage(String language) {
        Locale langlocale = Locale.forLanguageTag(language);
        return StringUtils.capitalize(langlocale.getDisplayLanguage(langlocale));
    }

    String getSoundFormat(int audioStreamNumber) {
//        # supported values AAC51, AAC, AAC20, DD51, DD20, DTS51, MP3, FLAC, WMA, VORBIS, DTSHD, DTRUEHD
        String audioCodec = mediaInfo.Get(MediaInfo.StreamKind.Audio, audioStreamNumber, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
//        my $audio_codec = lc($media_info->{Mediainfo}->{File}->{track}->[2]->{Format});
        Integer channels = Integer.valueOf(mediaInfo.Get(MediaInfo.StreamKind.Audio, audioStreamNumber, "Channel(s)", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
//        my $channels=$media_info->{Mediainfo}->{File}->{track}->[2]->{Channel_s_};
//        $channels =~ s/\D//g;
        String soundFormat = "";
        if (audioCodec.toLowerCase().contains("dts")) {
            switch (channels) {
                case 8:
                    soundFormat = "DTS71";
                    break;
                case 7:
                    soundFormat = "DTS70";
                    break;
                case 6:
                    soundFormat = "DTS51";
                    break;
                case 5:
                    soundFormat = "DTS41";
                    break;
                case 4:
                    soundFormat = "DTS40";
                    break;
                case 3:
                    soundFormat = "DTS21";
                    break;
                case 2:
                    soundFormat = "DTS20";
                    break;
                default:
                    soundFormat = "DTS";
            }
        } else if (audioCodec.toLowerCase().contains("aac")) {
            switch (channels) {
                case 8:
                    soundFormat = "AAC71";
                    break;
                case 7:
                    soundFormat = "AAC70";
                    break;
                case 6:
                    soundFormat = "AAC51";
                    break;
                case 5:
                    soundFormat = "AAC41";
                    break;
                case 4:
                    soundFormat = "AAC40";
                    break;
                case 3:
                    soundFormat = "AAC21";
                    break;
                case 2:
                    soundFormat = "AAC20";
                    break;
                default:
                    soundFormat = "AAC";
            }
        } else if (audioCodec.toLowerCase().contains("ac-3")) {
            switch (channels) {
                case 8:
                    soundFormat = "DD71";
                    break;
                case 7:
                    soundFormat = "DD70";
                    break;
                case 6:
                    soundFormat = "DD51";
                    break;
                case 5:
                    soundFormat = "DD41";
                    break;
                case 4:
                    soundFormat = "DD40";
                    break;
                case 3:
                    soundFormat = "DD21";
                    break;
                case 2:
                    soundFormat = "DD20";
                    break;
                default:
                    soundFormat = "DD";
            }
        } else if (audioCodec.toLowerCase().contains("mpeg")) {
            switch (channels) {
                case 2:
                    soundFormat = "MP320";
                    break;
                case 1:
                    soundFormat = "MP310";
                    break;
                default:
                    soundFormat = "MP3";
            }
        }
//        else {
//                $provider_hash->{SOUNDFORMAT}					=~ s/.*mpeg.*/All MPEG/i;
//                # more search/replace as found.
//        }
        return soundFormat;
    }

    String getAudioCodec(int audioStreamNumber) {
//    $provider_hash->{AUDIOCODECTEXT}			= $media_info->{Mediainfo}->{File}->{track}->[2]->{Codec_ID};
        return mediaInfo.Get(MediaInfo.StreamKind.Audio, audioStreamNumber, "CodecID", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

//    $provider_hash->{AUDIOCHANNELSTEXT}		= $media_info->{Mediainfo}->{File}->{track}->[2]->{Channel_s_};
//    $provider_hash->{AUDIOCHANNELSTEXT}		=~ s/(\d+) .*$/$1 /;
    String getAudioChannels(int audioStreamNumber) {
        return mediaInfo.Get(MediaInfo.StreamKind.Audio, audioStreamNumber, "Channel(s)", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    String getAudioBitrate(int audioStreamNumber) {
//    $provider_hash->{AUDIOBITRATETEXT}		= $media_info->{Mediainfo}->{File}->{track}->[2]->{Bit_rate};
        return mediaInfo.Get(MediaInfo.StreamKind.Audio, audioStreamNumber, "BitRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    String getLanguageCode(int audioStreamNumber) {
        return mediaInfo.Get(MediaInfo.StreamKind.Audio, audioStreamNumber, "Language", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    String getLanguage(int audioStreamNumber) {
//    my @al_ary=map{$_->{type} =~ /audio/i ? $_->{Language} : () } @{$media_info->{Mediainfo}->{File}->{track}};
//    $provider_hash->{LANGUAGE}=\@al_ary;
//    $counter=1;
//    #		Logger($config_options,sprintf("Found %d Subtitles",$#sub_ary),"DEBUG");
//
//    foreach (@al_ary) {
//            $provider_hash->{"LANGUAGE$counter"}=lc($_);
//            $counter++;
//    };
        return mediaInfo.Get(MediaInfo.StreamKind.Audio, audioStreamNumber, "Language/String", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public void close() {
        mediaInfo.Close();
        if (mediaInfo.Get(MediaInfo.StreamKind.General, 0, "CompleteName", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name).startsWith(System.getProperty("java.io.tmpdir"))) {
            // delete temp file
            mediaFile.delete();
        }
    }

}
