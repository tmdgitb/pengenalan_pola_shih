package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static org.bytedeco.javacpp.opencv_core.Mat;

@Service
public class ZhangSuen {
    private Logger log = LoggerFactory.getLogger(ZhangSuen.class);

    private byte[] p = new byte[9];
    private byte foreground = (byte) 255;
    private byte background = 0;
    private static int THRESHOLD = 128;
//    private byte imageout[][];
    private boolean mark[][];

    /**
     * @param image Input must be grayscale, where foreground is > {@link #THRESHOLD}.
     * @param idx
     * @return
     */
    public Mat process(final Mat srcImage) {
        // Threshold first. so output is binary grayscale. 0=background, 255=foreground.
        final Mat image = new Mat();
        opencv_imgproc.threshold(srcImage, image, THRESHOLD, 255, opencv_imgproc.CV_THRESH_BINARY);
        final ByteIndexer idx = image.createIndexer();

        try {
            int row = image.rows();
            int col = image.cols();
            int breaker = 0;
            boolean finish = false;
//        imageout = new byte[row][col];
            mark = new boolean[row][col];
            while (!finish) {
                int count = 0;
                if (breaker == -1) finish = true;
                //STEP 1 - Mark semua FG yang memenuhi kondisi 1 sampai 4
                for (int y = 0; y < row; y++) {
                    for (int x = 0; x < col; x++) {
                        p[0] = idx.get(y, x);

                        if (p[0] == foreground) {
                            if (y == 0 || y == row - 1 || x == 0 || x == col - 1) {
                            } else {
                                p[1] = idx.get(y - 1, x);
                                p[2] = idx.get(y - 1, x + 1);
                                p[3] = idx.get(y, x + 1);
                                p[4] = idx.get(y + 1, x + 1);
                                p[5] = idx.get(y + 1, x);
                                p[6] = idx.get(y + 1, x - 1);
                                p[7] = idx.get(y, x - 1);
                                p[8] = idx.get(y - 1, x - 1);

                                if (firstCondition() && secondCondition() && thirdCondition() && fourthCondition()) {
                                    mark[y][x] = true;
                                }
                            }
                        }
                    }
                }

                // STEP 2 - Ubah semua pixel yang sudah ditandai menjadi background
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (mark[i][j]) {
                            idx.put(i, j, background);
                        }
                        mark[i][j] = false;
                    }
                }

                // STEP 3 - Mark semua FG yang memenuhi kondisi 5 & 8
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        p[0] = idx.get(i, j);

                        if (p[0] == foreground) {
                            if (i == 0 || i == row - 1 || j == 0 || j == col - 1) {
                            } else {
                                p[1] = idx.get(i - 1, j);
                                p[2] = idx.get(i - 1, j + 1);
                                p[3] = idx.get(i, j + 1);
                                p[4] = idx.get(i + 1, j + 1);
                                p[5] = idx.get(i + 1, j);
                                p[6] = idx.get(i + 1, j - 1);
                                p[7] = idx.get(i, j - 1);
                                p[8] = idx.get(i - 1, j - 1);

                                if (fifthCondition() && sixthCondition() && seventhCondition() && eighthCondition()) {
                                    mark[i][j] = true;
                                }
                            }
                        }
                    }
                }

                // STEP 4 - Ubah pixel yang mempunyai mark menjadi background
                for (int y = 0; y < row; y++) {
                    for (int x = 0; x < col; x++) {
                        if (mark[y][x]) {
                            idx.put(y, x, background);
                            count++;
                        }
                        mark[y][x] = false;
                    }
                }
                if (count == 0) {
                    log.info("COUNT = " + count);
                    breaker = -1;
                } else {
                    //System.out.print("COUNT = "+count);
                }
            }
            return image;
        } finally {
            idx.release();
        }
    }


    private int displacementBgToFg() {
        int count = 0;
        for (int i = 1; i < p.length; i++) {
            if (i == 8) {
                if (p[8] == background && p[1] == foreground) count++;
            } else {
                if (p[i] == background && p[i + 1] == foreground) count++;
            }
        }
        return count;
    }

    private int neighbouringForeground() {
        int neighbour = 0;
        for (int i = 1; i < p.length; i++) {
            if (p[i] == foreground) neighbour++;
        }
        return neighbour;
    }

    private boolean firstCondition() { // 2<=N(P1)<=6
        if (2 <= neighbouringForeground() && neighbouringForeground() <= 6)
            return true;
        else return false;
    }

    private boolean secondCondition() { // S(P1) = 1
        return displacementBgToFg() == 1;
    }

    private boolean thirdCondition() { // P2*P4*P6=0
        return p[1] * p[3] * p[5] == 0;
    }

    private boolean fourthCondition() { //P4*P6*P8=0
        return p[3] * p[5] * p[7] == 0;
    }

    private boolean fifthCondition() { // 2<=N(P1)<=6
        return firstCondition();
    }

    private boolean sixthCondition() { //S(P1)=1
        return secondCondition();
    }

    private boolean seventhCondition() { //P2*P4*P8=0
        return p[1] * p[3] * p[7] == 0;
    }

    private boolean eighthCondition() { //P2*P6*P8=0
        return p[1] * p[5] * p[7] == 0;
    }
}
