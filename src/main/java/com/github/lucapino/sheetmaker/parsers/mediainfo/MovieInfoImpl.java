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
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Luca Tagliani
 */
public class MovieInfoImpl implements MovieInfo {

    private final MediaInfo mediaInfo;
    private final File mediaFile;

    public MovieInfoImpl(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
        this.mediaFile = new File(mediaInfo.Get(MediaInfo.StreamKind.General, 0, "CompleteName", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
    }

    @Override
    public String getTitlePath() {
        return mediaInfo.Get(MediaInfo.StreamKind.General, 0, "FolderName", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public String getMovieFileName() {
        StringBuilder result = new StringBuilder();
        result.append(mediaInfo.Get(MediaInfo.StreamKind.General, 0, "FileName", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
        result.append(".");
        result.append(mediaInfo.Get(MediaInfo.StreamKind.General, 0, "FileExtension", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
        return result.toString();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getAllLocalizedSubtitles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getEmbeddedSubtitles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getExternalSubtitles() {
        // TODO: implement scan of files alongside movie file
        return new ArrayList<>();
    }

    @Override
    public String getMediaformat() {
        return mediaInfo.Get(MediaInfo.StreamKind.General, 0, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public String getSoundFormat() {
//        # supported values AAC51, AAC, AAC20, DD51, DD20, DTS51, MP3, FLAC, WMA, VORBIS, DTSHD, DTRUEHD
        String audioCodec = mediaInfo.Get(MediaInfo.StreamKind.Audio, 2, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
//        my $audio_codec = lc($media_info->{Mediainfo}->{File}->{track}->[2]->{Format});
        Integer channels = Integer.valueOf(mediaInfo.Get(MediaInfo.StreamKind.Audio, 2, "Channel(s)", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name));
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
        String suffix = mediaInfo.Get(MediaInfo.StreamKind.Video, 1, "ScanType", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name).substring(0, 1);
//        my $suffix	= lc(substr($media_info->{Mediainfo}->{File}->{track}->[1]->{Scan_type},0,1)) ;
        String videoWidth = mediaInfo.Get(MediaInfo.StreamKind.Video, 1, "Width", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
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
        String videoFormat = mediaInfo.Get(MediaInfo.StreamKind.Video, 1, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
        videoFormat = videoFormat.replaceAll("mpeg-4 visual", "divx");
//        $provider_hash->{VIDEOFORMAT}					= lc($media_info->{Mediainfo}->{File}->{track}->[1]->{Format});
//        $provider_hash->{VIDEOFORMAT}					=~ s/mpeg-4 visual/divx/i;
        return videoFormat;
    }

    @Override
    public String getFrameRate() {
        String frameRate = mediaInfo.Get(MediaInfo.StreamKind.Video, 1, "FrameRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
        frameRate = frameRate.replaceAll("\\w*fps", "");
//    $provider_hash->{FRAMERATETEXT}				= $media_info->{Mediainfo}->{File}->{track}->[1]->{Frame_rate};
//    $provider_hash->{FRAMERATETEXT}				=~ s/\w*fps//;
        return frameRate;
    }

    @Override
    public String getAspectRatio() {
//    $provider_hash->{ASPECTRATIOTEXT}			= $media_info->{Mediainfo}->{File}->{track}->[1]->{Display_aspect_ratio};
        return mediaInfo.Get(MediaInfo.StreamKind.Video, 1, "DisplayAspectRatio", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name).substring(0, 1);
    }

    @Override
    public String getVideoResolution() {
        String videoWidth = mediaInfo.Get(MediaInfo.StreamKind.Video, 1, "Width", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
        String videoHeight = mediaInfo.Get(MediaInfo.StreamKind.Video, 1, "Height", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
//    $provider_hash->{VIDEORESOLUTIONTEXT}	= sprintf("%sx%s",$media_info->{Mediainfo}->{File}->{track}->[1]->{Width},$media_info->{Mediainfo}->{File}->{track}->[1]->{Height});
//    $provider_hash->{VIDEORESOLUTIONTEXT} =~ s/pixels//g;
//    $provider_hash->{VIDEORESOLUTIONTEXT} =~ s/\s//g;
        return String.format("%sx%s", videoWidth, videoHeight);
    }

    @Override
    public String getVideoBitrate() {
//    $provider_hash->{VIDEOBITRATETEXT}		= $media_info->{Mediainfo}->{File}->{track}->[1]->{Bit_rate}; 
        return mediaInfo.Get(MediaInfo.StreamKind.Video, 1, "BitRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
    public String getAudioCodec() {
//    $provider_hash->{AUDIOCODECTEXT}			= $media_info->{Mediainfo}->{File}->{track}->[2]->{Codec_ID};
        return mediaInfo.Get(MediaInfo.StreamKind.Video, 2, "BitRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
    }

    @Override
//    $provider_hash->{AUDIOCHANNELSTEXT}		= $media_info->{Mediainfo}->{File}->{track}->[2]->{Channel_s_};
//    $provider_hash->{AUDIOCHANNELSTEXT}		=~ s/(\d+) .*$/$1 /;
    public String getAudioChannels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAudioBitrate() {
//    $provider_hash->{AUDIOBITRATETEXT}		= $media_info->{Mediainfo}->{File}->{track}->[2]->{Bit_rate};
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDuration() {
//    $provider_hash->{DURATIONTEXT}				= $media_info->{Mediainfo}->{File}->{track}->[1]->{Duration};
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFileSize() {
//    $provider_hash->{FILESIZETEXT}				= $media_info->{Mediainfo}->{File}->{track}->[0]->{File_size};
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContainer() {
//    $provider_hash->{CONTAINERTEXT}				= lc($media_info->{Mediainfo}->{File}->{track}->[0]->{Format});
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLanguageCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLanguage() {
//    my @al_ary=map{$_->{type} =~ /audio/i ? $_->{Language} : () } @{$media_info->{Mediainfo}->{File}->{track}};
//    $provider_hash->{LANGUAGE}=\@al_ary;
//    $counter=1;
//    #		Logger($config_options,sprintf("Found %d Subtitles",$#sub_ary),"DEBUG");
//
//    foreach (@al_ary) {
//            $provider_hash->{"LANGUAGE$counter"}=lc($_);
//            $counter++;
//    };
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getAllLanguages() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getAllLanguageCodes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AudioInfo> getAllAudioInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String localizeLanguage(String language) {
        Locale langlocale = Locale.forLanguageTag(language);
        return StringUtils.capitalize(langlocale.getDisplayLanguage(langlocale));
    }

}
