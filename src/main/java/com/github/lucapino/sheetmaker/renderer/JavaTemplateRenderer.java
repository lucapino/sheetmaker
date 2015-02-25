/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker.renderer;

import static com.github.lucapino.sheetmaker.renderer.Constants.CANVAS;
import static com.github.lucapino.sheetmaker.renderer.Constants.IMAGE_DRAW_TEMPLATE;
import static com.github.lucapino.sheetmaker.renderer.Constants.MEDIA_FORMATS;
import static com.github.lucapino.sheetmaker.renderer.Constants.OUTPUT_IMAGE_SETTINGS;
import static com.github.lucapino.sheetmaker.renderer.Constants.RESOLUTIONS;
import static com.github.lucapino.sheetmaker.renderer.Constants.SETTINGS;
import static com.github.lucapino.sheetmaker.renderer.Constants.SOUND_FORMATS;
import static com.github.lucapino.sheetmaker.renderer.Constants.VIDEO_FORMATS;
import com.github.lucapino.sheetmaker.utils.ScreenImage;
import com.jhlabs.image.FlipFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.GlowFilter;
import com.jhlabs.image.MirrorFilter;
import com.jhlabs.image.OpacityFilter;
import com.jhlabs.image.RotateFilter;
import com.jhlabs.image.ShadowFilter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.imgscalr.Scalr;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Luca Tagliani
 */
public class JavaTemplateRenderer {

    Pattern pattern = Pattern.compile("%[\\w]*%(\\{[\\w]*\\})*");

    private final static Logger logger = LoggerFactory.getLogger(JavaTemplateRenderer.class);

    private String basePath;
    private Map<String, String> tokenMap;

    private TemplateSettings settings;
    private final Map<String, TemplateElement> soundFormats = new HashMap<>();
    private final Map<String, TemplateElement> resolutions = new HashMap<>();
    private final Map<String, TemplateElement> mediaFormats = new HashMap<>();
    private final Map<String, TemplateElement> videoFormats = new HashMap<>();

