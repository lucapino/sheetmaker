/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

import com.github.lucapino.sheetmaker.model.movie.Movie;
import static com.github.lucapino.sheetmaker.renderer.Constants.CANVAS;
import static com.github.lucapino.sheetmaker.renderer.Constants.IMAGE_DRAW_TEMPLATE;
import static com.github.lucapino.sheetmaker.renderer.Constants.MEDIA_FORMATS;
import static com.github.lucapino.sheetmaker.renderer.Constants.OUTPUT_IMAGE_SETTINGS;
import static com.github.lucapino.sheetmaker.renderer.Constants.RESOLUTIONS;
import static com.github.lucapino.sheetmaker.renderer.Constants.SETTINGS;
import static com.github.lucapino.sheetmaker.renderer.Constants.SOUND_FORMATS;
import static com.github.lucapino.sheetmaker.renderer.Constants.VIDEO_FORMATS;
import com.github.lucapino.sheetmaker.utils.ScreenImage;
import com.jhlabs.image.BicubicScaleFilter;
import com.jhlabs.image.OpacityFilter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Luca Tagliani
 */
public class MovieTemplateRenderer {

    private final static Logger logger = LoggerFactory.getLogger(MovieTemplateRenderer.class);

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

    public JPanel renderTemplate(URL templateXML) throws Exception {

        String templatePath = templateXML.getFile();
        File templateFile = new File(templatePath);

        // set basePath
        basePath = templateFile.getParentFile().getAbsolutePath();

        logger.info("Loading template...");
        // load template
        String templateString = IOUtils.toString(new FileInputStream(templateFile));
        // filter placeHolder
        logger.info("Parsing template for substitution...");
        StringReader templateReader = filterTemplate(templateString, movie);
        logger.info("Template parsed...");
        // parse XML
        // the SAXBuilder is the easiest way to create the JDOM2 objects.
        SAXBuilder jdomBuilder = new SAXBuilder();

        // jdomDocument is the JDOM2 Object
        Document jdomDocument = jdomBuilder.build(templateReader);
        Element rootElement = jdomDocument.getRootElement();
        // process Settings
        logger.info("Caching settings...");
        Element settingsElement = rootElement.getChild(SETTINGS);
        settings = new TemplateSettings(settingsElement);
        // process SoundFormats
        logger.info("Caching soundFormats...");
        Element soundFormatsElement = rootElement.getChild(SOUND_FORMATS);
        cacheElements(soundFormatsElement, soundFormats);
        // process Resolutions
        logger.info("Caching resolutions...");
        Element resolutionsElement = rootElement.getChild(RESOLUTIONS);
        cacheElements(resolutionsElement, resolutions);
        // process MediaFormats
        logger.info("Caching mediaFormats...");
        Element mediaFormatsElement = rootElement.getChild(MEDIA_FORMATS);
        cacheElements(mediaFormatsElement, mediaFormats);
        // process VideoFormats
        logger.info("Caching videoFormats...");
        Element videoFormatsElement = rootElement.getChild(VIDEO_FORMATS);
        cacheElements(videoFormatsElement, videoFormats);
        // process ImageDrawTemplate
        Element drawImageTemplateElement = rootElement.getChild(IMAGE_DRAW_TEMPLATE);
        logger.info("Drawing images...");
        return drawTemplate(drawImageTemplateElement);
    }

    private void cacheElements(Element element, Map<String, TemplateElement> cache) {
        for (Element child : element.getChildren()) {
            TemplateElement templateElement = new TemplateElement(child);
            cache.put(templateElement.getName(), templateElement);
        }
    }

    private ImagePanel drawTemplate(Element drawImageTemplateElement) throws Exception {
        // OutputImageSettings
        logger.info("reading ImageDrawTemlate attributes...");
        Element outputImageSettingsElement = drawImageTemplateElement.getChild(OUTPUT_IMAGE_SETTINGS);
        String colorDepth = outputImageSettingsElement.getAttributeValue("ColorDepth");
        String imageForat = outputImageSettingsElement.getAttributeValue("ImageFormat");
        String jpegCompressionLevel = outputImageSettingsElement.getAttributeValue("JpegCompressionLevel");
        String dpi = outputImageSettingsElement.getAttributeValue("Dpi");
        logger.info("Reading Canvas attributes...");
        // Canvas
        Element canvasElement = drawImageTemplateElement.getChild(CANVAS);
        String autoSize = canvasElement.getAttributeValue("AutoSize");
        String centerElements = canvasElement.getAttributeValue("CenterElements");
        int height = Integer.valueOf(canvasElement.getAttributeValue("Height"));
        int width = Integer.valueOf(canvasElement.getAttributeValue("Width"));
        String fill = canvasElement.getAttributeValue("Fill");

        // create image of specified dimensions
        logger.info("Creating working image...");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // Elements
        logger.info("Processing elements...");
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
//        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
//        g2.setFont(new Font("Chancery Uralic", Font.BOLD, 60));
//        g2.drawString("Test string", 300, 400);
        return new ImagePanel(image);
    }

