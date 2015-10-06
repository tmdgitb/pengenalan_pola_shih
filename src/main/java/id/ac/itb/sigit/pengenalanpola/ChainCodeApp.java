package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
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
        final opencv_core.Mat mat = opencv_highgui.imread(imageFile.getPath(), opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        log.info("Image mat: rows={} cols={}", mat.rows(), mat.cols());

        flag = new boolean[mat.rows()][mat.cols()];
        int objectIdx = 0;

        final ByteIndexer idx = mat.createIndexer();
        try {
            for (int y = 0; y < mat.rows(); y++) {
                for (int x = 0; x < mat.cols(); x++) {
                    int grayScale = Byte.toUnsignedInt(idx.get(y, x));

                    if (grayScale < toleransi && searchObject && !flag[y][x]) {

                        minVer = y;
                        maxVer = y;
                        minHor = x;
                        maxHor = x;
                        String chaincode = prosesChaincode(y, x, 3, mat, idx, 0);
                        if (chaincode.length() > 20) {
                            log.info("Chaincode object #{} at ({}, {}): {}", objectIdx, x, y, chaincode);
                            objectIdx++;
                            subObject(mat, idx);
                        }
                        searchObject = false;
                    }

                    if (grayScale < toleransi && flag[y][x]) {
                        int grayScale1 = Byte.toUnsignedInt(idx.get(y, x + 1));

                        if (grayScale1 > toleransi) {
                            searchObject = true;
                        } else {
                            searchObject = false;
                        }
                    }
                }
            }
        } finally {
            idx.release();
        }
    }

    private void subObject(opencv_core.Mat mat, ByteIndexer idx) {
        for (int y = minVer; y <= maxVer; y++) {
            for (int x = minHor; x <= maxHor; x++) {
                int grayScale = Byte.toUnsignedInt(idx.get(y, x));
                int nextGrayScale = Byte.toUnsignedInt(idx.get(y, x + 1));
                if (grayScale < toleransi && flag[y][x]) {
                    if (nextGrayScale < toleransi) {
                        searchSubObject = true;
                    } else {
                        searchSubObject = false;
                    }
                }

                if (grayScale > toleransiWhite && searchSubObject && !flag[y][x]) {
                    String chaincode2 = prosesChaincode(y, x, 3, mat, idx, 1);
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

    private String prosesChaincode(int row, int col, int arah, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        if (flag[row][col]) {
            return "";
        }
        flag[row][col] = true;

        //kondisi perjalanan arah 1
        if (arah == 1) {
            //
            //cek arah 7 (samping kiri)
            //
            String arah7 = objectarah7(row, col, mat, idx, mode);
            if (arah7 != "") {
                return arah7;
            }
            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, mat, idx, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, mat, idx, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, mat, idx, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, mat, idx, mode);
            if (arah3 != "") {
                return arah3;
            }

        }
        //kondisi perjalanan arah 2
        else if (arah == 2) {
            //
            //cek arah 8 (samping kiri)
            //
            String arah8 = objectarah8(row, col, mat, idx, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, mat, idx, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, mat, idx, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, mat, idx, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, mat, idx, mode);
            if (arah4 != "") {
                return arah4;
            }

        } else if (arah == 3) {
            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, mat, idx, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, mat, idx, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, mat, idx, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, mat, idx, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, mat, idx, mode);
            if (arah5 != "") {
                return arah5;
            }
        } else if (arah == 4) {
            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, mat, idx, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, mat, idx, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, mat, idx, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, mat, idx, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, mat, idx, mode);
            if (arah6 != "") {
                return arah6;
            }
        } else if (arah == 5) {
            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, mat, idx, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, mat, idx, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, mat, idx, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, mat, idx, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, mat, idx, mode);
            if (arah7 != "") {
                return arah7;
            }
        } else if (arah == 6) {
            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, mat, idx, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, mat, idx, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, mat, idx, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, mat, idx, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, mat, idx, mode);
            if (arah8 != "") {
                return arah8;
            }
        } else if (arah == 7) {
            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, mat, idx, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, mat, idx, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, mat, idx, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, mat, idx, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, mat, idx, mode);
            if (arah1 != "") {
                return arah1;
            }
        } else //if(arah==8)
        {
            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, mat, idx, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, mat, idx, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, mat, idx, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, mat, idx, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, mat, idx, mode);
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
//        return opencv_core.Math.round((r + g + b) / 3f);
    }

    private String objectarah1(int row, int col, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col;
        int gray1 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
        if (mode == 1) {
            if (gray1 > toleransiWhite) {
                return "1" + prosesChaincode(temprow, tempcol, 1, mat, idx, mode);
            }
        } else {
            if (gray1 < toleransi) {
                areaObject(row, col);
                return "1" + prosesChaincode(temprow, tempcol, 1, mat, idx, mode);
            }
        }
        return "";
    }

    private String objectarah2(int row, int col, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col + 1;
        int gray2 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
        if (mode == 1) {
            if (gray2 > toleransiWhite) {
                return "2" + prosesChaincode(temprow, tempcol, 2, mat, idx, mode);
            }
        } else {
            if (gray2 < toleransi) {
                areaObject(row, col);
                return "2" + prosesChaincode(temprow, tempcol, 2, mat, idx, mode);
            }
        }
        return "";
    }

    private String objectarah3(int row, int col, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        int temprow, tempcol;

        temprow = row;
        tempcol = col + 1;
        int gray3 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
        if (mode == 1) {
            if (gray3 > toleransiWhite) {
                return "3" + prosesChaincode(temprow, tempcol, 3, mat, idx, mode);
            }
        } else {
            if (gray3 < toleransi) {
                areaObject(row, col);
                return "3" + prosesChaincode(temprow, tempcol, 3, mat, idx, mode);
            }
        }

        return "";
    }

    private String objectarah4(int row, int col, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        int temprow, tempcol;

        temprow = row + 1;
        tempcol = col + 1;
        int gray4 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
        if (mode == 1) {
            if (gray4 > toleransiWhite) {
                return "4" + prosesChaincode(temprow, tempcol, 4, mat, idx, mode);
            }
        } else {
            if (gray4 < toleransi) {
                areaObject(row, col);
                return "4" + prosesChaincode(temprow, tempcol, 4, mat, idx, mode);
            }
        }

        return "";
    }

    private String objectarah5(int row, int col, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        int temprow, tempcol;

        temprow = row + 1;
        tempcol = col;
        int gray5 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
        if (mode == 1) {
            if (gray5 > toleransiWhite) {
                return "5" + prosesChaincode(temprow, tempcol, 5, mat, idx, mode);
            }
        } else {
            if (gray5 < toleransi) {
                areaObject(row, col);
                return "5" + prosesChaincode(temprow, tempcol, 5, mat, idx, mode);
            }
        }

        return "";
    }

    private String objectarah6(int row, int col, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        int temprow, tempcol;

        temprow = row + 1;
        tempcol = col - 1;
        int gray6 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
        if (mode == 1) {
            if (gray6 > toleransiWhite) {
                return "6" + prosesChaincode(temprow, tempcol, 6, mat, idx, mode);
            }
        } else {
            if (gray6 < toleransi) {
                areaObject(row, col);
                return "6" + prosesChaincode(temprow, tempcol, 6, mat, idx, mode);
            }
        }

        return "";
    }

    private String objectarah7(int row, int col, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        int temprow, tempcol;

        temprow = row;
        tempcol = col - 1;
        int gray7 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
        if (mode == 1) {
            if (gray7 > toleransiWhite) {
                return "7" + prosesChaincode(temprow, tempcol, 7, mat, idx, mode);
            }
        } else {
            if (gray7 < toleransi) {
                areaObject(row, col);
                return "7" + prosesChaincode(temprow, tempcol, 7, mat, idx, mode);
            }
        }

        return "";
    }

    private String objectarah8(int row, int col, opencv_core.Mat mat, ByteIndexer idx, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col - 1;
        int gray8 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
        if (mode == 1) {
            if (gray8 > toleransiWhite) {
                return "8" + prosesChaincode(temprow, tempcol, 8, mat, idx, mode);
            }
        } else {
            if (gray8 < toleransi) {
                areaObject(row, col);
                return "8" + prosesChaincode(temprow, tempcol, 8, mat, idx, mode);
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