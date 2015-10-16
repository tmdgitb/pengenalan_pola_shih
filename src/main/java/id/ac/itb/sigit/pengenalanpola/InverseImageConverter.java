package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;

/**
 * Created by Sigit on 15/10/2015.
 */
public class InverseImageConverter {
    private opencv_core.Mat inverseImage;

    InverseImageConverter(opencv_core.Mat matImage)
    {
        inverseImage=matImage.clone();
        final ByteIndexer imgIdxInt = matImage.createIndexer();
        final ByteIndexer imgIdxOut = inverseImage.createIndexer();
        try {
            for (int i = 0; i < matImage.rows(); i++) {
                for (int j = 0; j < matImage.cols(); j++) {
                    int grayScale = Byte.toUnsignedInt(imgIdxInt.get(i, j));
                    grayScale=255-grayScale;
                    imgIdxOut.put(i,j,(byte) grayScale);
                }
            }
        } finally {
            imgIdxOut.release();
            imgIdxInt.release();
        }
    }

    public opencv_core.Mat getInverseImage()
    {
        return inverseImage;
    }
}
