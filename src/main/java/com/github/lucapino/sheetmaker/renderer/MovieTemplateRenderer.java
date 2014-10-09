/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

import com.github.lucapino.sheetmaker.model.movie.Movie;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author Luca Tagliani
 */
public class MovieTemplateRenderer {

    private final String SETTINGS = "Settings";
    private final String SOUND_FORMATS = "SoundFormats";
    private final String RESOLUTIONS = "Resolutions";
    private final String MEDIA_FORMATS = "MediaFormats";
    private final String VIDEO_FORMATS = "VideoFormats";
    private final String IMAGE_DRAW_TEMPLATE = "ImageDrawTemplate";

    private final String OUTPUT_IMAGE_SETTINGS = "OutputImageSettings";
    private final String CANVAS = "Canvas";

    private final static String BACKGROUND = "%BACKGROUND%";
    private final static String FANART1 = "%FANART1%";
    private final static String FANART2 = "%FANART2%";
    private final static String FANART3 = "%FANART3%";
    private final static String COVER = "%COVER%";
    private final static String PATH = "%PATH%";

    private String basePath;

    private TemplateSettings settings;
    private final Map<String, TemplateElement> soundFormats = new HashMap<>();
    private final Map<String, TemplateElement> resolutions = new HashMap<>();
    private final Map<String, TemplateElement> mediaFormats = new HashMap<>();
    private final Map<String, TemplateElement> videoFormats = new HashMap<>();

    private final Movie movie;
    private final String backgroundFilePath;
    private final String fanArt1FilePath;
    private final String fanArt2FilePath;
    private final String fanArt3FilePath;
    private final String coverFilePath;

    public MovieTemplateRenderer(Movie movie, String backgroundFilePath, String fanArt1FilePath, String fanArt2FilePath, String fanArt3FilePath, String coverFilePath) {
        this.movie = movie;
        this.backgroundFilePath = backgroundFilePath;
        this.fanArt1FilePath = fanArt1FilePath;
        this.fanArt2FilePath = fanArt2FilePath;
        this.fanArt3FilePath = fanArt3FilePath;
        this.coverFilePath = coverFilePath;
    }

    public JPanel renderTemplate(File templateXML) throws Exception {
        JPanel panel = new JPanel();

        basePath = templateXML.getParentFile().getAbsolutePath();

        String templateString = IOUtils.toString(new FileInputStream(templateXML));
        StringReader templateReader = filterTemplate(templateString, movie);

        // parse XML
        // the SAXBuilder is the easiest way to create the JDOM2 objects.
        SAXBuilder jdomBuilder = new SAXBuilder();

        // jdomDocument is the JDOM2 Object
        Document jdomDocument = jdomBuilder.build(templateReader);
        Element rootElement = jdomDocument.getRootElement();
        // process Settings
        Element settingsElement = rootElement.getChild(SETTINGS);
        settings = new TemplateSettings(settingsElement);
        // process SoundFormats
        Element soundFormatsElement = rootElement.getChild(SOUND_FORMATS);
        cacheElements(soundFormatsElement, soundFormats);
        // process Resolutions
        Element resolutionsElement = rootElement.getChild(RESOLUTIONS);
        cacheElements(resolutionsElement, resolutions);
        // process MediaFormats
        Element mediaFormatsElement = rootElement.getChild(MEDIA_FORMATS);
        cacheElements(mediaFormatsElement, mediaFormats);
        // process VideoFormats
        Element videoFormatsElement = rootElement.getChild(VIDEO_FORMATS);
        cacheElements(videoFormatsElement, videoFormats);
        // process ImageDrawTemplate
        Element drawImageTemplateElement = rootElement.getChild(IMAGE_DRAW_TEMPLATE);
        drawTemplate(panel, drawImageTemplateElement);
        return panel;
    }

    private void cacheElements(Element element, Map<String, TemplateElement> cache) {
        for (Element child : element.getChildren()) {
            TemplateElement templateElement = new TemplateElement(child);
            cache.put(templateElement.getName(), templateElement);
        }
    }

