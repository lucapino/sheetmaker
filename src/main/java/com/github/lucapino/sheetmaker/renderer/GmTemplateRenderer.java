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
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.OpacityFilter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.ArrayList;
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
import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tagliani
 */
public class GmTemplateRenderer {

    Pattern pattern = Pattern.compile("%[\\w]*%(\\{[\\w]*\\})*");

    private final static Logger logger = LoggerFactory.getLogger(GmTemplateRenderer.class);

    private String basePath;
    private Map<String, String> tokenMap;

//    private final GMService service = new SimpleGMService();
    private TemplateSettings settings;
    private final Map<String, TemplateElement> soundFormats = new HashMap<>();
    private final Map<String, TemplateElement> resolutions = new HashMap<>();
    private final Map<String, TemplateElement> mediaFormats = new HashMap<>();
    private final Map<String, TemplateElement> videoFormats = new HashMap<>();
    
    private final ConvertCmd convert = new ConvertCmd();
    private final CompositeCmd composite = new CompositeCmd();

    Map<String, String> gravityMap = new HashMap() {
        {
            put("TopLeft", "NorthWest");
            put("TopCenter", "North");
            put("TopRight", "NorthEast");
            put("Left", "West");
            put("Right", "East");
            put("MiddleLeft", "West");
            put("MiddleRight", "East");
            put("BottomLeft", "SouthWest");
            put("BottomMiddle", "South");
            put("BottomRight", "SouthEast");
            put("MiddleCenter", "Center");
        }
    };

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
        basePath = templateFile.getParentFile().getAbsolutePath();

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
        int height = Integer.valueOf(canvasElement.getAttributeValue("Height"));
        int width = Integer.valueOf(canvasElement.getAttributeValue("Width"));
        String fill = canvasElement.getAttributeValue("Fill");

        // create image of specified dimensions
        logger.info("Creating working image...");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Elements
        logger.info("Processing elements...");
        Element elementsElement = drawImageTemplateElement.getChild("Elements");
        for (Element element : elementsElement.getChildren()) {
            switch (element.getName()) {
                case "ImageElement":
                    image = processImageElement(image, element);
                    break;
                case "TextElement":
                    image = processTextElement(image, element);
                    break;
            }
        }
        return new ImagePanel(image);
    }

    private BufferedImage processImageElement(BufferedImage image, Element imageElement) throws Exception {
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
        // temporary image for this step
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
            IMOperation cOp = new IMOperation();
            cOp.addImage();
            // use "!" to forget aspectratio
            cOp.resize(width, height, "!");
            cOp.addImage("png:-");
            Stream2BufferedImage s2b = new Stream2BufferedImage();
            convert.setOutputConsumer(s2b);
            convert.run(cOp, tmpImage);
            tmpImage = s2b.getImage();

            // compose over current image
            IMOperation op = new IMOperation();
            // the image is alrready resized, so we have to fill only x and y
            op.geometry(null, null, x, y);
            // compose putting source image over destination image
            op.compose("Src_Over");
            op.addImage(2);
            op.addImage("png:-");
            s2b = new Stream2BufferedImage();
            composite.setOutputConsumer(s2b);
            composite.run(op, tmpImage, image);

            // retrieve image
            image = s2b.getImage();

//            logger.info("Saving image...");
//            ScreenImage.writeImage(image, "/tmp/images/image" + imageElement.getAttributeValue("Name") + ".png");

        }
        logger.info("{} processed...", imageElement.getAttributeValue("Name"));
        // return processed image
        return image;
    }

