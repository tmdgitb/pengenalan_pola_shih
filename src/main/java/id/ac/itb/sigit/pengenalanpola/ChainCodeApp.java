package id.ac.itb.sigit.pengenalanpola;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;

/**
 * Created by Sigit on 18/09/2015.
 */
@SpringBootApplication
@Profile("chaincodeapp")
public class ChainCodeApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HistogramApp.class);

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public enum Direction {

    }

    /**
     * variable
     */

    private boolean flag[][];
    private int toleransi = 10, toleransiWhite = 100;
    private boolean searchObject = true, searchSubObject = false;

    private int minHor = 0, maxHor = 0, minVer = 0, maxVer = 0;

    /**
     * ////////////////////////
     */

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChainCodeApp.class).profiles("chaincodeapp")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        final File imageFile = new File("angka.jpg");//AA_1.jpg
        log.info("Processing image file '{}' ...", imageFile);
        final Mat imgMat = Highgui.imread(imageFile.getPath(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        log.info("Image mat: rows={} cols={}", imgMat.rows(), imgMat.cols());

        byte[] imagByte = new byte[1];
        flag = new boolean[imgMat.rows()][imgMat.cols()];
        int objectIdx = 0;

        for (int y = 0; y < imgMat.rows(); y++) {
            Mat scanline = imgMat.row(y);
            for (int x = 0; x < imgMat.cols(); x++) {
                scanline.get(0, x, imagByte);
                int grayScale = grayScale(imagByte);

                if (grayScale < toleransi && searchObject && !flag[y][x]) {

                    minVer = y;
                    maxVer = y;
                    minHor = x;
                    maxHor = x;
                    String chaincode = prosesChaincode(y, x, 3, imgMat, 0);
                    if (chaincode.length() > 20) {
                        log.info("Chaincode object #{} at ({}, {}): {}", objectIdx, x, y, chaincode);
                        objectIdx++;
                        subObject(imgMat);
                    }
                    searchObject = false;
                }

                if (grayScale < toleransi && flag[y][x]) {
                    scanline.get(0, x + 1, imagByte);
                    int grayScale1 = grayScale(imagByte);

                    if (grayScale1 > toleransi) {
                        searchObject = true;
                    } else {
                        searchObject = false;
                    }
                }
            }
        }
    }

    private void subObject(Mat imgMat) {
        byte[] imagByte = new byte[1];

        for (int y = minVer; y <= maxVer; y++) {
            Mat scanline = imgMat.row(y);
            for (int x = minHor; x <= maxHor; x++) {
                scanline.get(0, x, imagByte);
                int grayScale = grayScale(imagByte);

                scanline.get(0, x + 1, imagByte);
                int nextGrayScale = grayScale(imagByte);
                if (grayScale < toleransi && flag[y][x]) {
                    if (nextGrayScale < toleransi) {
                        searchSubObject = true;
                    } else {
                        searchSubObject = false;
                    }
                }

                if (grayScale > toleransiWhite && searchSubObject && !flag[y][x]) {
                    scanline.get(0, x + 1, imagByte);
                    String chaincode2 = prosesChaincode(y, x, 3, imgMat, 1);
                    log.info("Chaincode subobject : {}", chaincode2);
                    searchSubObject = false;
                }

                if (grayScale > toleransiWhite && flag[y][x]) {
                    if (nextGrayScale < toleransiWhite) {
                        searchSubObject = true;
                    } else {
                        searchSubObject = false;
                    }
                }
            }
        }
    }

    private String prosesChaincode(int row, int col, int arah, Mat imgMat, int mode) {
        if (flag[row][col]) {
            return "";
        }
        flag[row][col] = true;

        //kondisi perjalanan arah 1
        if (arah == 1) {
            //
            //cek arah 7 (samping kiri)
            //
            String arah7 = Objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }
            //
            //cek arah 8
            //
            String arah8 = Objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = Objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = Objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = Objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

        }
        //kondisi perjalanan arah 2
        else if (arah == 2) {
            //
            //cek arah 8 (samping kiri)
            //
            String arah8 = Objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = Objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = Objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = Objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = Objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

        } else if (arah == 3) {
            //
            //cek arah 1 (depan)
            //
            String arah1 = Objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = Objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = Objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = Objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = Objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }
        } else if (arah == 4) {
            //
            //cek arah 2
            //
            String arah2 = Objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = Objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = Objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = Objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = Objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }
        } else if (arah == 5) {
            //
            //cek arah 3
            //
            String arah3 = Objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = Objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = Objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = Objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = Objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }
        } else if (arah == 6) {
            //
            //cek arah 4
            //
            String arah4 = Objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = Objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = Objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = Objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = Objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }
        } else if (arah == 7) {
            //
            //cek arah 5
            //
            String arah5 = Objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = Objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = Objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = Objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = Objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }
        } else //if(arah==8)
        {
            //
            //cek arah 6
            //
            String arah6 = Objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = Objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = Objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = Objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = Objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }
        }
        return "";
    }

    private int grayScale(byte[] imagByte) {
        return Byte.toUnsignedInt(imagByte[0]);
//        int b = Byte.toUnsignedInt(imagByte[0]);
//        int g = Byte.toUnsignedInt(imagByte[1]);
//        int r = Byte.toUnsignedInt(imagByte[2]);
//        return Math.round((r + g + b) / 3f);
    }

    private String Objectarah1(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row - 1;
        tempcol = col;
        imgMat.get(temprow, tempcol, imagByte);
        int gray1 = grayScale(imagByte);
        if (mode == 1) {
            if (gray1 > toleransiWhite) {
                return "1" + prosesChaincode(temprow, tempcol, 1, imgMat, mode);
            }
        } else {
            if (gray1 < toleransi) {
                areaObject(row, col);
                return "1" + prosesChaincode(temprow, tempcol, 1, imgMat, mode);
            }
        }
        return "";
    }

    private String Objectarah2(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row - 1;
        tempcol = col + 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray2 = grayScale(imagByte);
        if (mode == 1) {
            if (gray2 > toleransiWhite) {
                return "2" + prosesChaincode(temprow, tempcol, 2, imgMat, mode);
            }
        } else {
            if (gray2 < toleransi) {
                areaObject(row, col);
                return "2" + prosesChaincode(temprow, tempcol, 2, imgMat, mode);
            }
        }
        return "";
    }

    private String Objectarah3(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row;
        tempcol = col + 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray3 = grayScale(imagByte);
        if (mode == 1) {
            if (gray3 > toleransiWhite) {
                return "3" + prosesChaincode(temprow, tempcol, 3, imgMat, mode);
            }
        } else {
            if (gray3 < toleransi) {
                areaObject(row, col);
                return "3" + prosesChaincode(temprow, tempcol, 3, imgMat, mode);
            }
        }

        return "";
    }

    private String Objectarah4(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row + 1;
        tempcol = col + 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray4 = grayScale(imagByte);
        if (mode == 1) {
            if (gray4 > toleransiWhite) {
                return "4" + prosesChaincode(temprow, tempcol, 4, imgMat, mode);
            }
        } else {
            if (gray4 < toleransi) {
                areaObject(row, col);
                return "4" + prosesChaincode(temprow, tempcol, 4, imgMat, mode);
            }
        }

        return "";
    }

    private String Objectarah5(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row + 1;
        tempcol = col;
        imgMat.get(temprow, tempcol, imagByte);
        int gray5 = grayScale(imagByte);
        if (mode == 1) {
            if (gray5 > toleransiWhite) {
                return "5" + prosesChaincode(temprow, tempcol, 5, imgMat, mode);
            }
        } else {
            if (gray5 < toleransi) {
                areaObject(row, col);
                return "5" + prosesChaincode(temprow, tempcol, 5, imgMat, mode);
            }
        }

        return "";
    }

    private String Objectarah6(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row + 1;
        tempcol = col - 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray6 = grayScale(imagByte);
        if (mode == 1) {
            if (gray6 > toleransiWhite) {
                return "6" + prosesChaincode(temprow, tempcol, 6, imgMat, mode);
            }
        } else {
            if (gray6 < toleransi) {
                areaObject(row, col);
                return "6" + prosesChaincode(temprow, tempcol, 6, imgMat, mode);
            }
        }

        return "";
    }

    private String Objectarah7(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row;
        tempcol = col - 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray7 = grayScale(imagByte);
        if (mode == 1) {
            if (gray7 > toleransiWhite) {
                return "7" + prosesChaincode(temprow, tempcol, 7, imgMat, mode);
            }
        } else {
            if (gray7 < toleransi) {
                areaObject(row, col);
                return "7" + prosesChaincode(temprow, tempcol, 7, imgMat, mode);
            }
        }

        return "";
    }

    private String Objectarah8(int row, int col, Mat imgMat, int mode) {
        int temprow, tempcol;
        byte[] imagByte = new byte[1];

        temprow = row - 1;
        tempcol = col - 1;
        imgMat.get(temprow, tempcol, imagByte);
        int gray8 = grayScale(imagByte);
        if (mode == 1) {
            if (gray8 > toleransiWhite) {
                return "8" + prosesChaincode(temprow, tempcol, 8, imgMat, mode);
            }
        } else {
            if (gray8 < toleransi) {
                areaObject(row, col);
                return "8" + prosesChaincode(temprow, tempcol, 8, imgMat, mode);
            }
        }

        return "";
    }

    private void areaObject(int row, int col) {
        if (minHor > col) {
            minHor = col;
        } else if (maxHor < col) {
            maxHor = col;
        }

        if (minVer > row) {
            minVer = row;
        } else if (maxVer < row) {
            maxVer = row;
        }
    }
}