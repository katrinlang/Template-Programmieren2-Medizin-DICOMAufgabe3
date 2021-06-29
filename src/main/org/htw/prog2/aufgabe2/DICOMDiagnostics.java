package org.htw.prog2.aufgabe2;

import org.htw.prog2.aufgabe2.gui.MainFrame;

import java.io.File;

public class DICOMDiagnostics {

    public static void main(String[] args) {
//        DICOMImage image = new DICOMImage(new File("data/angiogram1.DCM"), "Angiogram");
//        image.writeFrames(50, 60, false, true,10);
        MainFrame main = new MainFrame();
        main.setVisible(true);
    }
}
