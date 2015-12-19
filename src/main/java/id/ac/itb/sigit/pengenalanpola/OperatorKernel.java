package id.ac.itb.sigit.pengenalanpola;

/**
 * Created by Sigit A on 10/29/2015.
 * 012
 * 345
 * 678
 */

public class OperatorKernel {
    /* 00 01 02
     * 10 11 12
     * 20 21 22
     */
    private int[][] operatorX;
    private int[][] operatorY;
    private int[][] gaussianOperator;

    private int kelas;
    private int a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;

    public OperatorKernel() {
        setOperatorSobel();
    }

    public void setOperatorGaussian() {
        gaussianOperator = new int[5][5];
        a = 8;
        b = 5;
        c = 3;
        d = 4;
        e = 2;
        f = 1;
        gaussianOperator[0][0] = f;
        gaussianOperator[0][1] = e;
        gaussianOperator[0][2] = c;
        gaussianOperator[0][3] = e;
        gaussianOperator[0][4] = f;
        gaussianOperator[1][0] = e;
        gaussianOperator[1][1] = d;
        gaussianOperator[1][2] = b;
        gaussianOperator[1][3] = d;
        gaussianOperator[1][4] = e;
        gaussianOperator[2][0] = c;
        gaussianOperator[2][1] = b;
        gaussianOperator[2][2] = a;
        gaussianOperator[2][3] = b;
        gaussianOperator[2][4] = c;
        gaussianOperator[3][0] = e;
        gaussianOperator[3][1] = d;
        gaussianOperator[3][2] = b;
        gaussianOperator[3][3] = d;
        gaussianOperator[3][4] = e;
        gaussianOperator[4][0] = f;
        gaussianOperator[4][1] = e;
        gaussianOperator[4][2] = c;
        gaussianOperator[4][3] = e;
        gaussianOperator[4][4] = f;
    }

    public void setOperatorKirsch() {

    }

    public void setOperatorSobel() {
        operatorX = new int[3][3];
        operatorY = new int[3][3];
        a = 1;
        b = 2;
        c = 1;
        operatorX[0][0] = -a;
        operatorX[0][2] = a;
        operatorX[1][0] = -b;
        operatorX[1][2] = b;
        operatorX[2][0] = -c;
        operatorX[2][2] = c;
        operatorY[0][0] = -a;
        operatorY[0][1] = -b;
        operatorY[0][2] = -c;
        operatorY[2][0] = a;
        operatorY[2][1] = b;
        operatorY[2][2] = c;
        kelas = 1;
    }

    public void setOperatorPrewit() {
        operatorX = new int[3][3];
        operatorY = new int[3][3];
        a = 1;
        b = 1;
        c = 1;
        operatorX[0][0] = -a;
        operatorX[0][2] = a;
        operatorX[1][0] = -b;
        operatorX[1][2] = b;
        operatorX[2][0] = -c;
        operatorX[2][2] = c;
        operatorY[0][0] = -a;
        operatorY[0][1] = -b;
        operatorY[0][2] = -c;
        operatorY[2][0] = a;
        operatorY[2][1] = b;
        operatorY[2][2] = c;
        kelas = 1;
    }

    public void setCustomOperator(int ai, int bi, int ci) {
        operatorX = new int[3][3];
        operatorY = new int[3][3];
        a = ai;
        b = bi;
        c = ci;

        operatorX[0][0] = -a;
        operatorX[0][2] = a;
        operatorX[1][0] = -b;
        operatorX[1][2] = b;
        operatorX[2][0] = -c;
        operatorX[2][2] = c;
        operatorY[0][0] = -a;
        operatorY[0][1] = -b;
        operatorY[0][2] = -c;
        operatorY[2][6] = a;
        operatorY[2][7] = b;
        operatorY[2][8] = c;
        kelas = 1;
    }

    public int[][] getOperatorX() {
        return this.operatorX;
    }

    /*
     * p0p1p2 op0op1op2
     * p3p4p5 op3op4op5
     * p6p7p8 op6op7op8
     */
    public int getGradienXY(int[] npixel) {
        int a = (int) Math.sqrt(npixel.length);
        int[][] p = new int[a][a];
        int itr = 0;
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < a; j++) {
                p[i][j] = npixel[itr];
                itr++;
            }
        }
        return Konvolutor.konvolusi(getOperatorX(), p) + Konvolutor.konvolusi(getOperatorY(), p);
    }

    public int getGradienGauss(int[] npixel) {
        int a = (int) Math.sqrt(npixel.length);
        int[][] p = new int[a][a];
        int itr = 0;
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < a; j++) {
                p[i][j] = npixel[itr];
                itr++;
            }
        }
        return Konvolutor.konvolusi(gaussianOperator, p);
    }

    public int[][] getOperatorY() {
        return this.operatorY;
    }

    public int normalizeXorY(int value) {
        int max = (a * 255) + (b * 255) + (c * 255);
        double result = ((double) value / (double) max) * 255;
        return (int) result;
    }

    public int normalizeGrad(int value) {
        int max = 2 * ((a * 255) + (b * 255) + (c * 255));
        double result = ((double) value / (double) max) * 255;
        return (int) result;
    }

    public byte normalizeGaussian(int value) {
        int sum = 0;
        for (int i = 0; i < gaussianOperator.length; i++) {
            for (int j = 0; j < gaussianOperator[i].length; j++) {
                sum = sum + Math.abs(gaussianOperator[i][j]);
            }
        }
        double result = value / sum;
        return (byte) result;
    }
}
