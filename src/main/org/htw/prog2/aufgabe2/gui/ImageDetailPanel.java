package org.htw.prog2.aufgabe2.gui;

import org.htw.prog2.aufgabe2.DICOMFrame;
import org.htw.prog2.aufgabe2.DICOMFrameMark;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ImageDetailPanel extends JPanel {
    private MainFrame mainFrame;
    private DICOMFrame frame;
    private boolean showEdges;

    public ImageDetailPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                if(mouseWheelEvent.getWheelRotation() < 0) {
                    mainFrame.setPreviousDetailFrame();
                }
                else {
                    mainFrame.setNextDetailFrame();
                }
            }
        });

    }

    public ImageDetailPanel getThis() {
        return this;
    }

    public void paintComponent(Graphics g) {
        if(frame == null) {
            g.setColor(Color.BLACK);
            g.drawString("Bitte ein Bild laden", getWidth()/2, getHeight()/2);
        }
        else {
            if(showEdges) {
                g.drawImage(frame.getEdges(10), 0, 0, null);
            } else {
                g.drawImage(frame.getImage(), 0, 0, null);
            }
        }
    }

    public void setDetailFrame(DICOMFrame frame, boolean showEdges) {
        this.showEdges = showEdges;
        this.frame = frame;
        setPreferredSize(new Dimension(frame.getImage().getWidth(), frame.getImage().getHeight()));
    }
}