    private void processImageElement(Graphics2D g2, Element imageElement) throws Exception {
        logger.info("Processing {}...", imageElement.getAttributeValue("Name"));
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
        BufferedImage tmpImage = null;
        switch (sourceType) {
            case "File":
                // load image from file
                if (StringUtils.isEmpty(sourceData)) {
                    tmpImage = ImageIO.read(new File(nullImageUrl.replaceAll("\\\\", "/")));
                } else {
                    tmpImage = ImageIO.read(new File(sourceData.replaceAll("\\\\", "/")));
                }
                break;
            case "Base64String":
                // use substitution to retrieve fileName
                // RATINGSTARS
                if (sourceData.equalsIgnoreCase("%RATINGSTARS%")) {

                    BufferedImage stars = ImageIO.read(new FileInputStream(settings.getStarsRating().replaceAll("\\\\", "/")));

                    // create stars
                    float starsNumber = Float.valueOf(movie.getRatingPercent()) / 10F;
                    int fullStarsNumber = (int) Math.floor(starsNumber);
                    float starFraction = starsNumber - fullStarsNumber;

                    // 1 star -> 24px, so 7.4 stars are 24x7.4 -> 178px
                    BufferedImage singleStar = stars.getSubimage(0, 0, 24, 24);

                    //Initializing the final image  
                    tmpImage = new BufferedImage(width, height, singleStar.getType());
                    Graphics2D g2i = tmpImage.createGraphics();
                    for (int i = 0; i < fullStarsNumber; i++) {
                        g2i.drawImage(singleStar, 24 * i, 0, null);
                    }
                    // crop the last star
                    BufferedImage croppedStar = singleStar.getSubimage(0, 0, Math.round(24 * starFraction), 24);
                    g2i.drawImage(croppedStar, 24 * fullStarsNumber, 0, null);
                }
                break;
        }
        if (tmpImage != null) {
            // process actions
            tmpImage = processActions(imageElement, tmpImage, g2);
            // alway resize
            BicubicScaleFilter scaleFilter = new BicubicScaleFilter(width, height);
            tmpImage = scaleFilter.filter(tmpImage, null);
            g2.drawImage(tmpImage, x, y, width, height, null);
        }
        logger.info("{} processed...", imageElement.getAttributeValue("Name"));
    }

    private void processTextElement(Graphics2D g2, Element textElement) {

        int x = Integer.valueOf(textElement.getAttributeValue("X"));
        int y = Integer.valueOf(textElement.getAttributeValue("Y"));
        int width = Integer.valueOf(textElement.getAttributeValue("Width"));
        int height = Integer.valueOf(textElement.getAttributeValue("Height"));
        Font font = parseFont(textElement.getAttributeValue("Font"));
        // now get the text
        String text = textElement.getAttributeValue("Text");
//        FontMetrics fm = g2.getFontMetrics(font);
//        FontRenderContext fontRendContext = g2.getFontRenderContext();
//        TextLayout textLayout = new TextLayout(text, font, fontRendContext);
//        Shape shape = textLayout.getOutline(null);
//        Rectangle outlineBounds = fm.getStringBounds(text, g2).getBounds();
        // we need to create a transparent image to paint
//        BufferedImage tmpImage = new BufferedImage(outlineBounds.width, outlineBounds.height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2d = tmpImage.createGraphics();
        // set current font
        g2.setFont(font);
//        g2d.setComposite(AlphaComposite.Clear);
//        g2d.fillRect(0, 0, width, height);
//        g2d.setComposite(AlphaComposite.Src);
        // TODO: we have to parse it
        int strokeWidth = Integer.valueOf(textElement.getAttributeValue("StrokeWidth"));
        // the color of the outline
        if (strokeWidth > 0) {
//            Color strokeColor = new Color(Integer.valueOf(textElement.getAttributeValue("StrokeColor")));
//            AffineTransform affineTransform;
//            affineTransform = g2d.getTransform();
//            affineTransform.translate(width / 2 - (outlineBounds.width / 2), height / 2
//                    + (outlineBounds.height / 2));
//            g2d.transform(affineTransform);
//            // backup stroke width and color
//            Stroke originalStroke = g2d.getStroke();
//            Color originalColor = g2d.getColor();
//            g2d.setColor(strokeColor);
//            g2d.setStroke(new BasicStroke(strokeWidth));
//            g2d.draw(shape);
//            g2d.setClip(shape);
//            // restore stroke width and color
//            g2d.setStroke(originalStroke);
//            g2d.setColor(originalColor);
        }
//        // get the text color
        Color textColor = new Color(Integer.valueOf(textElement.getAttributeValue("ForeColor")));
        g2.setColor(textColor);
//        g2d.setBackground(Color.BLACK);
//        g2d.setStroke(new BasicStroke(2));
//        g2d.setColor(Color.WHITE);
        // draw the text

//        g2.drawString(text, x, y);
        Rectangle rect = new Rectangle(x, y, width, height); // defines the desired size and position
        FontMetrics fm = g2.getFontMetrics();
        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout tl = new TextLayout(text, g2.getFont(), frc);
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation(rect.getX(), rect.getY());
        if (Boolean.valueOf(textElement.getAttributeValue("AutoSize"))) {
            double scaleY
                    = rect.getHeight() / (double) (tl.getOutline(null).getBounds().getMaxY()
                    - tl.getOutline(null).getBounds().getMinY());
            transform.scale(rect.getWidth() / (double) fm.stringWidth(text), scaleY);
        }
        Shape shape = tl.getOutline(transform);
        g2.setClip(shape);
        g2.fill(shape.getBounds());
//        g2.drawString(text, x, y);

        // alway resize
//        BicubicScaleFilter scaleFilter = new BicubicScaleFilter(width, height);
//        tmpImage = scaleFilter.filter(tmpImage, null);
        // draw the image to the source
        /*
         g2.drawImage(tmpImage, x, y, width, height, null);
         try {
         ScreenImage.writeImage(tmpImage, "/tmp/images/" + textElement.getAttributeValue("Name") + ".png");
         } catch (IOException ex) {

         }
         */
    }

