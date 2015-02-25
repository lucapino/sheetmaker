package com.github.lucapino.sheetmaker;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FontStyleSizeFamilyChooser extends JFrame {

    public static void main(String[] args) {
        new FontStyleSizeFamilyChooser();
    }

    private JLabel sampleText = new JLabel("Label");

    private JComboBox fontComboBox;

    private JComboBox sizeComboBox;

    private JCheckBox boldCheck, italCheck;

    private String[] fonts;

    public FontStyleSizeFamilyChooser() {
        this.setSize(500, 150);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FontListener fl = new FontListener();
        this.add(sampleText, BorderLayout.NORTH);
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fonts = g.getAvailableFontFamilyNames();

        JPanel controlPanel = new JPanel();
        fontComboBox = new JComboBox(fonts);
        fontComboBox.addActionListener(fl);
        controlPanel.add(new JLabel("Family: "));
        controlPanel.add(fontComboBox);

        Integer[] sizes = {7, 8, 9, 10, 11, 12, 14, 18, 20, 22, 24, 36};

        sizeComboBox = new JComboBox(sizes);
        sizeComboBox.setSelectedIndex(5);
        sizeComboBox.addActionListener(fl);
        controlPanel.add(new JLabel("Size: "));
        controlPanel.add(sizeComboBox);

        boldCheck = new JCheckBox("Bold");
        boldCheck.addActionListener(fl);
        controlPanel.add(boldCheck);

        italCheck = new JCheckBox("Ital");
        italCheck.addActionListener(fl);
        controlPanel.add(italCheck);

        this.add(controlPanel, BorderLayout.SOUTH);
        fl.updateText();

        this.setVisible(true);
    }

    private class FontListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            updateText();
        }

        public void updateText() {
            String name = (String) fontComboBox.getSelectedItem();

            Integer size = (Integer) sizeComboBox.getSelectedItem();

            int style;
            if (boldCheck.isSelected() && italCheck.isSelected()) {
                style = Font.BOLD | Font.ITALIC;
            } else if (boldCheck.isSelected()) {
                style = Font.BOLD;
            } else if (italCheck.isSelected()) {
                style = Font.ITALIC;
            } else {
                style = Font.PLAIN;
            }

            Font f = new Font(name, style, size.intValue());
            sampleText.setFont(f);
        }
    }
}
