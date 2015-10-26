package id.ac.itb.sigit.pengenalanpola.web;

import com.google.common.collect.ImmutableList;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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

    @Inject
    private ZhangSuenService zhangSuenService;

    public ZhangSuenPage(PageParameters parameters) {
        super(parameters);

//        zhangSuenService=new ZhangSuenService();

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

        final WebMarkupContainer resultDiv = new WebMarkupContainer("resultDiv");
        resultDiv.setOutputMarkupId(true);

        final Image zhangsuenImg = new Image("zhangSuenImg",zhangSuenImgRees);
        resultDiv.add(zhangsuenImg);

        final MultiLineLabel multilabelujung = new MultiLineLabel("jumlahujung", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject(){
                return zhangSuenService.getZhangSuenFitur().getEdges().size();//jumlah ujung
            }
        });
        resultDiv.add(multilabelujung);
        final AbstractReadOnlyModel<List<ZhangSuenEdge>> edgesModel = new AbstractReadOnlyModel<List<ZhangSuenEdge>>() {
            @Override
            public List<ZhangSuenEdge> getObject() {
                return zhangSuenService.getZhangSuenFitur().getEdges();
            }
        };
        resultDiv.add(new ListView<ZhangSuenEdge>("edgeLv", edgesModel) {
            @Override
            protected void populateItem(ListItem<ZhangSuenEdge> item) {
                item.add(new Label("x", item.getModelObject().getEdge().getX()));
                item.add(new Label("y", item.getModelObject().getEdge().getY()));
            }
        });

        final MultiLineLabel multilabelcabang = new MultiLineLabel("jumlahcabang", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject(){
                return zhangSuenService.getZhangSuenFitur().getCrosses().size(); // jumlah cabang
            }
        });
        resultDiv.add(multilabelcabang);
        final AbstractReadOnlyModel<List<ZhangSuenCross>> crossesModel = new AbstractReadOnlyModel<List<ZhangSuenCross>>() {
            @Override
            public List<ZhangSuenCross> getObject() {
                return zhangSuenService.getZhangSuenFitur().getCrosses();
            }
        };
        resultDiv.add(new ListView<ZhangSuenCross>("crossLv", crossesModel) {
            @Override
            protected void populateItem(ListItem<ZhangSuenCross> item) {
                item.add(new Label("x", item.getModelObject().getEdge().getX()));
                item.add(new Label("y", item.getModelObject().getEdge().getY()));
            }
        });

        final MultiLineLabel multilabelbulatan = new MultiLineLabel("jumlahbulatan", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject(){
                return zhangSuenService.getZhangSuenFitur().getLoops().size();//jumlah bulatan
            }
        });
        final AbstractReadOnlyModel<List<Loop>> loopsModel = new AbstractReadOnlyModel<List<Loop>>() {
            @Override
            public List<Loop> getObject() {
                return zhangSuenService.getZhangSuenFitur().getLoops();
            }
        };
        resultDiv.add(new ListView<Loop>("loopLv", loopsModel) {
            @Override
            protected void populateItem(ListItem<Loop> item) {
                item.add(new Label("x", item.getModelObject().getX()));
                item.add(new Label("y", item.getModelObject().getY()));
            }
        });
        resultDiv.add(multilabelbulatan);

        form.add(resultDiv);

        form.add(new LaddaAjaxButton("loadBtn", new Model<>("Load"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final int mode = modeImage.getModelObject() == GrayscaleMode.BLACK_ON_WHITE ? 1 : 0;
                final FileUpload first = filesModel.getObject().get(0);
                sdsad="dfs";
                zhangSuenService.loadInput(first.getContentType(), first.getBytes(),mode);
                info("Loaded file " + first.getClientFileName() + " (" + first.getContentType() + ")");
                target.add(origImg, resultDiv, notificationPanel);
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
