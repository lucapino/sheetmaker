/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.parsers.xuggler;

import com.github.lucapino.sheetmaker.parsers.InfoRetriever;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 *
 * @author Luca Tagliani
 */
public class XugglerRetriever implements InfoRetriever {

    public static void main(String[] args) {

        // parse file inside folder
        File folder = new File(args[0]);
        File[] files = folder.listFiles();
        for (File file : files) {
            try {
//            String filename = "/media/Elements/Film/mononoke.mkv";
                String filename = file.getAbsolutePath();
                // Create a Xuggler container object
                IContainer container = IContainer.make();

                List<String> audioTracks = new ArrayList<>();
                List<String> videoTracks = new ArrayList<>();
                List<String> subtitlesTracks = new ArrayList<>();

                // Open up the container
                if (container.open(filename, IContainer.Type.READ, null) < 0) {
                    throw new IllegalArgumentException("could not open file: " + filename);
                }

                System.out.println("Title\t: " + container.getMetaData().getValue("title"));
                String duration = DurationFormatUtils.formatDurationHMS(container.getDuration() / 1000);
                duration = duration.substring(0, duration.indexOf("."));// , "H':'m':'s", false);
                System.out.println("Duration: " + duration);

//        Collection<String> keys = metaData.getKeys();
//        for (String key : keys) {
//            System.out.println(key + " = " + metaData.getValue(key));
//        }
                System.out.println("------------------------");
                int numStreams = container.getNumStreams();
                for (int i = 0; i < numStreams; i++) {
                    IStream stream = container.getStream(i);
//            System.out.println("Track n." + stream.getIndex());
                    IStreamCoder coder = stream.getStreamCoder();
                    switch (coder.getCodecType()) {
                        case CODEC_TYPE_VIDEO:
                            // Video: 720x576 @ 25fps
                            videoTracks.add(String.format("%dx%d @ %5.2ffps", coder.getWidth(), coder.getHeight(), coder.getFrameRate().getDouble()));
                            break;
                        case CODEC_TYPE_AUDIO:
                            // Audio: AC3 ITA 5.1 48000Hz
                            String info = stream.getMetaData().getValue("title");
                            if (StringUtils.isEmpty(info)) {
                                String codecID = coder.getCodecID().name();
                                codecID = codecID.substring(codecID.lastIndexOf("_") + 1);
                                info = String.format(codecID + " " + stream.getLanguage().toUpperCase() + " %d %dHz", coder.getSampleRate(), coder.getChannels());
                            } else {
                                // add samplerate
                                info += String.format(" %dHz", coder.getSampleRate());
                            }
                            audioTracks.add(info);
                            break;
                        case CODEC_TYPE_SUBTITLE:
                            // SUBTITLE: ITA
                            subtitlesTracks.add(stream.getLanguage().toUpperCase());
                            break;
                        case CODEC_TYPE_UNKNOWN:
                            // try if it's a subtitle, audio or video track
                            // SUBTITLE: ITA
                            subtitlesTracks.add(stream.getLanguage().toUpperCase());
                            break;
                    }

//            IMetaData metaData = stream.getMetaData();
//            Collection<String> keys = metaData.getKeys();
//            for (String key : keys) {
//                System.out.println(key + " = " + metaData.getValue(key));
//            }
//            System.out.println("=========================");
                }
                System.out.println("Video \t:\t" + videoTracks.toString());
                System.out.println("Audio \t:\t" + audioTracks.toString());
                System.out.println("Subtitles:\t" + subtitlesTracks.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