// The LineBreakMeasurer used to line-break the paragraph.
    private LineBreakMeasurer lineMeasurer;

    // index of the first character in the paragraph.
    private int paragraphStart;

    // index of the first character after the end of the paragraph.
    private int paragraphEnd;

    private static final Hashtable<TextAttribute, Object> map
            = new Hashtable<TextAttribute, Object>();

    private BufferedImage processTextElement(BufferedImage image, Element textElement) throws IOException, IM4JavaException, InterruptedException {

        int x = Integer.valueOf(textElement.getAttributeValue("X"));
        int y = Integer.valueOf(textElement.getAttributeValue("Y"));
        int width = Integer.valueOf(textElement.getAttributeValue("Width"));
        int height = Integer.valueOf(textElement.getAttributeValue("Height"));
        String alignment = textElement.getAttributeValue("TextAlignment");
        boolean multiline = Boolean.valueOf(textElement.getAttributeValue("Multiline").toLowerCase());
        boolean antiAlias = textElement.getAttributeValue("TextQuality").equalsIgnoreCase("antialias");
        String textColor = "#" + Integer.toHexString(Integer.valueOf(textElement.getAttributeValue("ForeColor"))).substring(2);
        
        // now get the text
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

        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);
        IMOperation op = new IMOperation();
        op.background("none");
        op.size(width, height);
        op = parseText(op, text, textElement.getAttributeValue("Font"), textColor);
        op.gravity(gravityMap.get(alignment));
        op.addImage("png:-");
        convert.createScript("/tmp/images/myscript.sh", op);
        convert.run(op);

        BufferedImage tmpImage = s2b.getImage();

        // compose over current image
        CompositeCmd command = new CompositeCmd();
        op = new IMOperation();
        // the image is alrready resized, so we have to fill only x and y
        op.geometry(null, null, x, y);
        // compose putting source image over destination image
        op.compose("Src_Over");
        op.addImage(2);
        op.addImage("png:-");
        s2b = new Stream2BufferedImage();
        command.setOutputConsumer(s2b);
        command.run(op, tmpImage, image);

        // retrieve image
        image = s2b.getImage();

