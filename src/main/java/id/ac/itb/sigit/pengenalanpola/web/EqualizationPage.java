package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.Histogram;
import id.ac.itb.sigit.pengenalanpola.HistogramEq;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import java.io.File;

import static org.bytedeco.javacpp.opencv_core.Mat;

/**
 * Created by ilham on 16/10/2015.
 */
@MountPath("equalization")
public class EqualizationPage  extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(EqualizationPage.class);
    private HistogramEq histogramEq;
    private Histogram histogram1 = new Histogram(), histogram2 = new Histogram();
    private byte[] origBytes;

    public EqualizationPage(PageParameters parameters) {
        super(parameters);
        File inputImg = new File("Unequalized.jpg");
        histogram1.loadInput(inputImg);

        histogramEq = new HistogramEq();
        Mat eqMat = histogramEq.loadInput(inputImg);

        histogram2 = new Histogram();
        histogram2.loadInput(eqMat);

        final WebMarkupContainer resultDiv = new WebMarkupContainer("resultDiv");
        resultDiv.setOutputMarkupId(true);
//        resultDiv.add(new Label("uniqueColorCount", histogram.getUniqueColorCount()));
        resultDiv.add(new MultiHistogramPanel("histogram", new AbstractReadOnlyModel<Histogram>() {
            @Override
            public Histogram getObject() {
                return histogram2;
            }
        }));

        final DynamicImageResource eqImgRes = new DynamicImageResource("png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return histogramEq.getEqualizedPng() != null ? histogramEq.getEqualizedPng() : new byte[0];
            }
        };
        final Image eqImg = new Image("eqImg", eqImgRes);
        eqImg.setOutputMarkupId(true);
        resultDiv.add(eqImg);

        add(resultDiv);
//        add(new HistogramPanel("grayscale", new Model<>(histogram.getGrayscale())));
        /*add(new HistogramPanel("red", new Model<>(histogram.getRed())));
        add(new HistogramPanel("green", new Model<>(histogram.getGreen())));
        add(new HistogramPanel("blue", new Model<>(histogram.getBlue())));*/

//        final DropZoneFileUpload fileFld = new DropZoneFileUpload("fileFld") {
//            @Override
//            protected void onUpload(AjaxRequestTarget ajaxRequestTarget, Map<String, List<FileItem>> map) {
//                final FileItem first = map.values().iterator().next().get(0);
//                HistogramPage.this.histogram.loadInput(first.getContentType(), first.get());
//                HistogramPage.this.histogram.run();
//                ajaxRequestTarget.add(origImg, resultDiv);
//                info("Loaded file " + first.getName() + " (" + first.getContentType() + ")");
//            }
//        };
        final Form<Void> form = new Form<>("form");
        final DynamicImageResource origImgRes = new DynamicImageResource("png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                if (origBytes != null) {
                    final Mat origBytesMat = new Mat(origBytes);
                    final Mat origMat = opencv_highgui.imdecode(origBytesMat, opencv_highgui.CV_LOAD_IMAGE_UNCHANGED);
                    final BytePointer bufPtr = new BytePointer();
                    opencv_highgui.imencode(".png", origMat, bufPtr);
                    log.info("PNG Image: {} bytes", bufPtr.capacity());
                    final byte[] buf = new byte[bufPtr.capacity()];
                    bufPtr.get(buf);
                    return buf;
                } else {
                    return new byte[0];
                }
            }
        };
        final Image origImg = new Image("origImg", origImgRes);
        origImg.setOutputMarkupId(true);
        form.add(origImg);

        form.add(new MultiHistogramPanel("origHistogram", new AbstractReadOnlyModel<Histogram>() {
            @Override
            public Histogram getObject() {
                return histogram1;
            }
        }));

        final ListModel<FileUpload> filesModel = new ListModel<>();
        final FileUploadField fileFld = new FileUploadField("fileFld", filesModel);
        form.add(fileFld);
        form.add(new LaddaAjaxButton("loadBtn", new Model<>("Load"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload first = filesModel.getObject().get(0);
                origBytes = first.getBytes();
                histogram1.loadInput(first.getContentType(), origBytes);
                info("Loaded file " + first.getClientFileName() + " (" + first.getContentType() + ")");

                Mat eqMat = histogramEq.loadInput(first.getContentType(), origBytes);
                histogram2.loadInput(eqMat);

                target.add(origImg, resultDiv, notificationPanel);
            }
        });
        add(form);
    }

    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Equalization | Pengenalan Pola SHIH");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Pengenalan Pola SHIH: Meraba citra, mencari arti.");
    }

}