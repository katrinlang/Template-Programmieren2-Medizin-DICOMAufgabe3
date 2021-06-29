package org.htw.prog2.aufgabe2;

import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DICOMImage {
    private ArrayList<DICOMFrame> frames = new ArrayList<>();
    private String name;

    public DICOMImage(File infile, String name) {
        this.name = name;
        try {
            ImageIO.scanForPlugins();
            ImageReader ir = ImageIO.getImageReadersByFormatName("DICOM").next();
            DicomImageReadParam param = (DicomImageReadParam) ir.getDefaultReadParam();
            ImageInputStream iis = ImageIO.createImageInputStream(infile);
            ir.setInput(iis);
            for(int i=0; ; i++) {
                BufferedImage image = ir.read(i, param);
                frames.add(new DICOMFrame(image));
            }
        } catch (IOException e) {
            System.out.println("Error reading image file " + infile.getAbsolutePath() + ": " + e.getMessage());
        } catch(IndexOutOfBoundsException e) {
        }
    }

    public void writeFrames(int from, int to, boolean original, boolean edges, double edgeLightnessCutoff) {
        for(int i=from; i<frames.size() && i<to; i++) {
            if(original) {
                String outfilename = name + "_" + i + ".png";
                writeImage(frames.get(i).getImage(), outfilename);
            }
            if(edges) {
                String outfilename = name + "_" + i + "_edges.png";
                writeImage(frames.get(i).getEdges(edgeLightnessCutoff), outfilename);
            }
        }
    }

    private void writeImage(BufferedImage image, String filename) {
        File outfile = new File(filename);
        try {
            ImageIO.write(image, "png", outfile);
        } catch (IOException e) {
            System.out.println("Could not write file " + filename + ": " + e.getMessage());
        }
    }

    public int getNumFrames() {
        return frames.size();
    }

    public DICOMFrame getFrame(int num) {
        return frames.get(num);
    }
}
