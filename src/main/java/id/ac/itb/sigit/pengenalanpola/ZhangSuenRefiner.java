package id.ac.itb.sigit.pengenalanpola;


import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_core;

/**
 * Created by Sigit A on 10/29/2015.
 */
public class ZhangSuenRefiner {

    /*
     * kernel structure
     * p0p1p2
     * p3p4p5
     * p6p7p8
     *
     */
    private int[] n = new int[9];
    private int[] p = new int[9];

    public opencv_core.Mat refineZhangSuen(Mat input) {
        Mat refinedZhangSuenMat = input.clone();
        ByteIndexer refinedZhangSuen = refinedZhangSuenMat.createIndexer();

        for (int i = 1; i < input.rows() - 2; i++) {
            for (int j = 1; j < input.cols() - 2; j++) {
                byte[] data = new byte[1];
                refinedZhangSuen.get(i, j, data);
                n[4] = data[0];
                if (n[4] == 1) {
                    refinedZhangSuen.get(i - 1, j - 1, data);
                    n[0] = data[0];

                    refinedZhangSuen.get(i - 1, j, data);
                    n[1] = data[0];

                    refinedZhangSuen.get(i - 1, j + 1, data);
                    n[2] = data[0];

                    refinedZhangSuen.get(i, j - 1, data);
                    n[3] = data[0];

                    refinedZhangSuen.get(i, j + 1, data);
                    n[5] = data[0];

                    refinedZhangSuen.get(i + 1, j - 1, data);
                    n[6] = data[0];

                    refinedZhangSuen.get(i + 1, j, data);
                    n[7] = data[0];

                    refinedZhangSuen.get(i + 1, j + 1, data);
                    n[8] = data[0];

                    if (isKernel8()) {
                        data[0] = 0;
                        refinedZhangSuen.put(i, j, data);
                    } else if (isKernel7()) {
                        data[0] = 0;
                        refinedZhangSuen.put(i, j, data);
                    } else if (isKernel6()) {
                        data[0] = 0;
                        refinedZhangSuen.put(i, j, data);
                    } else if (isKernel5()) {
                        data[0] = 0;
                        refinedZhangSuen.put(i, j, data);
                    }
                }
            }
        }
        for (int i = 1; i < input.rows() - 2; i++) {
            for (int j = 1; j < input.cols() - 2; j++) {
                byte[] data = new byte[1];
                refinedZhangSuen.get(i, j, data);
                n[4] = data[0];
                if (n[4] == 1) {
                    refinedZhangSuen.get(i - 1, j - 1, data);
                    n[0] = data[0];

                    refinedZhangSuen.get(i - 1, j, data);
                    n[1] = data[0];

                    refinedZhangSuen.get(i - 1, j + 1, data);
                    n[2] = data[0];

                    refinedZhangSuen.get(i, j - 1, data);
                    n[3] = data[0];

                    refinedZhangSuen.get(i, j + 1, data);
                    n[5] = data[0];

                    refinedZhangSuen.get(i + 1, j - 1, data);
                    n[6] = data[0];

                    refinedZhangSuen.get(i + 1, j, data);
                    n[7] = data[0];

                    refinedZhangSuen.get(i + 1, j + 1, data);
                    n[8] = data[0];

                    if (isKernel4()) {
                        data[0] = 0;
                        refinedZhangSuen.put(i, j, data);
                    } else if (isKernel3()) {
                        data[0] = 0;
                        refinedZhangSuen.put(i, j, data);
                    } else if (isKernel2()) {
                        data[0] = 0;
                        refinedZhangSuen.put(i, j, data);
                    } else if (isKernel1()) {
                        data[0] = 0;
                        refinedZhangSuen.put(i, j, data);
                    }
                }
            }
        }
       // return refinedZhangSuen;
        return refinedZhangSuenMat;
    }


    /*
     * 010
     * 011
     * 000
     */
    private boolean isKernel1() {
        p[0] = 0;
        p[1] = 1;
        p[2] = 0;
        p[3] = 0;
        p[4] = 1;
        p[5] = 1;
        p[6] = 0;
        p[7] = 0;
        p[8] = 0;
        boolean trust = true;
        for (int i = 0; i < 9; i++) {
            if (p[i] == 1 && n[i] == 1) {
                trust = trust && true;
            } else if (p[i] == 1 && n[i] == 0) {
                return false;
            }
        }
        if (n[6]==1)return false;
        return trust;
    }