    private InputStream checkForUtf8BOMAndDiscardIfAny(InputStream inputStream) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
        byte[] bom = new byte[3];
        if (pushbackInputStream.read(bom) != -1) {
            if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
                pushbackInputStream.unread(bom);
            }
        }
        return pushbackInputStream;
    }

    public JPanel renderTemplate(URL templateXML, Map<String, String> tokenMap, String backgroundFilePath, String fanArt1FilePath, String fanArt2FilePath, String fanArt3FilePath, String coverFilePath) throws Exception {
        this.tokenMap = tokenMap;
        String templatePath = templateXML.getFile();
        File templateFile = new File(templatePath);

        // set basePath
        basePath = templateFile.getParentFile().getAbsolutePath().replaceAll("\\\\", "/");

        logger.info("Loading template...");
        // load template
        String templateString = IOUtils.toString(checkForUtf8BOMAndDiscardIfAny(new FileInputStream(templateFile)), "ISO-8859-1");
        // filter placeHolder
        logger.info("Parsing template for substitution...");
        templateString = templateString.replaceAll("%PATH%", basePath);
        templateString = templateString.replaceAll("%BACKGROUND%", backgroundFilePath);
        templateString = templateString.replaceAll("%FANART1%", fanArt1FilePath);
        templateString = templateString.replaceAll("%FANART2%", fanArt2FilePath);
        templateString = templateString.replaceAll("%FANART3%", fanArt3FilePath);
        templateString = templateString.replaceAll("%COVER%", coverFilePath);
        StringReader templateReader = new StringReader(templateString);
        System.out.println(templateString);
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
        int width = Integer.valueOf(canvasElement.getAttributeValue("Width"));
        int height = Integer.valueOf(canvasElement.getAttributeValue("Height"));
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
//        String sourceDpi = imageElement.getAttributeValue("SourceDpi");
//        boolean useSourceDpi = Boolean.valueOf(imageElement.getAttributeValue("UseSourceDpi"));
        BufferedImage tmpImage = null;
        switch (sourceType) {
            case "File":
                // load image from file
                if (StringUtils.isEmpty(sourceData)) {
                    tmpImage = ImageIO.read(new File(nullImageUrl.replaceAll("\\\\", "/")));
                } else {
//                    tmpImage = ImageIO.read(new File(sourceData.replaceAll("\\\\", "/")));
                    tmpImage = ImageIO.read(new File(sourceData.replaceAll("\\\\", "/")));
                }
                break;
            case "Base64String":
                // use substitution to retrieve fileName
                // RATINGSTARS
                if (sourceData.equalsIgnoreCase("%RATINGSTARS%")) {
//
//                    BufferedImage stars = ImageIO.read(new FileInputStream(settings.getStarsRating().replaceAll("\\\\", "/")));
//
//                    // create stars
//                    float starsNumber = Float.valueOf(tokenMap.get("%RATINGPERCENT%")) / 10F;
//                    int fullStarsNumber = (int) Math.floor(starsNumber);
//                    float starFraction = starsNumber - fullStarsNumber;
//
//                    // 1 star -> 24px, so 7.4 stars are 24x7.4 -> 178px
//                    BufferedImage singleStar = stars.getSubimage(0, 0, 24, 24);
//
//                    //Initializing the final image  
//                    tmpImage = new BufferedImage(width, height, singleStar.getType());
//                    Graphics2D g2i = tmpImage.createGraphics();
//                    for (int i = 0; i < fullStarsNumber; i++) {
//                        g2i.drawImage(singleStar, 24 * i, 0, null);
//                    }
//                    // crop the last star
//                    BufferedImage croppedStar = singleStar.getSubimage(0, 0, Math.round(24 * starFraction), 24);
//                    g2i.drawImage(croppedStar, 24 * fullStarsNumber, 0, null);
                } else {
                    String imageUrl = tokenMap.get(sourceData);
                    if (imageUrl != null) {
                        tmpImage = ImageIO.read(new File(imageUrl.replaceAll("\\\\", "/")));
                    }
                }
                break;
        }
        if (tmpImage != null) {
            // process actions
            tmpImage = processActions(imageElement, tmpImage);
            // alway resize
            Scalr.resize(tmpImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                    width, height, Scalr.OP_ANTIALIAS);
//            BicubicScaleFilter scaleFilter = new BicubicScaleFilter(width, height);
//            tmpImage = scaleFilter.filter(tmpImage, null);
            g2.drawImage(tmpImage, x, y, width, height, null);
        }
        logger.info("{} processed...", imageElement.getAttributeValue("Name"));
    }
