package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by Sigit on 16/10/2015.
 */
public class ZhangSuenService {

    private ZhangSuen zhangSuen;
    private static final Logger log = LoggerFactory.getLogger(ChainCodeService.class);
    private opencv_core.Mat origMat;
    private opencv_core.Mat zhangSuenMat;

    public opencv_core.Mat loadInput(File imageFile)
    {
        log.info("Processing image file '{}' ...", imageFile);
        origMat = opencv_highgui.imread(imageFile.getPath());
        zhangSuenMat=origMat.clone();
        final ByteIndexer idx = zhangSuenMat.createIndexer();
        zhangSuen=new ZhangSuen();
        zhangSuen.process(origMat,idx);
        return origMat;
    }

    public opencv_core.Mat loadInput(String contentType, byte[] inputBytes) {
        log.info("Processing input image {}: {} bytes ...", contentType, inputBytes.length);
        origMat = opencv_highgui.imdecode(new opencv_core.Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_UNCHANGED);
        zhangSuenMat=origMat.clone();
        final ByteIndexer idx = zhangSuenMat.createIndexer();
        zhangSuen=new ZhangSuen();
        zhangSuen.process(origMat,idx);
        return origMat;
    }

    public opencv_core.Mat getOrigMat()
    {
        return origMat;
    }

    public opencv_core.Mat getZhainSuenMat()
    {
        return zhangSuenMat;
    }
}
