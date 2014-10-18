/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker;

import com.github.lucapino.sheetmaker.collectors.DataRetriever;
import com.github.lucapino.sheetmaker.collectors.tmdb.DataRetrieverImpl;
import com.github.lucapino.sheetmaker.model.Artwork;
import com.github.lucapino.sheetmaker.model.movie.Movie;
import com.github.lucapino.sheetmaker.parsers.InfoRetriever;
import com.github.lucapino.sheetmaker.parsers.MovieInfo;
import com.github.lucapino.sheetmaker.parsers.mediainfo.MediaInfoRetriever;
import com.github.lucapino.sheetmaker.renderer.TemplateFilter;
import com.github.lucapino.sheetmaker.renderer.TemplateRenderer;
import com.github.lucapino.sheetmaker.utils.ScreenImage;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tagliani
 */
public class PreviewJFrame extends JFrame {

    private final static Logger logger = LoggerFactory.getLogger(PreviewJFrame.class);

    public PreviewJFrame() throws HeadlessException {
        this.setSize(240, 360);
    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return (AlphaComposite.getInstance(type, alpha));
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)
        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

//    @Override
//    public void paint(Graphics g) {
//        long now = System.nanoTime();
//        Graphics2D g2 = (Graphics2D) g;
//        try {
////            BufferedImage in = ImageIO.read(this.getClass().getResource("/templates/default/logo/wdtvhd.png"));
////            BufferedImage originalImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
////            int newW = (int) (originalImage.getWidth() * 0.50);
////            int newH = (int) (originalImage.getHeight() * 0.50);
////            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
////                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
////            ShadowRenderer renderer = new ShadowRenderer();
////            BufferedImage shadow = renderer.createShadow(in);
////            int newShadowW = (int) (shadow.getWidth() * 0.50);
////            int newShadowH = (int) (shadow.getHeight() * 0.50);
////            g2.drawImage(shadow, 200, 300, newShadowW, newShadowH, null);
////            g2.drawImage(in, 200, 300, newW, newH, null);
////            /*
////             Composite originalComposite = g2.getComposite();
////             g2.setComposite(makeComposite(0.05F));
////             g2.setColor(Color.red);
////             Rectangle redSquare = new Rectangle(200, 300, 100, 100);
////             g2.fill(redSquare);
////             g2.setComposite(originalComposite);
////             */
////            BufferedImage bi = ScreenImage.createImage(getContentPane());
////            ScreenImage.writeImage(bi, "/home/tagliani/tmp/image.png");
////            ------------------------------------------
//            BufferedImage overlay = makeRoundedCorner(ImageIO.read(new File("/home/tagliani/Immagini/overlayframe.png")), 10);
////            BufferedImage backdrop = makeRoundedCorner(ImageIO.read(new File("/home/tagliani/Immagini/backdrops.png")), 10);
////            BufferedImage gloss = makeRoundedCorner(ImageIO.read(new File("/home/tagliani/Immagini/covergloss.png")), 10);
////            g2.setColor(Color.BLACK);
////            g2.setStroke(new BasicStroke(2));
////            g2.drawRect(0, 0, 240, 360);
////            g2.drawImage(overlay, 0, 0, 240, 360, null);
//////            g2.drawRect(10, 3, 206, 310);
////            g2.drawImage(backdrop, 10, 3, 206, 310, null);
//////            g2.drawRect(45, 10, 176, 331);
////            g2.drawImage(gloss, 45, 10, 176, 331, null);
//
//            // load starsmask
//            BufferedImage starmask = ImageIO.read(this.getClass().getResource("/templates/default/starmask.png"));
//            BufferedImage stars = ImageIO.read(this.getClass().getResource("/templates/default/star_yellow_24x24.png"));
//
//            // create 7.4 stars
//            float starsNumber = 7.4F;
//            int fullStarsNumber = Math.round(starsNumber);
//            float starFraction = starsNumber - fullStarsNumber;
//
//            // 1 star -> 24px, so 7.4 stars are 24x7.4 -> 178px
//            BufferedImage singleStar = stars.getSubimage(0, 0, 24, 24);
//
//            //Initializing the final image  
//            BufferedImage finalImg = new BufferedImage(Math.round(24 * 7.4F), 24, singleStar.getType());
//            Graphics2D g2i = finalImg.createGraphics();
//            for (int i = 0; i < fullStarsNumber; i++) {
//                g2i.drawImage(singleStar, 24 * i, 0, null);
//            }
//            // crop the last star
//            BufferedImage croppedStar = singleStar.getSubimage(0, 0, Math.round(24 * starFraction), 24);
//            g2i.drawImage(croppedStar, 24 * fullStarsNumber, 0, null);
//
//            g2.drawImage(overlay, 0, 0, 240, 360, null);
////            g2.drawRect(10, 3, 206, 310);
//            Composite originalComposite = g2.getComposite();
//            g2.setComposite(makeComposite(0.3F));
//            g2.drawImage(starmask, 0, 320, null);
//            g2.setComposite(originalComposite);
////            g2.drawRect(45, 10, 176, 331);
//            g2.drawImage(finalImg, 0, 320, null);
//
//            /*
//             # this token is part of the Settings token in the template.xml file
//             my $source=$template_xml->{Template}->{Settings}->{Rating}->{FileName}->{value};
//             next unless DeTokenize($config_options,\$source,$provider_hash,$template_xml);
//             my $rating=$provider_hash->{RATING};
//             $temp->Read($source);
//             my ($width, $height) = $temp->Get('columns', 'rows');
//             # single star
//             if ($width>24) {
//             $temp->Crop(width=>$width/2,height=>$height);
//             }
//             my $rating_image=Image::Magick->new(magick=>'png');
//
//             my ($full_stars,$remainder) = split (/\./, $rating);
//             my $clipboard=Image::Magick->new();
//
//             for (my $count = 1; $count <= $full_stars; $count++) {
//             push(@$clipboard, $rating_image);
//             push(@$clipboard, $temp);
//             $rating_image=$clipboard->Append(stack=>'false');
//             @$clipboard=();
//             }
//
//             if ( $remainder > 0 ) {
//             # add a partial star
//             $temp->Crop(width=>($width/2*$remainder/10), height=>$height);
//             push(@$clipboard, $rating_image);
//             push(@$clipboard, $temp);
//             $rating_image=$clipboard->Append(stack=>'false');
//             @$clipboard=();
//             }
//             */
//            // g.drawImage(overlay, 200, 200, null);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        long time = System.nanoTime() - now;
////        System.out.println(time/(1000 * 1000) + " ms");
//    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    File tmpFolder = new File("/tmp/images");
                    tmpFolder.mkdir();

