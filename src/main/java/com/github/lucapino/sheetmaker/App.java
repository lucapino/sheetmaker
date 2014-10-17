/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker;

import com.github.lucapino.sheetmaker.renderer.ImagePanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author Luca Tagliani
 */
public class App {

    public static void main(String[] args) {
        App app = new App();
        app.displayFrame();
    }

    private void displayFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        BufferedImage image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("Test", 200, 100);
        ImagePanel panel = new ImagePanel(image);
        
        panel.setPreferredSize(new Dimension(1024,768));
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        
    }

}
