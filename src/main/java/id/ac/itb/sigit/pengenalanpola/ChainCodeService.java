package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 15/10/2015.
 */
@Service
public class ChainCodeService {

    private static final Logger log = LoggerFactory.getLogger(ChainCodeService.class);

    private opencv_core.Mat origMat;

    //List<CharDef> charDefs = new ArrayList<>();
    private List<Geometry> geometries = new ArrayList<>();

    // Input file
    public opencv_core.Mat loadInput(File imageFile) {
        return loadInput(imageFile, 0);
    }

    public opencv_core.Mat loadInput(File imageFile, int mode) {
        return loadInput(imageFile, mode, "");
    }

    public opencv_core.Mat loadInput(File imageFile, int mode, String msg) {
        log.info("Processing image file '{}' ...", imageFile);
        origMat = opencv_highgui.imread(imageFile.getPath(), opencv_highgui.CV_LOAD_IMAGE_UNCHANGED);
        log.info("Image mat: rows={} cols={} depth={} type={}", origMat.rows(), origMat.cols(), origMat.depth(), origMat.type());
        processChainCode(origMat, msg, mode);
        return origMat;
    }

    // Input Byte
    public opencv_core.Mat loadInput(String contentType, byte[] inputBytes) {
        return loadInput(contentType, inputBytes, 0);
    }

    public opencv_core.Mat loadInput(String contentType, byte[] inputBytes, int mode) {
        return loadInput(contentType, inputBytes, mode, "");
    }

    public opencv_core.Mat loadInput(String contentType, byte[] inputBytes, int mode, String msg) {
        opencv_core.Mat imgGray = new opencv_core.Mat();
        log.info("Processing input image {}: {} bytes ...", contentType, inputBytes.length);
        origMat = opencv_highgui.imdecode(new opencv_core.Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_UNCHANGED);
        log.info("Image mat: rows={} cols={} depth={} type={}", origMat.rows(), origMat.cols(), origMat.depth(), origMat.type());
        processChainCode(origMat, msg, mode);
        return origMat;
    }

    public opencv_core.Mat getOrigMat() {
        return origMat;
    }

    private void processChainCode(opencv_core.Mat inputMat, String msg, int mode) {
        opencv_core.Mat imgGray;
        if (inputMat.type() == opencv_core.CV_8UC1) {
            imgGray = inputMat;
        } else { // assume BGR
            imgGray = new opencv_core.Mat();
            opencv_imgproc.cvtColor(inputMat, imgGray, opencv_imgproc.COLOR_BGR2GRAY);
        }
        if (mode == 1) {
            final InverseImageConverter inverseImage = new InverseImageConverter(imgGray);
            imgGray = inverseImage.getInverseImage();
        }

        final ChainCodeWhiteConverter chainCodeWhiteConverter = new ChainCodeWhiteConverter(imgGray, "plat");
        geometries = chainCodeWhiteConverter.getChainCode();
        log.info("Geometries detected: {}", geometries.size());
    }

    public List<Geometry> getGeometries() {
        return geometries;
    }

//    private void prosesChainCode(opencv_core.Mat img, String msg) {
//        final ByteIndexer idx = img.createIndexer();
//        try {
//            ChainCodeConverter chainCodeConverter = new ChainCodeConverter(img, idx, msg);
//            charDefs.add(chainCodeConverter.getChainCode());
//        } finally {
//            idx.release();
//        }
//    }

}
