package org.htw.prog2.aufgabe2.gui;

import org.htw.prog2.aufgabe2.DICOMFrame;
import org.htw.prog2.aufgabe2.DICOMImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainFrame extends JFrame {
    private ImageListPanel imagelist;
    private ImageDetailPanel detailPanel;
    private JSlider seriesSlider;
    private JLabel frameNumberLabel;
    private DICOMImage image;
    private JCheckBox edgeCheckBox;

    public MainFrame() {
        super("DICOM-Diagnosetool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        initMenu();
        initPanels();
        setSize(630, 550);
    }

    private void initMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem loadFile = new JMenuItem("Öffnen");
        JMenuItem exit = new JMenuItem("Beenden");
        bar.add(fileMenu);
        fileMenu.add(loadFile);
        fileMenu.add(exit);
        setJMenuBar(bar);
        loadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
                chooser.setFileFilter(new FileNameExtensionFilter("DICOM file", "DCM"));
                if(chooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    image = new DICOMImage(file, file.getName());
                    imagelist.setImage(image);
                    revalidate();
                }
            }
        });
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
    }

    public void addMarkedFrame(DICOMFrame frame) {
    }


    public void setDetailSeries(DICOMImage image) {
        seriesSlider.setEnabled(true);
        seriesSlider.setMinimum(0);
        seriesSlider.setMaximum(image.getNumFrames()-1);
        seriesSlider.setValue(0);
        frameNumberLabel.setText("Bild 1/"+(image.getNumFrames()));
        edgeCheckBox.setEnabled(true);
        setDetailFrame(image.getFrame(0), edgeCheckBox.isSelected());
    }

    public void setDetailFrame(DICOMFrame frame) {
        setDetailFrame(frame, edgeCheckBox.isSelected());
    }


    public void setDetailFrame(DICOMFrame frame, boolean showEdges) {
        detailPanel.setDetailFrame(frame, showEdges);
        imagelist.setSelectedFrame(frame);
        revalidate();
        repaint();
    }

    public void setPreviousDetailFrame() {
        seriesSlider.setValue(seriesSlider.getValue()-1);
    }

    public void setNextDetailFrame() {
        seriesSlider.setValue(seriesSlider.getValue()+1);
    }

    public void updateDetailView() {
        int frameNumber = seriesSlider.getValue();
        setDetailFrame(image.getFrame(frameNumber), edgeCheckBox.isSelected());
        frameNumberLabel.setText("Bild " + (frameNumber+1) + "/"+(image.getNumFrames()));
    }

    private void initPanels() {
        imagelist = new ImageListPanel(80, this);
        add(imagelist, BorderLayout.WEST);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new JLabel("Detailansicht"), BorderLayout.NORTH);
        detailPanel = new ImageDetailPanel(this);
        centerPanel.add(detailPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        seriesSlider = new JSlider();
        seriesSlider.setEnabled(false);
        seriesSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                updateDetailView();
            }
        });
        frameNumberLabel = new JLabel("Bild:");
        controlPanel.add(frameNumberLabel);
        controlPanel.add(seriesSlider);
        add(controlPanel, BorderLayout.SOUTH);
        controlPanel.add(new JLabel("Kanten anzeigen "));
        edgeCheckBox = new JCheckBox();
        edgeCheckBox.setEnabled(false);
        edgeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateDetailView();
            }
        });
        controlPanel.add(edgeCheckBox);
    }

    public void removeMarkedFrame(DICOMFrame frame) {
    }
}
