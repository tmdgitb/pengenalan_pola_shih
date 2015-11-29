package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.opencv.core.Core.circle;
import static org.opencv.core.Core.rectangle;

/**
 * Created by Yusfia Hafid A on 11/17/2015.
 */
public class BoundingObject {
    private static final Logger log = LoggerFactory.getLogger(BoundingObject.class);
    private int x_min;
    private int y_min;
    private int x_max;
    private int y_max;
    //nose
    private int x_nose;
    private int y_nose;
    //lips
    private int x_lips_left;
    private int y_lips_left;
    private int x_lips_right;
    private int y_lips_right;
    private ArrayList<String> lipsPattern;
    private boolean cek[][];
    int new_row_top;
    //eyes
    private int x_eye_left;
    private int y_eye_left;
    private int x_eye_right;
    private int y_eye_right;

    public BoundingObject() {
    }

    public int getX_min() {
        return x_min;
    }

    public void setX_min(int x_min) {
        this.x_min = x_min;
    }

    public int getY_min() {
        return y_min;
    }

    public void setY_min(int y_min) {
        this.y_min = y_min;
    }

    public int getX_max() {
        return x_max;
    }

    public void setX_max(int x_max) {
        this.x_max = x_max;
    }

    public int getY_max() {
        return y_max;
    }

    public void setY_max(int y_max) {
        this.y_max = y_max;
    }

    public Mat drawBoundingBox(Mat input, Mat grayscale, Mat binarry, Scalar color) {
        if (isFace(input, grayscale)) {
            rectangle(input, new Point(y_min, x_min), new Point(y_max, x_max), color);
            findNose(input, grayscale);
            findLips(input, binarry);
        }
        return input;
    }

    private boolean isFace(Mat input, Mat grayscale) {
        int deltax = x_max - x_min;
        int deltay = y_max - y_min;
        double imagerat = (double) (input.cols() * input.rows()) / (double) (deltax * deltay);
        double ratio = (double) (deltax) / (double) (deltay);
        if (ratio > 1.2 && ratio < 1.8 && imagerat < 90) {
            log.info("rasio = {} deltaX = {}, deltaY = {}, imagerat = {}", ratio, deltax, deltay, imagerat);
            return true;
        }
        return false;
    }

    private void findNose(Mat input, Mat grayscsale) {
        byte[] data = new byte[1];
        grayscsale.get(x_min, y_min, data);
        int temp = Byte.toUnsignedInt(data[0]);
        for (int i = x_min; i < x_max; i++) {
            for (int j = y_min; j < y_max; j++) {
                grayscsale.get(i, j, data);
                int current = Byte.toUnsignedInt(data[0]);
                if (temp < current) {
                    temp = current;
                    x_nose = i;
                    y_nose = j;
                }
            }
        }
        circle(input, new Point(y_nose, x_nose), 2, new Scalar(255, 0, 0), 2);
    }

    private void findLips(Mat input, Mat binarry) {
        cek = new boolean[binarry.rows()][binarry.cols()];
        int chin_nose = x_max - x_nose;
        new_row_top = x_nose + (int) (0.3 * chin_nose);
        //int new_row_bottom = x_nose +(int)(0.7*chin_nose);
        //line(input, new Point(y_min, new_row_top), new Point(y_max, new_row_top), new Scalar(255, 0, 0));
        addLipsPattern();
        boolean exist = false;
        for (int i = new_row_top; i < x_max; i++) {
            String linePattern = "";
            for (int j = 0; j < lipsPattern.size(); j++) {
                linePattern = getLinePattern(binarry, i, y_min, y_max);
                if (linePattern.equals(lipsPattern.get(j))) {
                    exist = exist || true;
                    break;
                }
            }
            if (exist) {
                mostLeftAndRightLips(i, binarry, y_min, y_max);
                circle(input, new Point(y_lips_left, x_lips_left), 2, new Scalar(255, 0, 0), 2);
                circle(input, new Point(y_lips_right, x_lips_right), 2, new Scalar(255, 0, 0), 2);
                break;
            }
        }
        //line(input,new Point(y_min,new_row_bottom),new Point(y_max,new_row_bottom),new Scalar(255,0,0));
    }

    private void mostLeftAndRightLips(int row, Mat binnary, int min_y, int max_y) {
        Mat scanLine = binnary.row(row);
        for (int i = min_y; i <= max_y; i++) {
            byte[] data = new byte[1];
            scanLine.get(0, i, data);
            if (Byte.toUnsignedInt(data[0]) == 0 && i == min_y) {
                i++;
                scanLine.get(0, i, data);
                while (Byte.toUnsignedInt(data[0]) != 255) {
                    i++;
                    scanLine.get(0, i, data);
                }
            }
            if (Byte.toUnsignedInt(data[0]) == 0) {
                x_lips_left = row;
                y_lips_left = i;
                x_lips_right = row;
                y_lips_right = i;
                iterateLips(row, i, binnary);
                break;
            }
        }
    }

    private void iterateLips(int i, int j, Mat binnary) {
        if (i>new_row_top && i<x_max && j>y_min && j<y_max) {
            log.info("row = {}, col = {}", i, j);
            cek[i][j] = true;
            if (j < y_lips_left) {
                y_lips_left = j;
                x_lips_left = i;
            }
            if (j > y_lips_right) {
                y_lips_right = j;
                x_lips_right = i;
            }

            if (!cek[i - 1][j] && isBlack(i - 1, j, binnary)) {
                iterateLips(i - 1, j, binnary);
            }
            if (!cek[i][j + 1] && isBlack(i, j + 1, binnary)) {
                iterateLips(i, j + 1, binnary);
            }
            if (!cek[i + 1][j] && isBlack(i + 1, j, binnary)) {
                iterateLips(i + 1, j, binnary);
            }
            if (!cek[i][j - 1] && isBlack(i, j - 1, binnary)) {
                iterateLips(i, j - 1, binnary);
            }
        }
    }

    private boolean isBlack(int i, int j, Mat binnary) {
        byte[] data = new byte[1];
        binnary.get(i, j, data);
        if (Byte.toUnsignedInt(data[0]) == 0) {
            return true;
        } else {
            return false;
        }
    }

    private String getLinePattern(Mat binary, int row, int min_y, int max_y) {
        String pattern = "";
        byte[] data = new byte[1];
        Mat scanLine = binary.row(row);
        for (int i = min_y; i <= max_y; i++) {
            scanLine.get(0, i, data);
            if (Byte.toUnsignedInt(data[0]) == 0) {
                pattern = pattern + "h";
            } else {
                pattern = pattern + "p";
            }
        }
        char[] p = pattern.toCharArray();
        char temp = ' ';
        pattern = "";
        for (int i = 0; i < p.length; i++) {
            if (temp != p[i]) {
                temp = p[i];
                pattern = pattern + p[i];
            }
        }
        return pattern;
    }

    private void addLipsPattern() {
        lipsPattern = new ArrayList<>();
        lipsPattern.add("hphph");
        lipsPattern.add("php");
    }
}
