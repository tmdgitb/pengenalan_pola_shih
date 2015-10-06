package id.ac.itb.sigit.pengenalanpola;

import com.googlecode.javaewah.EWAHCompressedBitmap;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by ceefour on 10/6/15.
 */
@Service
public class ZhangSuenThinning {
    private static final Logger log = LoggerFactory.getLogger(ZhangSuenThinning.class);

    protected int getConnectivity(opencv_core.Mat in, ByteIndexer idx, int row, int col) {
        int[] pixels = new int[9]; // pixels[9] == pixels[1]
        pixels[0] = Byte.toUnsignedInt(idx.get(row, col)); // center
        if (col + 1 < in.cols()) {
            pixels[1] = Byte.toUnsignedInt(idx.get(row, col + 1)); // right
            if (row - 1 >= 0) {
                pixels[2] = Byte.toUnsignedInt(idx.get(row - 1, col + 1)); // top right
            }
        }
        if (row - 1 < in.rows()) {
            pixels[3] = Byte.toUnsignedInt(idx.get(row - 1, col)); // top center
            if (col - 1 >= 0) {
                pixels[4] = Byte.toUnsignedInt(idx.get(row - 1, col - 1)); // top left
            }
        }
        if (col - 1 >= 0) {
            pixels[5] = Byte.toUnsignedInt(idx.get(row, col - 1)); // left
            if (row + 1 < in.rows()) {
                pixels[6] = Byte.toUnsignedInt(idx.get(row + 1, col - 1)); // bottom left
            }
        }
        if (row + 1 < in.rows()) {
            pixels[7] = Byte.toUnsignedInt(idx.get(row + 1, col)); // bottom
            if (col + 1 < in.cols()) {
                pixels[8] = Byte.toUnsignedInt(idx.get(row + 1, col + 1)); // bottom right
            }
        }
        // now calculate
        int cn = (pixels[1] - (pixels[1] & pixels[2] & pixels[3])) +
                (pixels[3] - (pixels[3] & pixels[4] & pixels[5])) +
                (pixels[5] - (pixels[5] & pixels[6] & pixels[7])) +
                (pixels[7] - (pixels[7] & pixels[8] & pixels[1]));
        return cn / 255;
    }

    /**
     * Single iteration.
     * @param in Must be black-white! Use {@link opencv_imgproc#threshold(opencv_core.Mat, opencv_core.Mat, double, double, int)}.
     * @param mask
     * @return
     */
    public opencv_core.Mat thin(opencv_core.Mat in, ByteIndexer idx, EWAHCompressedBitmap mask) {
//        Imgproc.threshold()
//        in.depth()
        for (int y = 0; y < in.rows(); y++) {
            for (int x = 0; x < in.cols(); x++) {
                final int bitIndex = y * in.cols() + x;
                if (!mask.get(bitIndex)) {
                    continue;
                }

                final int pixel = Byte.toUnsignedInt(idx.get(y, x));
            }
        }
        return null;
    }

}