    private StringReader filterTemplate(String templateString, Movie movie) {
        templateString = templateString.replaceAll("%PATH%", basePath);
        templateString = templateString.replaceAll("%BACKGROUND%", backgroundFilePath);
        templateString = templateString.replaceAll("%FANART1%", fanArt1FilePath);
        templateString = templateString.replaceAll("%FANART2%", fanArt2FilePath);
        templateString = templateString.replaceAll("%FANART3%", fanArt3FilePath);
        templateString = templateString.replaceAll("%COVER%", coverFilePath);
        return new StringReader(templateString);
    }

    private BufferedImage processActions(Element imageElement, BufferedImage tmpImage, Graphics2D g2) {
        // verify if there are filters
        Element actions = imageElement.getChild("Actions");
        if (actions != null) {
            List<Element> filters = actions.getChildren();
            for (Element filter : filters) {
                // TODO: implement filters
                switch (filter.getName()) {
                    // Crop
                    // GlassTable
                    // Glow
                    // GaussianBlur
                    // AdjustHue
                    // AdjustGamma
                    // RoundCorners
                    // AdjustSaturation
                    // AdjustBrightness
                    case "AdjustOpacity":
                        int opacity = Integer.valueOf(filter.getAttributeValue("Opacity")) * 255 / 100;
                        OpacityFilter opacityFilter = new OpacityFilter(opacity);
                        tmpImage = opacityFilter.filter(tmpImage, null);
                        break;
                    // PerspectiveView
                    // Rotate
                    // DropShadow
                    // Skew
                    // Flip

                }
            }
        }
        return tmpImage;
    }

    private Font parseFont(String fontSpecs) {
        // split attribute value to get font name and carachteristics
        // bold, italic, underline, and strikeout.
        // Size and Unit (Point, Pixel, Millimeter, Inch)
        String[] fontSpecsArray = fontSpecs.split(",");
        int fontStyle = Font.PLAIN;
        boolean isUnderline = false;
        boolean isStrikeout = false;
        // TODO: manage unit
        String unit;
        // font name
        String fontName = "Arial"; //fontSpecsArray[0];
        int size = Integer.valueOf(fontSpecsArray[1]);
        if (fontSpecsArray.length > 5) {
            // her we have a full description
            // bold
            if (Boolean.valueOf(fontSpecsArray[2])) {
                fontStyle += Font.BOLD;
            }
            // italic
            if (Boolean.valueOf(fontSpecsArray[3])) {
                fontStyle += Font.ITALIC;
            }
            // underline
            isUnderline = Boolean.valueOf(fontSpecsArray[4]);
            // strikeout
            isStrikeout = Boolean.valueOf(fontSpecsArray[5]);
            // unit
            unit = fontSpecsArray[6];
        } else {
            // unit
            unit = fontSpecsArray[2];
        }
        Font font = new Font(fontName, fontStyle, size);
        Map attributes = font.getAttributes();
        if (isStrikeout) {
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        if (isUnderline) {
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }
        return new Font(attributes);
    }

}
