package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by Sigit A on 10/15/2015.
 */
@Service
public class ThinningAlgorithmContainer {
    private static final Logger log = LoggerFactory.getLogger(ThinningAlgorithmContainer.class);
    private opencv_core.Mat Input, Output;
    private Histogram2 hist = new Histogram2();
    private LookupTable lookupTable = new LookupTable();
    private BinnaryTreshold bn = new BinnaryTreshold();
    private DFSAlgorithm dfs;

    public ThinningAlgorithmContainer() {
    }

    public Histogram2 getHist(){return this.hist;}

    public void setInput(String sources) {
        final File imageFile = new File(sources);
        //opencv_highgui.imread(imageFile.getPath(), opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        this.Input = opencv_highgui.imread(imageFile.getPath(), opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        log.info("Input Sudah row={} col={}",Input.rows(),Input.cols());
    }

    public void setInput(byte[] gambar){
        Mat mb = new Mat(gambar);
        this.Input = opencv_highgui.imdecode(mb, opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
    }

    public void setBinnaryTreshold(Integer treshold){
        if (0<=treshold && treshold<=255) bn.setTreshold(treshold);
    }

    public byte[] getOutput() {
        BytePointer result = new BytePointer();
        try {
            opencv_highgui.imencode(".png", Output, result);
            byte[] byteArr = new byte[result.limit()];
            result.get(byteArr);
            return byteArr;
        } finally {
            result.deallocate();
        }
    }

    public void setOutput() {
        log.info("lewat sini rows={} cols={}",Input.rows(), Input.cols());
        hist.setHistogram(Input);
        //--------------------------------
        lookupTable.setSinglelookup();
        //bn.setTreshold(70);
        bn.setTreshold(bn.getTreshold());
        lookupTable = bn.createBinaryLookup();
        Mat Outputsemi = bn.getBinaryImage(Input, lookupTable);
        if (bn.getMin()==0)Outputsemi = bn.getInvers(Outputsemi);
        ZhangSuen zs = new ZhangSuen();
        Outputsemi = zs.process(Outputsemi);
        ZhangSuenRefiner zr = new ZhangSuenRefiner();
        Outputsemi = zr.refineZhangSuen(Outputsemi);
        Output = Outputsemi.clone();
        bn.cek(Output);
        final ByteIndexer outputIdx = Output.createIndexer();
        for (int i = 0; i < Outputsemi.rows(); i++) {
            for (int j = 0; j < Outputsemi.cols(); j++) {
                byte num[] = new byte[1];
                outputIdx.get(0,j,num);
                if (num[0]==0){
                    outputIdx.put(i,j,(byte)0);
                }else{
                    outputIdx.put(i,j,(byte)255);
                }
            }
        }
        log.info("Selesai sudah row={} col={}",Output.rows(),Output.cols());
    }

    public byte[] getInput() {
        BytePointer result = new BytePointer();
        try {
            opencv_highgui.imencode(".png", Input, result);
            byte[] byteArr = new byte[result.limit()];
            result.get(byteArr);
            return byteArr;
        } finally {
            result.deallocate();
        }
    }

    public String recognizeObject(){
        setOutput();
        dfs = new DFSAlgorithm(Output);
        dfs.processImage();
        return dfs.printInfo();
    }
}
