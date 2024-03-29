package com.barco.raad.model;

import com.barco.raad.comparison.ImageComparisonUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Data transfer objects which contains all the needed data for result of the comparison.
 */
public class ImageComparisonResult {

    /**
     * {@link BufferedImage} object of the expected.
     */
    private BufferedImage expected;

    /**
     * {@link BufferedImage} object of the actual.
     */
    private BufferedImage actual;

    /**
     * {@link BufferedImage} object of the comparison result.
     */
    private BufferedImage result;

    /**
     * State of the comparison.
     */
    private ImageComparisonState imageComparisonState;
    /**
     * The difference percentage between two images.
     */
    private double differencePercent;

    private List<Rectangle> rectangles;

    /**
     * Create default instance of the {@link ImageComparisonResult} with {@link ImageComparisonState#SIZE_MISMATCH}.
     *
     * @param expected expected {@link BufferedImage} object.
     * @param actual actual {@link BufferedImage} object.
     * @param differencePercent the percent of the differences between images.
     * @return instance of the {@link ImageComparisonResult} object.
     */
    public static ImageComparisonResult defaultSizeMisMatchResult(BufferedImage expected, BufferedImage actual, List<Rectangle> rectangles, double differencePercent) {
        return new ImageComparisonResult().setImageComparisonState(ImageComparisonState.SIZE_MISMATCH).setRectangles(rectangles)
            .setDifferencePercent(differencePercent).setExpected(expected).setActual(actual).setResult(actual);
    }

    /**
     * Create default instance of the {@link ImageComparisonResult} with {@link ImageComparisonState#MISMATCH}.
     *
     * @param expected expected {@link BufferedImage} object.
     * @param actual actual {@link BufferedImage} object.
     * @return instance of the {@link ImageComparisonResult} object.
     */
    public static ImageComparisonResult defaultMisMatchResult(BufferedImage expected, BufferedImage actual, List<Rectangle> rectangles, double differencePercent) {
        return new ImageComparisonResult().setImageComparisonState(ImageComparisonState.MISMATCH).setRectangles(rectangles)
                .setDifferencePercent(differencePercent).setExpected(expected).setActual(actual).setResult(actual);
    }

    /**
     * Create default instance of the {@link ImageComparisonResult} with {@link ImageComparisonState#MATCH}.
     *
     * @param expected expected {@link BufferedImage} object.
     * @param actual actual {@link BufferedImage} object.
     * @return instance of the {@link ImageComparisonResult} object.
     */
    public static ImageComparisonResult defaultMatchResult(BufferedImage expected, BufferedImage actual, List<Rectangle> rectangles, double differencePercent) {
        return new ImageComparisonResult().setImageComparisonState(ImageComparisonState.MATCH).setRectangles(rectangles)
                .setDifferencePercent(differencePercent).setExpected(expected).setActual(actual).setResult(actual);
    }

    /**
     * Save the image to the provided {@link File} object.
     *
     * @param file the provided {@link File} object.
     * @return this {@link ImageComparisonResult} object.
     */
    public ImageComparisonResult writeResultTo(File file) {
        ImageComparisonUtil.saveImage(file, result);
        return this;
    }

    public BufferedImage getExpected() {
        return expected;
    }

    public ImageComparisonResult setExpected(BufferedImage expected) {
        this.expected = expected;
        return this;
    }

    public BufferedImage getActual() {
        return actual;
    }

    public ImageComparisonResult setActual(BufferedImage actual) {
        this.actual = actual;
        return this;
    }

    public BufferedImage getResult() {
        return result;
    }

    public ImageComparisonResult setResult(BufferedImage result) {
        this.result = result;
        return this;
    }

    public ImageComparisonState getImageComparisonState() {
        return imageComparisonState;
    }

    public ImageComparisonResult setImageComparisonState(ImageComparisonState imageComparisonState) {
        this.imageComparisonState = imageComparisonState;
        return this;
    }

    public double getDifferencePercent() {
        return differencePercent;
    }

    ImageComparisonResult setDifferencePercent(double differencePercent) {
        this.differencePercent = differencePercent;
        return this;
    }

    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    public ImageComparisonResult setRectangles(List<Rectangle> rectangles) {
        this.rectangles = rectangles;
        return this;
    }
}