    /*
     * 000
     * 011
     * 010
     */
    private boolean isKernel2() {
        int[] p = new int[9];
        p[0] = 0;
        p[1] = 0;
        p[2] = 0;
        p[3] = 0;
        p[4] = 1;
        p[5] = 1;
        p[6] = 0;
        p[7] = 1;
        p[8] = 0;
        boolean trust = true;
        for (int i = 0; i < 9; i++) {
            if (p[i] == 1 && n[i] == 1) {
                trust = trust && true;
            } else if (p[i] == 1 && n[i] == 0) {
                return false;
            }
        }
        if (n[0]==1)return false;
        return trust;
    }

    /*
     * 000
     * 110
     * 010
     */
    private boolean isKernel3() {
        int[] p = new int[9];
        p[0] = 0;
        p[1] = 0;
        p[2] = 0;
        p[3] = 1;
        p[4] = 1;
        p[5] = 0;
        p[6] = 0;
        p[7] = 1;
        p[8] = 0;
        boolean trust = true;
        for (int i = 0; i < 9; i++) {
            if (p[i] == 1 && n[i] == 1) {
                trust = trust && true;
            } else if (p[i] == 1 && n[i] == 0) {
                return false;
            }
        }
        if(n[2]==1)return false;
        return trust;
    }

    /*
     * 010
     * 110
     * 000
     */
    private boolean isKernel4() {
        int[] p = new int[9];
        p[0] = 0;
        p[1] = 1;
        p[2] = 0;
        p[3] = 1;
        p[4] = 1;
        p[5] = 0;
        p[6] = 0;
        p[7] = 0;
        p[8] = 0;
        boolean trust = true;
        for (int i = 0; i < 9; i++) {
            if (p[i] == 1 && n[i] == 1) {
                trust = trust && true;
            } else if (p[i] == 1 && n[i] == 0) {
                return false;
            }
        }
        if (n[8]==1)return false;
        return trust;
    }

    /*
     * 010
     * 111
     * 000
     */
    private boolean isKernel5() {
        int[] p = new int[9];
        p[0] = 0;
        p[1] = 1;
        p[2] = 0;
        p[3] = 1;
        p[4] = 1;
        p[5] = 1;
        p[6] = 0;
        p[7] = 0;
        p[8] = 0;
        boolean trust = true;
        for (int i = 0; i < 9; i++) {
            if (p[i] == 1 && n[i] == 1) {
                trust = trust && true;
            } else if (p[i] == 1 && n[i] == 0) {
                return false;
            }
        }
        return trust;
    }

    /*
     * 010
     * 011
     * 010
     */
    private boolean isKernel6() {
        int[] p = new int[9];
        p[0] = 0;
        p[1] = 1;
        p[2] = 0;
        p[3] = 0;
        p[4] = 1;
        p[5] = 1;
        p[6] = 0;
        p[7] = 1;
        p[8] = 0;
        boolean trust = true;
        for (int i = 0; i < 9; i++) {
            if (p[i] == 1 && n[i] == 1) {
                trust = trust && true;
            } else if (p[i] == 1 && n[i] == 0) {
                return false;
            }
        }
        return trust;
    }

    /*
     * 000
     * 111
     * 010
     */
    private boolean isKernel7() {
        int[] p = new int[9];
        p[0] = 0;
        p[1] = 0;
        p[2] = 0;
        p[3] = 1;
        p[4] = 1;
        p[5] = 1;
        p[6] = 0;
        p[7] = 1;
        p[8] = 0;
        boolean trust = true;
        for (int i = 0; i < 9; i++) {
            if (p[i] == 1 && n[i] == 1) {
                trust = trust && true;
            } else if (p[i] == 1 && n[i] == 0) {
                return false;
            }
        }
        return trust;
    }

    /*
     * 010
     * 110
     * 010
     */
    private boolean isKernel8() {
        int[] p = new int[9];
        p[0] = 0;
        p[1] = 1;
        p[2] = 0;
        p[3] = 1;
        p[4] = 1;
        p[5] = 0;
        p[6] = 0;
        p[7] = 1;
        p[8] = 0;
        boolean trust = true;
        for (int i = 0; i < 9; i++) {
            if (p[i] == 1 && n[i] == 1) {
                trust = trust && true;
            } else if (p[i] == 1 && n[i] == 0) {
                return false;
            }
        }
        return trust;
    }

}
