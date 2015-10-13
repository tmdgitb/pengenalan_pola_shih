package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import java.io.File;
import java.nio.ByteBuffer;

@MountPath("histogram")
public class HistogramPage extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(HistogramPage.class);

    public HistogramPage(PageParameters parameters) {
        super(parameters);
        final DynamicImageResource origImgRes = new DynamicImageResource("png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                final File imageFile = new File("Beach.jpg");
                log.info("Processing image file '{}' ...", imageFile);
                final opencv_core.Mat imgMat = opencv_highgui.imread(imageFile.getPath());
                final BytePointer bufPtr = new BytePointer();
                opencv_highgui.imencode(".png", imgMat, bufPtr);
                log.info("PNG Image: {} bytes", bufPtr.capacity());
                final byte[] buf = new byte[bufPtr.capacity()];
                bufPtr.get(buf);
                return buf;
            }
        };
        add(new Image("origImg", origImgRes));
    }

    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Histogram | Pengenalan Pola SHIH");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Pengenalan Pola SHIH: Meraba citra, mencari arti.");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(
                new WebjarsJavaScriptResourceReference("d3") ));
    }
}
