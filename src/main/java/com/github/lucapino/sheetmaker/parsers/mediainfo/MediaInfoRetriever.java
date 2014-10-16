/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.parsers.mediainfo;

import com.github.lucapino.sheetmaker.parsers.InfoRetriever;
import com.github.lucapino.sheetmaker.parsers.MovieInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileFilter;
import org.apache.commons.vfs.FileFilterSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

/**
 *
 * @author tagliani
 */
public class MediaInfoRetriever implements InfoRetriever {

    @Override
    public MovieInfo getMovieInfo(String filePath) {
        MediaInfo mediaInfo = new MediaInfo();
        String fileToParse = filePath;
        // test if we have iso
        if (filePath.toLowerCase().endsWith("iso")) {
            // open iso and parse the ifo files
            Map<Integer, String> ifoFiles = new TreeMap<>();
            try {
                FileSystemManager fsManager = VFS.getManager();
                FileObject fo = fsManager.resolveFile("iso:" + filePath + "!/");
                // create an ifo file selector
                FileSelector ifoFs = new FileFilterSelector(new FileFilter() {

                    @Override
                    public boolean accept(FileSelectInfo fsi) {
                        return fsi.getFile().getName().getBaseName().toLowerCase().endsWith("ifo");
                    }
                });
                FileObject[] files = fo.getChild("VIDEO_TS").findFiles(ifoFs);
                for (FileObject file : files) {
                    File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separator + file.getName().getBaseName());
                    System.out.println(file.getName().getBaseName());
                    IOUtils.copy(file.getContent().getInputStream(), new FileOutputStream(tmpFile));
                    mediaInfo.Open(tmpFile.getAbsolutePath());
                    String format = mediaInfo.Get(MediaInfo.StreamKind.General, 0, "Format_Profile", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                    System.out.println("Format profile: " + format);
                    // if format is "Program" -> it's a video file
                    if (format.equalsIgnoreCase("program")) {
                        String duration = mediaInfo.Get(MediaInfo.StreamKind.General, 0, "Duration", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                        System.out.println("Duration: " + duration);
                        ifoFiles.put(Integer.valueOf(duration), tmpFile.getName());
                    }
                    mediaInfo.Close();
                }
                if (!ifoFiles.isEmpty()) {
                    if (ifoFiles.size() == 1) {
                        fileToParse = ifoFiles.values().iterator().next();
                    } else {
                        // get the last entry -> the bigger one
                        Set<Integer> keys = ifoFiles.keySet();
                        Iterator<Integer> iterator = keys.iterator();
                        for (int i = 0; i < keys.size(); i++) {
                            String fileName = ifoFiles.get(iterator.next());
                            if (i == keys.size() - 1) {
                                fileToParse = fileName;
                            } else {
                                new File(fileName).delete();
                            }
                        }
                    }
                }
            } catch (IOException | NumberFormatException ex) {

            }
        }
        // here fileToParse is correct
        mediaInfo.Open(fileToParse);
        System.out.println(mediaInfo.Inform());

        return new MovieInfoImpl(mediaInfo, filePath);
    }

}
