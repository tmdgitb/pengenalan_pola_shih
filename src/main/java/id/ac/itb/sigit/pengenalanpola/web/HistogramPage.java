package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.Histogram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.io.File;

import static org.bytedeco.javacpp.opencv_core.*;

@MountPath("histogram")
public class HistogramPage extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(HistogramPage.class);

    private Histogram histogram = new Histogram();
    private byte[] origBytes;

    public HistogramPage(PageParameters parameters) {
        super(parameters);
        histogram.loadInput(new File("Beach.jpg"));

        final WebMarkupContainer resultDiv = new WebMarkupContainer("resultDiv");
        resultDiv.setOutputMarkupId(true);
        resultDiv.add(new Label("uniqueColorCount", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return histogram.getUniqueColorCount();
            }
        }));
        resultDiv.add(new MultiHistogramPanel("histogram", new AbstractReadOnlyModel<Histogram>() {
            @Override
            public Histogram getObject() {
                return histogram;
            }
        }));
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
                    try {
                        opencv_highgui.imencode(".png", origMat, bufPtr);
                        log.info("PNG Image: {} bytes", bufPtr.capacity());
                        final byte[] buf = new byte[bufPtr.capacity()];
                        bufPtr.get(buf);
                        return buf;
                    } finally {
                        bufPtr.deallocate();
                    }
                } else {
                    return new byte[0];
                }
            }
        };
        final Image origImg = new Image("origImg", origImgRes);
        origImg.setOutputMarkupId(true);
        form.add(origImg);

        final ListModel<FileUpload> filesModel = new ListModel<>();
        final FileUploadField fileFld = new FileUploadField("fileFld", filesModel);
        form.add(fileFld);
        form.add(new LaddaAjaxButton("loadBtn", new Model<>("Load"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload first = filesModel.getObject().get(0);
                origBytes = first.getBytes();
                histogram.loadInput(first.getContentType(), origBytes);
                info("Loaded file " + first.getClientFileName() + " (" + first.getContentType() + ")");
                target.add(origImg, resultDiv, notificationPanel);
            }
        });
        add(form);
    }

    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Histogram | Pengenalan Pola SHIH");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Pengenalan Pola SHIH: Meraba citra, mencari arti.");
    }

}
