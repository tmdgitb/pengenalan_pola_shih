package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.ZhangSuenService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
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

import javax.inject.Inject;
import java.util.List;

@MountPath("ZhangSuen")
public class ZhangSuenPage extends PubLayout {

    private static final Logger log = LoggerFactory.getLogger(ZhangSuenPage.class);

    //@Inject
    private ZhangSuenService zhangSuenService;

    public ZhangSuenPage(PageParameters parameters) {
        super(parameters);

        zhangSuenService=new ZhangSuenService();

        final Form<Void> form = new Form<>("form");

        final ListModel<FileUpload> filesModel = new ListModel<>();
        final FileUploadField fileFld = new FileUploadField("fileFld", filesModel);
        form.add(fileFld);


        TextField somethingField = new TextField("something");
        somethingField.setOutputMarkupId(true);

        final DynamicImageResource origImgRes = new DynamicImageResource("png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                final BytePointer bufPtr = new BytePointer();
                opencv_highgui.imencode(".png", zhangSuenService.getOrigMat(), bufPtr);
                log.info("PNG Image: {} bytes", bufPtr.capacity());
                final byte[] buf = new byte[bufPtr.capacity()];
                bufPtr.get(buf);
                return  buf;
            }
        };

        final  DynamicImageResource zhangSuenImgRees=new DynamicImageResource("png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                final BytePointer bufPtr = new BytePointer();
                opencv_highgui.imencode(".png", zhangSuenService.getOrigMat(), bufPtr);
                log.info("PNG Image: {} bytes", bufPtr.capacity());
                final byte[] buf = new byte[bufPtr.capacity()];
                bufPtr.get(buf);
                return  buf;
            }
         };

        final Image origImg = new Image("origImg",origImgRes);
        origImg.setOutputMarkupId(true);
        form.add(origImg);

        final Image zhangsuenImg = new Image("zhangSuenImg",zhangSuenImgRees);
        origImg.setOutputMarkupId(true);
        form.add(origImg);

        form.add(new LaddaAjaxButton("loadBtn", new Model<>("Load"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload first = filesModel.getObject().get(0);
                zhangSuenService.loadInput(first.getContentType(), first.getBytes());
                info("Loaded file " + first.getClientFileName() + " (" + first.getContentType() + ")");
                target.add(origImg, zhangsuenImg,notificationPanel);
            }
        });
        add(form);
    }

    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Pengenalan Pola SHIH: Meraba citra, mencari arti.");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Pengenalan Pola SHIH: Meraba citra, mencari arti.");
    }
}
