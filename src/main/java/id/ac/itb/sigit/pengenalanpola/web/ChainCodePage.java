package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.Geometry;
import id.ac.itb.sigit.pengenalanpola.ChainCodeService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
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
import java.util.*;

@MountPath("chaincode")
public class ChainCodePage extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(ChainCodePage.class);

    @Inject
    private ChainCodeService chainCodeService;


    public ChainCodePage(PageParameters parameters) {
        super(parameters);

        final Form<Void> form = new Form<>("form");

        final ListModel<FileUpload> filesModel = new ListModel<>();
        final FileUploadField fileFld = new FileUploadField("fileFld", filesModel);
        form.add(fileFld);

        final DynamicImageResource origImgRes = new DynamicImageResource("png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                if (chainCodeService.getOrigMat() != null) {
                    final BytePointer bufPtr = new BytePointer();
                    opencv_highgui.imencode(".png", chainCodeService.getOrigMat(), bufPtr);
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

        final WebMarkupContainer listchaincode = new WebMarkupContainer("listchaincode");
        listchaincode.setOutputMarkupId(true);

        IModel<List<Geometry>> listModel = new AbstractReadOnlyModel<List<Geometry>>() {
            @Override
            public List<Geometry> getObject() {
                return chainCodeService.getGeometries();
            }
        };
        ListView<Geometry> listview = new ListView<Geometry>("listview", listModel) {
            protected void populateItem(ListItem<Geometry> item) {
                Geometry geometry = item.getModelObject();
                item.add(new Label("chaincode", geometry.getChainCodeFcce()));
            }
        };

        listchaincode.add(listview);
        add(listchaincode);

        form.add(new LaddaAjaxButton("loadBtn", new Model<>("Load"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload first = filesModel.getObject().get(0);
                chainCodeService.loadInput(first.getContentType(), first.getBytes());
                info("Loaded file " + first.getClientFileName() + " (" + first.getContentType() + ")");
                target.add(origImg, listchaincode, notificationPanel);
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
