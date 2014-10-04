/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.parsers.mediainfo;

import com.github.lucapino.sheetmaker.parsers.InfoRetriever;
import com.github.lucapino.sheetmaker.parsers.mediainfo.MediaInfo;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author tagliani
 */
public class MediaInfoExtractor implements InfoRetriever {

    public static void main(String[] args) throws Exception {
        MediaInfoExtractor extractor = new MediaInfoExtractor();
        extractor.print("/media/Volume");
    }

    private void print(String rootPath) throws Exception {
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                String lowerCaseName = pathname.getName().toLowerCase();
                return (lowerCaseName.endsWith("ifo") && lowerCaseName.startsWith("vts"))
                        || lowerCaseName.endsWith("mkv")
                        || lowerCaseName.endsWith("iso")
                        || lowerCaseName.endsWith("avi");
            }
        };
        // parse all the tree under rootPath
        File rootFolder = new File(rootPath);
        File[] files = rootFolder.listFiles();
        Arrays.sort(files);
        Map<File, List<File>> mediaMap = new TreeMap<>();
        for (File file : files) {
            // name of the folder -> name of media
            List<File> fileList;
            if (file.isDirectory()) {
                fileList = recurseSubFolder(filter, file);
                if (!fileList.isEmpty()) {
                    mediaMap.put(file, fileList);
                }
            } else {
                if (filter.accept(file)) {
                    fileList = new ArrayList<>();
                    fileList.add(file);
                    mediaMap.put(file, fileList);
                }
            }
        }
        Set<File> fileNamesSet = mediaMap.keySet();
        File outputFile = new File("/home/tagliani/tmp/HD-report.xls");
        Workbook wb = new HSSFWorkbook(new FileInputStream(outputFile));
        Sheet sheet = wb.createSheet("HD8");

        MediaInfo MI = new MediaInfo();
        int j = 0;
        for (File mediaFile : fileNamesSet) {
            List<File> filesList = mediaMap.get(mediaFile);
            for (File fileInList : filesList) {
                List<String> audioTracks = new ArrayList<>();
                List<String> videoTracks = new ArrayList<>();
                List<String> subtitlesTracks = new ArrayList<>();
                MI.Open(fileInList.getAbsolutePath());
//                System.out.println(fileInList.getName());
                String durationInt = MI.Get(MediaInfo.StreamKind.General, 0, "Duration", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                if (StringUtils.isNotEmpty(durationInt) && Integer.valueOf(durationInt) >= 60 * 60 * 1000) {
                    Row row = sheet.createRow(j);
                    String duration = MI.Get(MediaInfo.StreamKind.General, 0, "Duration/String", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                    // Create a cell and put a value in it.
                    row.createCell(0).setCellValue(WordUtils.capitalizeFully(mediaFile.getName()));
                    if (fileInList.getName().toLowerCase().endsWith("iso") || fileInList.getName().toLowerCase().endsWith("ifo")) {
                        row.createCell(1).setCellValue("DVD");
                    } else {
                        row.createCell(1).setCellValue(FilenameUtils.getExtension(fileInList.getName()).toUpperCase());
                    }
                    row.createCell(2).setCellValue(FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(mediaFile)));
                    // row.createCell(3).setCellValue(fileInList.getAbsolutePath());
                    row.createCell(3).setCellValue(duration);
                    // MPEG-2 720x576 @ 25fps 16:9
                    String format = MI.Get(MediaInfo.StreamKind.Video, 0, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                    row.createCell(4).setCellValue(format);
                    String width = MI.Get(MediaInfo.StreamKind.Video, 0, "Width", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                    String height = MI.Get(MediaInfo.StreamKind.Video, 0, "Height", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                    row.createCell(5).setCellValue(width + "x" + height);
                    String fps = MI.Get(MediaInfo.StreamKind.Video, 0, "FrameRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                    row.createCell(6).setCellValue(fps);
                    String aspectRatio = MI.Get(MediaInfo.StreamKind.Video, 0, "DisplayAspectRatio/String", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                    row.createCell(7).setCellValue(aspectRatio);

                    int audioStreamNumber = MI.Count_Get(MediaInfo.StreamKind.Audio);
                    for (int i = 0; i < audioStreamNumber; i++) {
                        String audioTitle = MI.Get(MediaInfo.StreamKind.Audio, i, "Title", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                        String language = MI.Get(MediaInfo.StreamKind.Audio, i, "Language/String3", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                        String codec = MI.Get(MediaInfo.StreamKind.Audio, i, "Codec", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                        String channels = MI.Get(MediaInfo.StreamKind.Audio, i, "Channel(s)", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                        String sampleRate = MI.Get(MediaInfo.StreamKind.Audio, i, "SamplingRate/String", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                        // AC3 ITA 5.1 48.0KHz
                        StringBuilder sb = new StringBuilder();
                        if (StringUtils.isEmpty(audioTitle)) {
                            sb.append(codec).append(" ").append(language.toUpperCase()).append(" ").append(channels);
                        } else {
                            sb.append(audioTitle);
                        }
                        sb.append(" ").append(sampleRate);
                        audioTracks.add(sb.toString());
                    }

                    int textStreamNumber = MI.Count_Get(MediaInfo.StreamKind.Text);
                    for (int i = 0; i < textStreamNumber; i++) {
                        String textLanguage = MI.Get(MediaInfo.StreamKind.Text, i, "Language/String", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                        subtitlesTracks.add(textLanguage);
                    }
                    MI.Close();
                    row.createCell(8).setCellValue(audioTracks.toString());
                    row.createCell(9).setCellValue(subtitlesTracks.toString());
                    j++;
                }

            }

//            System.out.println(mediaName);
        }
        try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
            wb.write(fileOut);
        }

    }

    private List<File> recurseSubFolder(FileFilter filter, File file) {
        // if we have a folder
        File[] mediaFiles = file.listFiles(filter);
        List<File> mediaFileList = new ArrayList<>();
        if (mediaFiles.length > 0) {
            mediaFileList.addAll(Arrays.asList(mediaFiles));
        } else {
            File[] innerFiles = file.listFiles();
            for (File innerFile : innerFiles) {
                if (innerFile.isDirectory()) {
                    mediaFileList.addAll(recurseSubFolder(filter, innerFile));
                }
            }
        }
        return mediaFileList;
    }

}
