package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import id.ac.itb.sigit.pengenalanpola.Histogram;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.io.File;

@MountPath("histogram")
public class HistogramPage extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(HistogramPage.class);

    @Inject
    private Histogram histogram;

    public HistogramPage(PageParameters parameters) {
        super(parameters);
        histogram.loadInput(new File("Beach.jpg"));
        histogram.run();
        final DynamicImageResource origImgRes = new DynamicImageResource("png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                final BytePointer bufPtr = new BytePointer();
                opencv_highgui.imencode(".png", histogram.getOrigMat(), bufPtr);
                log.info("PNG Image: {} bytes", bufPtr.capacity());
                final byte[] buf = new byte[bufPtr.capacity()];
                bufPtr.get(buf);
                return buf;
            }
        };
        add(new Image("origImg", origImgRes));
        add(new Label("uniqueColorCount", histogram.getUniqueColorCount()));
        add(new MultiHistogramPanel("histogram"));
//        add(new HistogramPanel("grayscale", new Model<>(histogram.getGrayscale())));
        /*add(new HistogramPanel("red", new Model<>(histogram.getRed())));
        add(new HistogramPanel("green", new Model<>(histogram.getGreen())));
        add(new HistogramPanel("blue", new Model<>(histogram.getBlue())));*/
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