// The LineBreakMeasurer used to line-break the paragraph.
    private LineBreakMeasurer lineMeasurer;

    // index of the first character in the paragraph.
    private int paragraphStart;

    // index of the first character after the end of the paragraph.
    private int paragraphEnd;

    private static final Hashtable<TextAttribute, Object> map
            = new Hashtable<TextAttribute, Object>();

    private void processTextElement(Graphics2D g2, Element textElement) {

        int x = Integer.valueOf(textElement.getAttributeValue("X"));
        int y = Integer.valueOf(textElement.getAttributeValue("Y"));
        int width = Integer.valueOf(textElement.getAttributeValue("Width"));
        int height = Integer.valueOf(textElement.getAttributeValue("Height"));
        String alignment = textElement.getAttributeValue("TextAlignment");
        boolean multiline = Boolean.valueOf(textElement.getAttributeValue("Multiline").toLowerCase());
        boolean antiAlias = textElement.getAttributeValue("TextQuality").equalsIgnoreCase("antialias");

        Font font = parseFont(textElement.getAttributeValue("Font"));

        logger.info("Using font " + font);
        // now get the textim4java performance
        String text = textElement.getAttributeValue("Text");
        // if text matches pattern of %VARIABLE%{MODIFIER}
        logger.info("parsing token {}", text);
        Matcher matcher = pattern.matcher(text);
        int start = 0;
        while (matcher.find(start)) {
            // apply modification
            text = text.replace(matcher.group(), applyModifier(matcher.group()));
            start = matcher.end();
        }
        BufferedImage tmpImage;
        if (width > 0 && height > 0) {
            // create a transparent tmpImage
            tmpImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } else {
            FontMetrics fm = g2.getFontMetrics(font);
            Rectangle outlineBounds = fm.getStringBounds(text, g2).getBounds();
//         we need to create a transparent image to paint
            tmpImage = new BufferedImage(outlineBounds.width, outlineBounds.height, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d = tmpImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
//        }
        g2d.setFont(font);
        Color textColor = new Color(Integer.valueOf(textElement.getAttributeValue("ForeColor")));
        g2d.setColor(textColor);
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .8f);
        g2d.setComposite(comp);
        drawString(g2d, text, new Rectangle(0, 0, width, height), Align.valueOf(alignment), 0, multiline);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        tmpImage = processActions(textElement, tmpImage);

////        Graphics2D g2d = tmpImage.createGraphics();
//        // set current font
//        g2.setFont(font);
////        g2d.setComposite(AlphaComposite.Clear);
////        g2d.fillRect(0, 0, width, height);
////        g2d.setComposite(AlphaComposite.Src);
//        // TODO: we have to parse it
//        int strokeWidth = Integer.valueOf(textElement.getAttributeValue("StrokeWidth"));
//        // the color of the outline
//        if (strokeWidth > 0) {
////            Color strokeColor = new Color(Integer.valueOf(textElement.getAttributeValue("StrokeColor")));
////            AffineTransform affineTransform;
////            affineTransform = g2d.getTransform();
////            affineTransform.translate(width / 2 - (outlineBounds.width / 2), height / 2
////                    + (outlineBounds.height / 2));
////            g2d.transform(affineTransform);
////            // backup stroke width and color
////            Stroke originalStroke = g2d.getStroke();
////            Color originalColor = g2d.getColor();
////            g2d.setColor(strokeColor);
////            g2d.setStroke(new BasicStroke(strokeWidth));
////            g2d.draw(shape);
////            g2d.setClip(shape);
////            // restore stroke width and color
////            g2d.setStroke(originalStroke);
////            g2d.setColor(originalColor);
//        }
////        // get the text color
//        Color textColor = new Color(Integer.valueOf(textElement.getAttributeValue("ForeColor")));
//        g2.setColor(textColor);
////        g2d.setBackground(Color.BLACK);
////        g2d.setStroke(new BasicStroke(2));
////        g2d.setColor(Color.WHITE);
//        // draw the text
//
//        drawString(g2, text, new Rectangle(x, y, width, height), Align.valueOf(alignment), 0, multiline);
//        g2.drawString(text, x, y);
//        Rectangle rect = new Rectangle(x, y, width, height); // defines the desired size and position
//        FontMetrics fm = g2.getFontMetrics();
//        FontRenderContext frc = g2.getFontRenderContext();
//        TextLayout tl = new TextLayout(text, g2.getFont(), frc);
//        AffineTransform transform = new AffineTransform();
//        transform.setToTranslation(rect.getX(), rect.getY());
//        if (Boolean.valueOf(textElement.getAttributeValue("AutoSize").toLowerCase())) {
//            double scaleY
//                    = rect.getHeight() / (double) (tl.getOutline(null).getBounds().getMaxY()
//                    - tl.getOutline(null).getBounds().getMinY());
//            transform.scale(rect.getWidth() / (double) fm.stringWidth(text), scaleY);
//        }
//        Shape shape = tl.getOutline(transform);
//        g2.setClip(shape);
//        g2.fill(shape.getBounds());
//        if (antiAlias) {
        // we need to restore antialias to none
//            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//        }
//        g2.drawString(text, x, y);
        // alway resize
//        BicubicScaleFilter scaleFilter = new BicubicScaleFilter(width, height);
//        tmpImage = scaleFilter.filter(tmpImage, null);
        // draw the image to the source
        g2.drawImage(tmpImage, x, y, width, height, null);
        try {
            ScreenImage.writeImage(tmpImage, "/tmp/images/" + textElement.getAttributeValue("Name") + ".png");
        } catch (IOException ex) {

        }

    }

    private BufferedImage processActions(Element imageElement, BufferedImage tmpImage) {
        // verify if there are filters
        Element actions = imageElement.getChild("Actions");
        if (actions != null) {
            List<Element> filters = actions.getChildren();
            for (Element filter : filters) {
                logger.info("Processing action {}", filter.getName());
                // TODO: implement filters
                switch (filter.getName()) {
                    // Crop
                    case "Crop":
                        break;
                    // GlassTable
                    case "GlassTable":
                        MirrorFilter mirrorFilter = new MirrorFilter();
                        float reflectionOpacity = Float.valueOf(filter.getAttributeValue("ReflectionOpacity"));
                        mirrorFilter.setOpacity(reflectionOpacity / 100);
                        mirrorFilter.setCentreY(1f);
                        mirrorFilter.setGap(0f);
                        
                        tmpImage = mirrorFilter.filter(tmpImage, null);
                        break;
                    // Glow
                    case "Glow":
                        GlowFilter glowFilter = new GlowFilter();
                        float amount = Float.valueOf(filter.getAttributeValue("Amount"));
                        glowFilter.setAmount(amount);
                        tmpImage = glowFilter.filter(tmpImage, null);
                        break;
                    // GaussianBlur
                    case "GaussianBlur":
                        GaussianFilter gaussianFilter = new GaussianFilter();
                        gaussianFilter.setRadius(Float.valueOf(filter.getAttributeValue("Radius")));
                        tmpImage = gaussianFilter.filter(tmpImage, null);
                        break;
                    // AdjustHue
                    case "AdjustHue":
                        break;
                    // AdjustGamma
                    case "AdjustGamma":
                        break;
                    // RoundCorners
                    case "RoundCorners":
                        break;
                    // AdjustSaturation
                    case "AdjustSaturation":
                        break;
                    // AdjustBrightness
                    case "AdjustBrightness":
                        break;
                    // AdjustOpacity
                    case "AdjustOpacity":
//                        int opacity = (int) (Float.valueOf(filter.getAttributeValue("Opacity")) * 255 / 100);
//                        OpacityFilter opacityFilter = new OpacityFilter(opacity);
//                        tmpImage = opacityFilter.filter(tmpImage, null);
                        break;
                    // PerspectiveView
                    case "PerspectiveView":
                        break;
                    // Rotate
                    case "Rotate":
                        RotateFilter rotateFilter = new RotateFilter();
                        float rotateAngle = Float.valueOf(filter.getAttributeValue("Angle"));
                        rotateFilter.setAngle(rotateAngle);
                        tmpImage = rotateFilter.filter(tmpImage, null);
                        break;
                    // DropShadow
                    case "DropShadow":
                        logger.info("Dropping shadow...");
                        ShadowFilter shadow = new ShadowFilter();
                        float angle = Float.valueOf(filter.getAttributeValue("Angle"));
                        shadow.setAngle(angle);
                        float distance = Float.valueOf(filter.getAttributeValue("Distance"));
                        shadow.setDistance(distance);
                        // shadow.setRadius(3.0f);
                        float opacity = Float.valueOf(filter.getAttributeValue("Opacity"));
                        shadow.setOpacity(opacity / 100);
                        tmpImage = shadow.filter(tmpImage, null);
                        break;
                    // Skew
                    case "Skew":
                        break;
                    // Flip
                    case "Flip":
                        FlipFilter flipFilter = new FlipFilter();
                        // Type can be "Horizontal" or "Vertical"
                        String type = filter.getAttributeValue("Type");
                        switch (type.toLowerCase()) {
                            case "horizontal":
                                flipFilter.setOperation(FlipFilter.FLIP_H);
                                break;
                            case "vertical":
                                flipFilter.setOperation(FlipFilter.FLIP_V);
                                break;
                        }
                        tmpImage = flipFilter.filter(tmpImage, null);
                        break;

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
        String fontName = "Droid Sans"; //fontSpecsArray[0];
        int size = Integer.valueOf(fontSpecsArray[1]);
//        size = (int)Math.round(size * Toolkit.getDefaultToolkit().getScreenResolution() / 72.0);
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

    String applyModifier(String token) {
        // TODO: correct implementation
        String result;
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
        result = tokenMap.get(token);
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

    public enum Align {

        TopCenter, TopRight, MiddleRight, BottomRight, BottomCenter, BottomLeft, MiddleLeft, TopLeft, Center
    }

    public void drawString(Graphics g, String text, RectangularShape bounds, Align align, double angle, boolean multiline) {
        Graphics2D g2 = (Graphics2D) g;
        Font font = g2.getFont();
        if (angle != 0) {
            g2.setFont(font.deriveFont(AffineTransform.getRotateInstance(Math.toRadians(angle))));
        }

        Rectangle2D sSize = g2.getFontMetrics().getStringBounds(text, g2);
        Point2D pos = getPoint(bounds, align);
        double x = pos.getX();
        double y = pos.getY() + sSize.getHeight();

        switch (align) {
            case TopCenter:
            case BottomCenter:
            case Center:
                x -= (sSize.getWidth() / 2);
                break;
            case TopRight:
            case MiddleRight:
            case BottomRight:
                x -= (sSize.getWidth());
                break;
            case BottomLeft:
            case MiddleLeft:
            case TopLeft:
                break;
        }
        if (multiline) {
            // Create a new LineBreakMeasurer from the paragraph.
            // It will be cached and re-used.
            //if (lineMeasurer == null) {
            AttributedCharacterIterator paragraph = new AttributedString(text).getIterator();
            paragraphStart = paragraph.getBeginIndex();
            paragraphEnd = paragraph.getEndIndex();
            FontRenderContext frc = g2.getFontRenderContext();
            lineMeasurer = new LineBreakMeasurer(paragraph, frc);
        //}

            // Set break width to width of Component.
            float breakWidth = (float) bounds.getWidth();
            float drawPosY = (float) y;
            // Set position to the index of the first character in the paragraph.
            lineMeasurer.setPosition(paragraphStart);

            // Get lines until the entire paragraph has been displayed.
            while (lineMeasurer.getPosition() < paragraphEnd) {

                // Retrieve next layout. A cleverer program would also cache
                // these layouts until the component is re-sized.
                TextLayout layout = lineMeasurer.nextLayout(breakWidth);

                // Compute pen x position. If the paragraph is right-to-left we
                // will align the TextLayouts to the right edge of the panel.
                // Note: this won't occur for the English text in this sample.
                // Note: drawPosX is always where the LEFT of the text is placed.
                float drawPosX = layout.isLeftToRight()
                        ? (float) x : (float) x + breakWidth - layout.getAdvance();

                // Move y-coordinate by the ascent of the layout.
                drawPosY += layout.getAscent();

                // Draw the TextLayout at (drawPosX, drawPosY).
                layout.draw(g2, drawPosX, drawPosY);

                // Move y-coordinate in preparation for next layout.
                drawPosY += layout.getDescent() + layout.getLeading();
            }
        } else {
            g2.drawString(text, (float) x, (float) y);
        }
        g2.setFont(font);
    }

    public static Point2D getPoint(RectangularShape bounds, Align align) {
        double x = 0.0;
        double y = 0.0;

        switch (align) {
            case TopCenter:
                x = bounds.getCenterX();
                y = bounds.getMinY();
                break;
            case TopRight:
                x = bounds.getMaxX();
                y = bounds.getMinY();
                break;
            case MiddleRight:
                x = bounds.getMaxX();
                y = bounds.getCenterY();
                break;
            case BottomRight:
                x = bounds.getMaxX();
                y = bounds.getMaxY();
                break;
            case BottomCenter:
                x = bounds.getCenterX();
                y = bounds.getMaxY();
                break;
            case BottomLeft:
                x = bounds.getMinX();
                y = bounds.getMaxY();
                break;
            case MiddleLeft:
                x = bounds.getMinX();
                y = bounds.getCenterY();
                break;
            case TopLeft:
                x = bounds.getMinX();
                y = bounds.getMinY();
                break;
            case Center:
                x = bounds.getCenterX();
                y = bounds.getCenterY();
                break;
        }

        return new Point2D.Double(x, y);
    }
}