                    String coverPath = tmpFolder.getAbsolutePath() + "/cover.jpg";
                    String backdropPath = tmpFolder.getAbsolutePath() + "/backdrop.jpg";
                    String fanart1Path = tmpFolder.getAbsolutePath() + "/fanart1.jpg";
                    String fanart2Path = tmpFolder.getAbsolutePath() + "/fanart2.jpg";
                    String fanart3Path = tmpFolder.getAbsolutePath() + "/fanart3.jpg";

                    PreviewJFrame frame = new PreviewJFrame();
                    logger.info("Creating parser...");
                    InfoRetriever parser = new MediaInfoRetriever();
                    MovieInfo movieInfo = parser.getMovieInfo("/media/Elements/Film/JackRyan.mkv");
                    logger.info("Retrieving movie file info...");
                    logger.info("Creating dataretriever...");
                    DataRetriever retriever = new DataRetrieverImpl();
                    logger.info("Retrieving movie data...");
                    Movie movie = retriever.retrieveMovieFromImdbID("tt1205537", "IT");
                    logger.info("Retrieving backdrops and fanart...");
                    List<Artwork> images = movie.getBackdrops();
                    // background
                    logger.info("Saving backdrop...");
                    IOUtils.copyLarge(new URL(images.get(0).getImageURL()).openStream(), new FileOutputStream(backdropPath));
                    for (int i = 1; i < 4; i++) {
                        // fanart1
                        // fanart2
                        // fanart3
                        String imageURL = images.get(i).getImageURL();
                        logger.info("Saving fanart{}...", i);
                        IOUtils.copyLarge(new URL(imageURL).openStream(), new FileOutputStream(tmpFolder.getAbsolutePath() + "/fanart" + i + ".jpg"));
                    }
                    // cover
                    logger.info("Retrieving cover...");
                    Artwork cover = movie.getPosters().get(0);
                    String imageURL = cover.getImageURL();
                    logger.info("Saving cover...");
                    IOUtils.copyLarge(new URL(imageURL).openStream(), new FileOutputStream(coverPath));
                    
                    Map<String, String> tokenMap = TemplateFilter.createTokenMap(movie, movieInfo, null);
                    
                    logger.info("Creating renderer...");
                    TemplateRenderer renderer = new TemplateRenderer();
                    JPanel imagePanel = null;
                    try {
                        logger.info("Rendering image...");
                        imagePanel = renderer.renderTemplate(this.getClass().getResource("/templates/simplicity/template.xml"), tokenMap, backdropPath, fanart1Path, fanart2Path, fanart3Path, coverPath);
                        logger.info("Adding image to frame...");
                        frame.add(imagePanel);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    imagePanel.setPreferredSize(new Dimension(imagePanel.getWidth(), imagePanel.getHeight()));
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    frame.setVisible(true);
                    frame.pack();
                    logger.info("Creating image for save...");
                    BufferedImage imageTosave = ScreenImage.createImage(imagePanel);
                    logger.info("Saving image...");
                    ScreenImage.writeImage(imageTosave, "/tmp/images/final.jpg");
                    logger.info("Image saved...");
                } catch (Exception ex) {
                    logger.error("Error: ", ex);
                }
            }

        });

    }
}