//        logger.info("Saving image...");
//        ScreenImage.writeImage(image, "/tmp/images/image" + textElement.getAttributeValue("Name") + ".png");
//
//        BufferedImage tmpImage;
//        if (width > 0 && height > 0) {
//            // create a transparent tmpImage
//            tmpImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        } else {
////            FontMetrics fm = g2.getFontMetrics(font);
////            Rectangle outlineBounds = fm.getStringBounds(text, g2).getBounds();
////         we need to create a transparent image to paint
////            tmpImage = new BufferedImage(outlineBounds.width, outlineBounds.height, BufferedImage.TYPE_INT_RGB);
//        }
////        Graphics2D g2d = tmpImage.createGraphics();
////        g2d.setFont(font);
//
////        g2d.setColor(textColor);
////        drawString(g2d, text, new Rectangle(0, 0, width, height), Align.valueOf(alignment), 0, multiline);
////        tmpImage = processActions(textElement, tmpImage);
//////        Graphics2D g2d = tmpImage.createGraphics();
////        // set current font
////        g2.setFont(font);
//////        g2d.setComposite(AlphaComposite.Clear);
//////        g2d.fillRect(0, 0, width, height);
//////        g2d.setComposite(AlphaComposite.Src);
////        // TODO: we have to parse it
////        int strokeWidth = Integer.valueOf(textElement.getAttributeValue("StrokeWidth"));
////        // the color of the outline
////        if (strokeWidth > 0) {
//////            Color strokeColor = new Color(Integer.valueOf(textElement.getAttributeValue("StrokeColor")));
//////            AffineTransform affineTransform;
//////            affineTransform = g2d.getTransform();
//////            affineTransform.translate(width / 2 - (outlineBounds.width / 2), height / 2
//////                    + (outlineBounds.height / 2));
//////            g2d.transform(affineTransform);
//////            // backup stroke width and color
//////            Stroke originalStroke = g2d.getStroke();
//////            Color originalColor = g2d.getColor();
//////            g2d.setColor(strokeColor);
//////            g2d.setStroke(new BasicStroke(strokeWidth));
//////            g2d.draw(shape);
//////            g2d.setClip(shape);
//////            // restore stroke width and color
//////            g2d.setStroke(originalStroke);
//////            g2d.setColor(originalColor);
////        }
//////        // get the text color
////        Color textColor = new Color(Integer.valueOf(textElement.getAttributeValue("ForeColor")));
////        g2.setColor(textColor);
//////        g2d.setBackground(Color.BLACK);
//////        g2d.setStroke(new BasicStroke(2));
//////        g2d.setColor(Color.WHITE);
////        // draw the text
////
////        drawString(g2, text, new Rectangle(x, y, width, height), Align.valueOf(alignment), 0, multiline);
////        g2.drawString(text, x, y);
////        Rectangle rect = new Rectangle(x, y, width, height); // defines the desired size and position
////        FontMetrics fm = g2.getFontMetrics();
////        FontRenderContext frc = g2.getFontRenderContext();
////        TextLayout tl = new TextLayout(text, g2.getFont(), frc);
////        AffineTransform transform = new AffineTransform();
////        transform.setToTranslation(rect.getX(), rect.getY());
////        if (Boolean.valueOf(textElement.getAttributeValue("AutoSize").toLowerCase())) {
////            double scaleY
////                    = rect.getHeight() / (double) (tl.getOutline(null).getBounds().getMaxY()
////                    - tl.getOutline(null).getBounds().getMinY());
////            transform.scale(rect.getWidth() / (double) fm.stringWidth(text), scaleY);
////        }
////        Shape shape = tl.getOutline(transform);
////        g2.setClip(shape);
////        g2.fill(shape.getBounds());
//        if (antiAlias) {
//            // we need to restore antialias to none
////            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//        }
////        g2.drawString(text, x, y);
//
//        // alway resize
////        BicubicScaleFilter scaleFilter = new BicubicScaleFilter(width, height);
////        tmpImage = scaleFilter.filter(tmpImage, null);
//        // draw the image to the source
////        g2.drawImage(tmpImage, x, y, width, height, null);
////        try {
////            ScreenImage.writeImage(tmpImage, "/tmp/images/" + textElement.getAttributeValue("Name") + ".png");
////        } catch (IOException ex) {
////
////        }
        logger.info("{} processed...", textElement.getAttributeValue("Name"));
        // return processed image
        return image;
    }

    private BufferedImage processActions(Element imageElement, BufferedImage tmpImage) throws IM4JavaException, InterruptedException, IOException {
        BufferedImage result = tmpImage;
        // verify if there are filters
        Element actions = imageElement.getChild("Actions");
        if (actions != null) {
            List<Element> filters = actions.getChildren();
            for (Element filter : filters) {
                Stream2BufferedImage s2b = new Stream2BufferedImage();
                // TODO: implement filters
                switch (filter.getName()) {
                    // Crop
                    case "Crop":
                        break;
                    // GlassTable
                    case "GlassTable":
                        break;
                    // Glow
                    case "Glow":
                        break;
                    // GaussianBlur
                    case "GaussianBlur":
                        double sigma = Double.valueOf(filter.getAttributeValue("Radius"));
                        double radius = sigma * 3;
                        convert.setOutputConsumer(s2b);
                        IMOperation op = new IMOperation();
                        op.addImage();
                        op.blur(sigma, radius);
                        op.addImage("png:-");
                        convert.run(op, tmpImage);
                        result = s2b.getImage();
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
                        int opacity = Integer.valueOf(filter.getAttributeValue("Opacity")) * 255 / 100;
                        OpacityFilter opacityFilter = new OpacityFilter(opacity);
                        result = opacityFilter.filter(tmpImage, null);
                        break;
                    // PerspectiveView
                    case "PerspectiveView":
                        break;
                    // Rotate
                    case "Rotate":
                        break;
                    // DropShadow
                    case "DropShadow":
                        break;
                    // Skew
                    case "Skew":
                        break;
                    // Flip
                    case "Flip":
                        break;

                }
            }
        }
        return result;
    }

    private IMOperation parseText(IMOperation op, String text, String fontSpecs, String textColor) {
        // split attribute value to get font name and carachteristics
        // bold, italic, underline, and strikeout.
        // Size and Unit (Point, Pixel, Millimeter, Inch)
        String[] fontSpecsArray = fontSpecs.split(",");
        boolean isBold = false;
        boolean isItalic = false;
        boolean isStrikeout = false;
        boolean isUnderline = false;
        // TODO: manage unit
        String unit;
        // font name
        String fontName = fontSpecsArray[0];
        int size = Integer.valueOf(fontSpecsArray[1]);
        if (fontSpecsArray.length > 5) {
            // her we have a full description
            // bold
            isBold = Boolean.valueOf(fontSpecsArray[2]);
            // italic
            isItalic = Boolean.valueOf(fontSpecsArray[3]);
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
        op.font(fontName);
        op.pointsize(size);
        List<String> rawArgs = new ArrayList<>();
        rawArgs.add("-define");
        rawArgs.add("pango:justify=true");

        String pangoText = text;
        if (isBold) {
            pangoText = "<b>" + pangoText + "</b>";
        }
        if (isItalic) {
            pangoText = "<i>" + pangoText + "</i>";
        }
        if (isStrikeout) {
            pangoText = "<s>" + pangoText + "</s>";
        }
        if (isUnderline) {
            pangoText = "<u>" + pangoText + "</u>";
        }
        pangoText = "pango:<span fgcolor='"+textColor+"'>" + pangoText + "</span>";
        rawArgs.add(pangoText);
        op.addRawArgs(rawArgs);
        return op;
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

//    public void drawString(Graphics g, String text, RectangularShape bounds, Align align, double angle, boolean multiline) {
//        Graphics2D g2 = (Graphics2D) g;
//        Font font = g2.getFont();
//        if (angle != 0) {
//            g2.setFont(font.deriveFont(AffineTransform.getRotateInstance(Math.toRadians(angle))));
//        }
//
//        Rectangle2D sSize = g2.getFontMetrics().getStringBounds(text, g2);
//        Point2D pos = getPoint(bounds, align);
//        double x = pos.getX();
//        double y = pos.getY() + sSize.getHeight();
//
//        switch (align) {
//            case TopCenter:
//            case BottomCenter:
//            case Center:
//                x -= (sSize.getWidth() / 2);
//                break;
//            case TopRight:
//            case MiddleRight:
//            case BottomRight:
//                x -= (sSize.getWidth());
//                break;
//            case BottomLeft:
//            case MiddleLeft:
//            case TopLeft:
//                break;
//        }
//        if (multiline) {
//            // Create a new LineBreakMeasurer from the paragraph.
//            // It will be cached and re-used.
//            //if (lineMeasurer == null) {
//            AttributedCharacterIterator paragraph = new AttributedString(text).getIterator();
//            paragraphStart = paragraph.getBeginIndex();
//            paragraphEnd = paragraph.getEndIndex();
//            FontRenderContext frc = g2.getFontRenderContext();
//            lineMeasurer = new LineBreakMeasurer(paragraph, frc);
//        //}
//
//            // Set break width to width of Component.
//            float breakWidth = (float) bounds.getWidth();
//            float drawPosY = (float) y;
//            // Set position to the index of the first character in the paragraph.
//            lineMeasurer.setPosition(paragraphStart);
//
//            // Get lines until the entire paragraph has been displayed.
//            while (lineMeasurer.getPosition() < paragraphEnd) {
//
//                // Retrieve next layout. A cleverer program would also cache
//                // these layouts until the component is re-sized.
//                TextLayout layout = lineMeasurer.nextLayout(breakWidth);
//
//                // Compute pen x position. If the paragraph is right-to-left we
//                // will align the TextLayouts to the right edge of the panel.
//                // Note: this won't occur for the English text in this sample.
//                // Note: drawPosX is always where the LEFT of the text is placed.
//                float drawPosX = layout.isLeftToRight()
//                        ? (float) x : (float) x + breakWidth - layout.getAdvance();
//
//                // Move y-coordinate by the ascent of the layout.
//                drawPosY += layout.getAscent();
//
//                // Draw the TextLayout at (drawPosX, drawPosY).
//                layout.draw(g2, drawPosX, drawPosY);
//
//                // Move y-coordinate in preparation for next layout.
//                drawPosY += layout.getDescent() + layout.getLeading();
//            }
//        } else {
//            g2.drawString(text, (float) x, (float) y);
//        }
//        g2.setFont(font);
//    }
}
