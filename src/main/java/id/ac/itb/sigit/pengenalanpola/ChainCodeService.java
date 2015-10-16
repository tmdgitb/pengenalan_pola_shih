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

    List<CharDef> charDefs = new ArrayList<>();
    List<Geometry> data = new ArrayList<>();

    // Input file
    public opencv_core.Mat loadInput(File imageFile) {
        return loadInput(imageFile, 0);
    }

    public opencv_core.Mat loadInput(File imageFile, int mode) {
        return loadInput(imageFile, mode, "");
    }

    public opencv_core.Mat loadInput(File imageFile, int mode, String msg) {
        log.info("Processing image file '{}' ...", imageFile);
        origMat = opencv_highgui.imread(imageFile.getPath());
        log.info("Image mat: rows={} cols={} depth={} type={}", origMat.rows(), origMat.cols(), origMat.depth(), origMat.type());
        setChainCode(origMat, msg, mode);
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
        origMat = opencv_highgui.imdecode(new opencv_core.Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_COLOR);
        log.info("Image mat: rows={} cols={}", origMat.rows(), origMat.cols());
        setChainCode(origMat, msg, mode);
        return origMat;
    }

    public opencv_core.Mat getOrigMat() {
        if (origMat == null) {
            return new opencv_core.Mat();
        }
        return origMat;
    }

    private void setChainCode(opencv_core.Mat imageFile, String msg, int mode) {
        opencv_core.Mat imgGray = new opencv_core.Mat();
        opencv_imgproc.cvtColor(imageFile, imgGray, opencv_imgproc.COLOR_BGR2GRAY);
        if (mode == 1) {
            InverseImageConverter inverseImage = new InverseImageConverter(imgGray);
            imgGray = inverseImage.getInveseImage();
        }

        final ChainCodeWhiteConverter chainCodeWhiteConverter = new ChainCodeWhiteConverter(imgGray, "plat");
        data = chainCodeWhiteConverter.getChainCode();
        log.info("size chaincode {}", data.size());
    }

    public List<Geometry> getChainCode() {
        return data;
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
