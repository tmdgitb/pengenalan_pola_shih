package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Sigit on 22/09/2015.
 */
public class ChainCodeConverter {
    private static final Logger log = LoggerFactory.getLogger(HistogramApp.class);
    private final ByteIndexer idx;
    private boolean flag[][];
    private int toleransi = 10, toleransiWhite = 230;
    private boolean searchObject = true, searchSubObject = false;
    private int minHor = 0, maxHor = 0, minVer = 0, maxVer = 0;
    private final opencv_core.Mat imgMat;
    private final CharDef charDef;

    /**
     * @param data MUST BE GRAYSCALE
     * @param idx
     */
    public ChainCodeConverter(opencv_core.Mat data, ByteIndexer idx) {
        this(data, idx, "");
    }

    /**
     * @param data MUST BE GRAYSCALE
     * @param idx
     * @param msg
     */
    public ChainCodeConverter(opencv_core.Mat data, ByteIndexer idx, String msg) {
        this.imgMat = data;
        this.idx = idx;
        this.charDef = new CharDef();
        this.charDef.setCharacter(msg);
    }

    public CharDef getChainCode() {
        flag = new boolean[imgMat.rows()][imgMat.cols()];
        int objectIdx = 0;

        for (int y = 0; y < imgMat.rows(); y++) {
            for (int x = 0; x < imgMat.cols(); x++) {
                final int grayScale = Byte.toUnsignedInt(idx.get(y, x));

                if (grayScale < toleransi && searchObject && !flag[y][x]) {

                    minVer = y;
                    maxVer = y;
                    minHor = x;
                    maxHor = x;
                    String chaincode = prosesChaincode(y, x, 3, imgMat, 0);
                    charDef.setChainCode(chaincode);

                    if (chaincode.length() > 20) {
                        log.info("Chaincode object #{} at ({}, {}): {}", objectIdx, x, y, chaincode);
                        objectIdx++;
                        subObject(imgMat);
                    }
                    searchObject = false;
                }

                if (grayScale < toleransi && flag[y][x]) {
                    final int grayScale1 = Byte.toUnsignedInt(idx.get(y, x + 1));

                    if (grayScale1 > toleransi) {
                        searchObject = true;
                    } else {
                        searchObject = false;
                    }
                }
            }
        }

        return charDef;
    }

    private void subObject(opencv_core.Mat imgMat) {
        for (int y = minVer; y <= maxVer; y++) {
            for (int x = minHor; x <= maxHor; x++) {
                final int grayScale = Byte.toUnsignedInt(idx.get(y, x));
                final int nextGrayScale = Byte.toUnsignedInt(idx.get(y, x + 1));
                if (grayScale < toleransi && flag[y][x]) {
                    if (nextGrayScale < toleransi) {
                        searchSubObject = true;
                    } else {
                        searchSubObject = false;
                    }
                }

                if (grayScale > toleransiWhite && searchSubObject && !flag[y][x]) {
                    String chaincode2 = prosesChaincode(y, x, 3, imgMat, 1);
                    charDef.getSubChainCode().add(chaincode2);
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

    private String prosesChaincode(int row, int col, int arah, opencv_core.Mat imgMat, int mode) {
        if (flag[row][col]) {
            return "";
        }
        flag[row][col] = true;

        //kondisi perjalanan arah 1
        if (arah == 1) {
            //
            //cek arah 7 (samping kiri)
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }
            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

        }
        //kondisi perjalanan arah 2
        else if (arah == 2) {
            //
            //cek arah 8 (samping kiri)
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

        } else if (arah == 3) {
            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }
        } else if (arah == 4) {
            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }


            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }
        } else if (arah == 5) {
            //
            //cek arah 3
            //
            String arah3 = objectarah3(row, col, imgMat, mode);
            if (arah3 != "") {
                return arah3;
            }

            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }
        } else if (arah == 6) {
            //
            //cek arah 4
            //
            String arah4 = objectarah4(row, col, imgMat, mode);
            if (arah4 != "") {
                return arah4;
            }

            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }
        } else if (arah == 7) {
            //
            //cek arah 5
            //
            String arah5 = objectarah5(row, col, imgMat, mode);
            if (arah5 != "") {
                return arah5;
            }

            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }
        } else //if(arah==8)
        {
            //
            //cek arah 6
            //
            String arah6 = objectarah6(row, col, imgMat, mode);
            if (arah6 != "") {
                return arah6;
            }

            //
            //cek arah 7
            //
            String arah7 = objectarah7(row, col, imgMat, mode);
            if (arah7 != "") {
                return arah7;
            }

            //
            //cek arah 8
            //
            String arah8 = objectarah8(row, col, imgMat, mode);
            if (arah8 != "") {
                return arah8;
            }

            //
            //cek arah 1 (depan)
            //
            String arah1 = objectarah1(row, col, imgMat, mode);
            if (arah1 != "") {
                return arah1;
            }

            //
            //cek arah 2
            //
            String arah2 = objectarah2(row, col, imgMat, mode);
            if (arah2 != "") {
                return arah2;
            }
        }
        return "";
    }


    private String objectarah1(int row, int col, opencv_core.Mat imgMat, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col;
        final int gray1 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
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

    private String objectarah2(int row, int col, opencv_core.Mat imgMat, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col + 1;
        final int gray2 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
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

    private String objectarah3(int row, int col, opencv_core.Mat imgMat, int mode) {
        int temprow, tempcol;
        temprow = row;
        tempcol = col + 1;
        final int gray3 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
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

    private String objectarah4(int row, int col, opencv_core.Mat imgMat, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col + 1;
        final int gray4 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
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

    private String objectarah5(int row, int col, opencv_core.Mat imgMat, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col;
        final int gray5 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
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

    private String objectarah6(int row, int col, opencv_core.Mat imgMat, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col - 1;
        final int gray6 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
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

    private String objectarah7(int row, int col, opencv_core.Mat imgMat, int mode) {
        int temprow, tempcol;
        temprow = row;
        tempcol = col - 1;
        final int gray7 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
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

    private String objectarah8(int row, int col, opencv_core.Mat imgMat, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col - 1;
        final int gray8 = Byte.toUnsignedInt(idx.get(temprow, tempcol));
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
