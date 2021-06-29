package org.htw.prog2.aufgabe2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class DICOMFrame {
    private static final double[][] S_x = new double[][]{new double[]{-1,0,1}, new double[]{-2,0,2}, new double[]{-1,0,1}};
    private static final double[][] S_y = new double[][]{new double[]{-1,-2,-1}, new double[]{0,0,0}, new double[]{1,2,1}};
    private double brightness = 1;
    private LinkedList<DICOMFrameMark> marks = new LinkedList<>();

    private BufferedImage image;
    private BufferedImage edges;
    private BufferedImage combined;

    public DICOMFrame(BufferedImage image) {
        this.image = image;
    }

    private int getGrayscalePixel(BufferedImage image, int x, int y) {
        Color c = new Color(image.getRGB(x, y));
        int r = (int)(c.getRed() * 0.299);
        int g = (int)(c.getGreen() * 0.715);
        int b = (int)(c.getBlue() * 0.072);
        return r+g+b;
    }

    private void detectEdges() {
        int width = image.getWidth();
        int height = image.getHeight();

        int edgeWidth = width - 2;
        int edgeHeight = height - 2;

        edges = new BufferedImage(edgeWidth, edgeHeight, BufferedImage.TYPE_BYTE_GRAY);
        int[][] edgesArray = new int[edgeWidth][edgeHeight];
        double maxValue = 0;

        for(int x=1; x<width-1; x++) {
            for(int y=1; y<height-1; y++) {
                double G_x = (S_x[0][0] * getGrayscalePixel(image,x-1, y-1))   + (S_x[0][1] * getGrayscalePixel(image,x, y-1)) + (S_x[0][2] * getGrayscalePixel(image,x+1, y-1)) +
                        (S_x[1][0] * getGrayscalePixel(image,x-1, y))   + (S_x[1][1] * getGrayscalePixel(image,x, y)) + (S_x[1][2] * getGrayscalePixel(image,x+1, y)) +
                        (S_x[2][0] * getGrayscalePixel(image,x-1, y+1))   + (S_x[2][1] * getGrayscalePixel(image,x, y+1)) + (S_x[2][2] * getGrayscalePixel(image,x+1, y+1));
                double G_y = (S_y[0][0] * getGrayscalePixel(image,x-1, y-1))   + (S_y[0][1] * getGrayscalePixel(image,x, y-1)) + (S_y[0][2] * getGrayscalePixel(image,x+1, y-1)) +
                        (S_y[1][0] * getGrayscalePixel(image,x-1, y))   + (S_y[1][1] * getGrayscalePixel(image,x, y)) + (S_y[1][2] * getGrayscalePixel(image,x+1, y)) +
                        (S_y[2][0] * getGrayscalePixel(image,x-1, y+1))   + (S_y[2][1] * getGrayscalePixel(image,x, y+1)) + (S_y[2][2] * getGrayscalePixel(image,x+1, y+1));
                int newValue = Math.abs((int) Math.sqrt((G_x * G_x) + (G_y * G_y)));
                edgesArray[x-1][y-1]=newValue;
                if(newValue > maxValue) {
                    maxValue = newValue;
                }
            }
        }
        double scale = 255/maxValue;
        for(int x=0; x<edgeWidth; x++) {
            for(int y=0; y<edgeHeight; y++) {
                int col = Math.min((int)(edgesArray[x][y]*scale*brightness), 255);
                edges.setRGB(x, y, new Color(col, col, col).getRGB());
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getEdges(double brightness) {
        if(edges == null || this.brightness != brightness) {
            this.brightness = brightness;
            detectEdges();
        }
        return edges;
    }

    public void addMark(DICOMFrameMark mark) {
    }

    public LinkedList<DICOMFrameMark> getMarks() {
        return marks;
    }

    public void removeMark(DICOMFrameMark mark) {

    }
}
