package org.htw.prog2.aufgabe2.gui;

import org.htw.prog2.aufgabe2.DICOMFrame;
import org.htw.prog2.aufgabe2.DICOMFrameMark;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ImageDetailPanel extends JPanel {
    private MainFrame mainFrame;
    private JPopupMenu rightClickMenu;
    private DICOMFrameMark selectedMark = null;
    private DICOMFrame frame;
    private int markSize = 10;
    private boolean showEdges;

    public ImageDetailPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        JMenuItem deleteItem = new JMenuItem("Marke entfernen");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(selectedMark != null) {
                    frame.removeMark(selectedMark);
                    if(frame.getMarks().size() == 0) {
                        mainFrame.removeMarkedFrame(frame);
                    }
                    repaint();
                }
            }
        });
        rightClickMenu = new JPopupMenu();
        rightClickMenu.add(deleteItem);
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
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(mouseEvent.getButton()==MouseEvent.BUTTON1) {
                    frame.addMark(new DICOMFrameMark(mouseEvent.getX(), mouseEvent.getY(), markSize));
                    if(frame.getMarks().size() == 1) {
                        mainFrame.addMarkedFrame(frame);
                    }
                    repaint();
                }
                if(mouseEvent.getButton()==MouseEvent.BUTTON3) {
                    for(DICOMFrameMark mark : frame.getMarks()) {
                        if(mark.isInMark(mouseEvent.getX(), mouseEvent.getY())) {
                            selectedMark = mark;
                            rightClickMenu.show(getThis(), mouseEvent.getX(), mouseEvent.getY());
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

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
            for(DICOMFrameMark mark : frame.getMarks()) {
                g.setColor(Color.BLUE);
                g.drawRect(mark.getX()-mark.getSize()/2, mark.getY()-mark.getSize()/2, mark.getSize(), mark.getSize());
                g.setColor(Color.YELLOW);
                g.drawRect(mark.getX()-(mark.getSize()-2)/2, mark.getY()-(mark.getSize()-2)/2, mark.getSize()-2, mark.getSize()-2);
            }
        }
    }

    public void setDetailFrame(DICOMFrame frame, boolean showEdges) {
        this.showEdges = showEdges;
        this.frame = frame;
        this.selectedMark = null;
        setPreferredSize(new Dimension(frame.getImage().getWidth(), frame.getImage().getHeight()));
    }
}
