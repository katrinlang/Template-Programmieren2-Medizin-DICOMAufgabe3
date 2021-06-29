package org.htw.prog2.aufgabe2.gui;

import org.htw.prog2.aufgabe2.DICOMFrame;
import org.htw.prog2.aufgabe2.DICOMImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedList;

public class ImageListPanel extends JPanel {
    private DICOMImage image;
    private JPanel thumbnailPanel;
    private LinkedList<FrameThumbnail> markedFrames;
    private MainFrame mainFrame;
    private FrameThumbnail selectedThumbnail = null;

    public ImageListPanel(int width, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        JLabel name = new JLabel("Bilder");
        thumbnailPanel = new JPanel();
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        add(name);
        add(Box.createVerticalStrut(10));
        name.setAlignmentX(0);
        setPreferredSize(new Dimension(width, 400));
    }

    public void setSelectedFrame(DICOMFrame frame) {
    }

    public void setImage(DICOMImage newImage) {
    }

    private void updateSize() {
    }

    public void addMarkedFrame(DICOMFrame frame) {
    }

    public void removeMarkedFrame(DICOMFrame frame) {
    }
}
