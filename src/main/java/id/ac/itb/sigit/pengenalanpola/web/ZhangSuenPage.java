package id.ac.itb.sigit.pengenalanpola.web;

import com.google.common.collect.ImmutableList;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.GrayscaleMode;
import id.ac.itb.sigit.pengenalanpola.ZhangSuenService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
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

import javax.inject.Inject;
import java.util.List;

@MountPath("ZhangSuen")
public class ZhangSuenPage extends PubLayout {

    private static final Logger log = LoggerFactory.getLogger(ZhangSuenPage.class);
    private String sdsad="";

    //@Inject
    private ZhangSuenService zhangSuenService;

    public ZhangSuenPage(PageParameters parameters) {
        super(parameters);

        zhangSuenService=new ZhangSuenService();



        final Form<Void> form = new Form<>("form");

        final ListModel<FileUpload> filesModel = new ListModel<>();
        final FileUploadField fileFld = new FileUploadField("fileFld", filesModel);
        form.add(fileFld);

        final Model<GrayscaleMode> modeImageModel = new Model<>(GrayscaleMode.WHITE_ON_BLACK);
        final RadioChoice<GrayscaleMode> modeImage = new RadioChoice<>("modeImage",
                modeImageModel, ImmutableList.copyOf(GrayscaleMode.values()));
        form.add(modeImage);

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
                opencv_highgui.imencode(".png", zhangSuenService.getZhainSuenMat(), bufPtr);
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
        zhangsuenImg.setOutputMarkupId(true);
        form.add(zhangsuenImg);

        MultiLineLabel multilabelujung = new MultiLineLabel("jumlahujung", new AbstractReadOnlyModel<Object>() {
            @Override
            public String getObject(){
                return zhangSuenService.getZhangSuenFitur().getUjungString();//jumlah ujung
            }
        });
        form.add(multilabelujung);
        multilabelujung.setOutputMarkupId(true);

        MultiLineLabel multilabelcabang = new MultiLineLabel("jumlahcabang", new AbstractReadOnlyModel<Object>() {
            @Override
            public String getObject(){
                return zhangSuenService.getZhangSuenFitur().getSimpanganString();// jumlah cabang
            }
        });
        form.add(multilabelcabang);
        multilabelcabang.setOutputMarkupId(true);

        MultiLineLabel multilabelbulatan = new MultiLineLabel("jumlahbulatan", new AbstractReadOnlyModel<Object>() {
            @Override
            public String getObject(){
                return zhangSuenService.getZhangSuenFitur().getBulatanString();//jumlah bulatan
            }
        });
        form.add(multilabelbulatan);
        multilabelbulatan.setOutputMarkupId(true);

        form.add(new LaddaAjaxButton("loadBtn", new Model<>("Load"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final int mode = modeImage.getModelObject() == GrayscaleMode.BLACK_ON_WHITE ? 1 : 0;
                final FileUpload first = filesModel.getObject().get(0);
                sdsad="dfs";
                zhangSuenService.loadInput(first.getContentType(), first.getBytes(),mode);
                info("Loaded file " + first.getClientFileName() + " (" + first.getContentType() + ")");
                target.add(origImg, zhangsuenImg,multilabelujung,multilabelcabang,multilabelbulatan,notificationPanel);
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
