package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_highgui;

import org.bytedeco.javacpp.opencv_imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

/*
 * Created by Sigit A on 11/15/2015.
 */

@Service
public class ColorMapGroupingContainer {
    private static final Logger log = LoggerFactory.getLogger(ColorMapGroupingContainer.class);
    private Mat input, output, sample, bin, grayscale;
    private ArrayList<ColorMap> colorMaps = new ArrayList<>();
    private ByteIndexer binIdx;

    public void setInput(String sources) {

        final File imageFile = new File(sources);
        this.input =opencv_highgui.imread(imageFile.getPath(), opencv_highgui.CV_LOAD_IMAGE_COLOR);
        this.bin = opencv_highgui.imread(imageFile.getPath(), opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        this.binIdx = bin.createIndexer();
        grayscale = bin.clone();
        zerosBin();
        //resizeBin();
        //resizeInput();
        log.info("Input Sudah row={} col={}", input.rows(), input.cols());
    }

    public void setInput(byte[] gambar) {
        Mat mb = new Mat(gambar);
        this.input = opencv_highgui.imdecode(mb, opencv_highgui.CV_LOAD_IMAGE_COLOR);
        this.bin = opencv_highgui.imdecode(mb, opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        this.binIdx = bin.createIndexer();
        grayscale = bin.clone();
        zerosBin();
        //resizeBin();
        //resizeInput();
        //Imgproc.GaussianBlur(input, input, new Size(5, 5), 2.2, 2);
    }

    public void setInput(Mat a) {
        this.input = a;
        resizeInput();
    }

    public void setSample(byte[] gambar) {
        Mat mb = new Mat(gambar);
        this.sample = opencv_highgui.imdecode(mb, opencv_highgui.CV_LOAD_IMAGE_COLOR);
        opencv_imgproc.GaussianBlur(sample, sample, new opencv_core.Size(5, 5), 2.2, 2, opencv_imgproc.BORDER_DEFAULT);
    }

    public byte[] getSample() {
        BytePointer result = new BytePointer();
        try {
            opencv_highgui.imencode(".png", sample, result);
            byte[] byteArr = new byte[result.limit()];
            result.get(byteArr);
            return byteArr;
        } finally {
            result.deallocate();
        }
    }

    public byte[] getOutput() {
        BytePointer result = new BytePointer();
        try {
            opencv_highgui.imencode(".png", output, result);
            byte[] byteArr = new byte[result.limit()];
            result.get(byteArr);
            return byteArr;
        } finally {
            result.deallocate();
        }
    }

    public byte[] getBin() {
        BytePointer result = new BytePointer();
        try {
            opencv_highgui.imencode(".png", bin, result);
            byte[] byteArr = new byte[result.limit()];
            result.get(byteArr);
            return byteArr;
        } finally {
            result.deallocate();
        }
    }

    public byte[] getInput() {
        BytePointer result = new BytePointer();
        try {
            opencv_highgui.imencode(".png", input, result);
            byte[] byteArr = new byte[result.limit()];
            result.get(byteArr);
            return byteArr;
        } finally {
            result.deallocate();
        }
    }

    public Mat getOutputMat() {
        return output;
    }

    public void setColorMaps(String name, int radious) {
        Histogram2 hist = new Histogram2();
        hist.setHistogram(sample);
        ColorMap clrMap = new ColorMap();
        clrMap.setRadious(radious);
        clrMap.setGrup(name);
        for (int r = 0; r < 256; r++) {
            for (int g = 0; g < 256; g++) {
                for (int b = 0; b < 256; b++) {
                    ColorPixel pix = new ColorPixel();
                    pix.r = r;
                    pix.g = g;
                    pix.b = b;
                    if (hist.getDistribution()[r * 256 * 256 + g * 256 + b] > 0) {
                        //log.info("location r,g,b {},{},{} ",pix.r,pix.g,pix.b);
                        clrMap.addMap(pix);
                    }
                }
            }
        }
        log.info("ukuran pix {}", clrMap.getMap().size());
        ColorPixel clp = new ColorPixel();
        clp.r = 255;
        clp.b = 255;
        clp.g = 255;
        clrMap.setRepresentative(clp);
        //clrMap.setRepresentative(clrMap.getMap().get(0));
        colorMaps.add(clrMap);
    }

    public void resetColorMap() {
        colorMaps = new ArrayList<>();
    }

    private ColorMap getRepresentativeColorMap(ColorPixel px) {
        ColorMap clrMap = new ColorMap();
        clrMap.setRepresentative(px);
        clrMap.setGrup("no group");
        double min = 4096;
        for (int i = 0; i < colorMaps.size(); i++) {
            ColorMap clrMapTemp = colorMaps.get(i);
            double temp = clrMapTemp.matchPixel(px);
            if (temp < min) {
                min = temp;
                clrMap = clrMapTemp;
            }
        }
        return clrMap;
    }

    public void colorGrouping() {
        output = input.clone();
        final ByteIndexer outputIdx = output.createIndexer();
        try {
            output = input.clone();
            for (int i = 0; i < input.rows(); i++) {
                for (int j = 0; j < input.cols(); j++) {
                    for (int k = 0; k < colorMaps.size(); k++) {
                        ColorPixel px = new ColorPixel();
                        byte[] tinyimg = new byte[3];
                        outputIdx.get(i, j, tinyimg);
                        px.r = Byte.toUnsignedInt(tinyimg[0]);
                        px.g = Byte.toUnsignedInt(tinyimg[1]);
                        px.b = Byte.toUnsignedInt(tinyimg[2]);
                        ColorMap temp = getRepresentativeColorMap(px);
                        //log.info("location r,g,b {},{},{} temp {}",px.r,px.g,px.b,temp.getGrup());

                        if (!temp.getGrup().equals("no group")) {
                            //log.info("location x,y {},{}",i,j);
                            tinyimg[0] = (byte) 255;
                            tinyimg[1] = (byte) 255;
                            tinyimg[2] = (byte) 255;
                            binIdx.put(i, j, tinyimg);
                        }
                    }
                }
            }
        } finally {
            outputIdx.release();
        }

        DFSIteration dfi = new DFSIteration();
        dfi.setBinary(bin);
        dfi.fillForeground();
        ArrayList<BoundingObject> bo = dfi.getBoundingObject();
        for (int i = 0; i < bo.size(); i++) {
            bo.get(i).drawBoundingBox(output, grayscale,bin, new opencv_core.Scalar(255, 0, 0, 0));
        }
    }

    public void zerosBin() {
        for (int i = 0; i < bin.rows(); i++) {
            for (int j = 0; j < bin.cols(); j++) {
                binIdx.put(i, j, (byte) 0);
            }
        }
    }

    private void resizeBin() {
        if (bin.cols() > 500 || bin.rows() > 500) {
            //Mat cpyInput = new Mat();
            opencv_core.Size e = Resizer.resizeTo500Max(bin.rows(), bin.cols());
            log.info("Matrix size = {}", e);
            opencv_imgproc.resize(bin, bin, e);
            //Input = cpyInput.clone();
        }
    }

    private void resizeInput() {
        if (input.cols() > 500 || input.rows() > 500) {
            //Mat cpyInput = new Mat();

            opencv_imgproc.GaussianBlur(sample, sample, new opencv_core.Size(5, 5), 2.2, 2, opencv_imgproc.BORDER_DEFAULT);

            opencv_core.Size e = Resizer.resizeTo500Max(input.rows(), input.cols());
            log.info("Matrix size = {}", e);
            opencv_imgproc.resize(input, input, e, 0, 0, opencv_imgproc.INTER_CUBIC);
            //Input = cpyInput.clone();
        }
    }
}