    private void drawTemplate(JPanel panel, Element drawImageTemplateElement) throws Exception {
        // OutputImageSettings
        Element outputImageSettingsElement = drawImageTemplateElement.getChild(OUTPUT_IMAGE_SETTINGS);
        String colorDepth = outputImageSettingsElement.getAttributeValue("ColorDepth");
        String imageForat = outputImageSettingsElement.getAttributeValue("ImageFormat");
        String jpegCompressionLevel = outputImageSettingsElement.getAttributeValue("JpegCompressionLevel");
        String dpi = outputImageSettingsElement.getAttributeValue("Dpi");

        // Canvas
        Element canvasElement = drawImageTemplateElement.getChild(CANVAS);
        String autoSize = drawImageTemplateElement.getAttributeValue("AutoSize");
        String centerElements = drawImageTemplateElement.getAttributeValue("CenterElements");
        int height = Integer.valueOf(drawImageTemplateElement.getAttributeValue("Height"));
        int width = Integer.valueOf(drawImageTemplateElement.getAttributeValue("Width"));
        String fill = drawImageTemplateElement.getAttributeValue("Fill");

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        // set the correct dimension of JPanel
        // panel.setSize(Integer.valueOf(width), Integer.valueOf(height));
        // Elements
        Element elementsElement = drawImageTemplateElement.getChild("Elements");
        for (Element element : elementsElement.getChildren()) {
            switch (element.getName()) {
                case "ImageElement":
                    processImageElement(g2, element);
                    break;
                case "TextElement":
                    processTextElement(g2, element);
                    break;
            }
        }
    }

    private void processImageElement(Graphics2D g2, Element imageElement) throws Exception {
        System.out.println("Processing " + imageElement.getAttributeValue("Name") + "...");
        int x = Integer.valueOf(imageElement.getAttributeValue("X"));
        int y = Integer.valueOf(imageElement.getAttributeValue("Y"));
        int width = Integer.valueOf(imageElement.getAttributeValue("Width"));
        int height = Integer.valueOf(imageElement.getAttributeValue("Height"));
        // File or Base64String
        String sourceType = imageElement.getAttributeValue("Source");
        String sourceData = imageElement.getAttributeValue("SourceData");
        String nullImageUrl = imageElement.getAttributeValue("NullImageUrl");
        String sourceDpi = imageElement.getAttributeValue("SourceDpi");
        boolean useSourceDpi = Boolean.valueOf(imageElement.getAttributeValue("UseSourceDpi"));

        switch (sourceType) {
            case "File":
                BufferedImage tmpImage;
                // load image from file
                if (StringUtils.isEmpty(sourceData)) {
                    tmpImage = ImageIO.read(new File(nullImageUrl));
                } else {
                    tmpImage = ImageIO.read(new File(sourceData));
                }
                // verify if there are filters
                List<Element> filters = imageElement.getChild("Actions").getChildren();
                for (Element filter : filters) {
                    switch (filter.getName()) {
                        case "AdjustOpacity":
                            break;
                    }
                }
                // TODO: implement filters
                g2.drawImage(tmpImage, x, y, width, height, null);
                break;
            case "Base64String":
                // use substitution to retrieve fileName

                break;
        }

    }

    private void processTextElement(Graphics2D g2, Element textElement) {

    }

    private StringReader filterTemplate(String templateString, Movie movie) {
        templateString = templateString.replaceAll(PATH, basePath);
        templateString = templateString.replaceAll(BACKGROUND, backgroundFilePath);
        templateString = templateString.replaceAll(FANART1, fanArt1FilePath);
        templateString = templateString.replaceAll(FANART2, fanArt2FilePath);
        templateString = templateString.replaceAll(FANART3, fanArt3FilePath);
        templateString = templateString.replaceAll(COVER, coverFilePath);
        return new StringReader(templateString);
    }

}
