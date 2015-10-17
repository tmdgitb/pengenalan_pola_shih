package id.ac.itb.sigit.pengenalanpola.web;

import id.ac.itb.sigit.pengenalanpola.RecognizedSymbol;
import com.google.common.collect.ImmutableList;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.ChainCodeService;
import id.ac.itb.sigit.pengenalanpola.Geometry;
import id.ac.itb.sigit.pengenalanpola.GrayscaleMode;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
import java.util.ArrayList;
import java.util.List;

@MountPath("chaincodeiml")
public class ChainCodeImlPage extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(ChainCodeImlPage.class);

    @Inject
    private ChainCodeService chainCodeService;

    private List<Geometry> dataTraining=new ArrayList<>();

    private List<RecognizedSymbol> hasilPengenalan = new ArrayList<>();

    public ChainCodeImlPage(PageParameters parameters) {
        super(parameters);


        final Form<Void> form = new Form<>("form");

        final ListModel<FileUpload> filesModel = new ListModel<>();
        final FileUploadField fileFld = new FileUploadField("fileFld", filesModel);
        form.add(fileFld);

        final Model<GrayscaleMode> modeImageModel = new Model<>(GrayscaleMode.WHITE_ON_BLACK);
        final RadioChoice<GrayscaleMode> modeImage = new RadioChoice<>("modeImage",
                modeImageModel, ImmutableList.copyOf(GrayscaleMode.values()));
        form.add(modeImage);

        final TextField<String> msgImage = new TextField<String>("msgImage",
                Model.of(""));
        form.add(msgImage);

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


        form.add(new LaddaAjaxButton("loadBtn", new Model<>("Load"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                log.info("mode: {} ", modeImage.getModelObject());
                final String msg = (String) msgImage.getModelObject();
                log.info("Message: {} ", msg);
                final int mode = modeImage.getModelObject() == GrayscaleMode.BLACK_ON_WHITE ? 1 : 0;

                final FileUpload first = filesModel.getObject().get(0);
                chainCodeService.loadInput(first.getContentType(), first.getBytes(), mode);

                Geometry datageometry= chainCodeService.getGeometries().get(0);
                datageometry.setCharacter(msg);
                dataTraining.add(datageometry);

                info("Loaded file " + first.getClientFileName() + " (" + first.getContentType() + ")");
                target.add(origImg, notificationPanel);
            }
        });
        add(form);

        //===============================================list============================================//
        final WebMarkupContainer listPengenalan = new WebMarkupContainer("listPengenalan");
        listPengenalan.setOutputMarkupId(true);

        IModel<List<RecognizedSymbol>> listModel = new AbstractReadOnlyModel<List<RecognizedSymbol>>() {
            @Override
            public List<RecognizedSymbol> getObject() {
                return hasilPengenalan;
            }
        };
        ListView<RecognizedSymbol> listview = new ListView<RecognizedSymbol>("listview", listModel) {
            protected void populateItem(ListItem<RecognizedSymbol> item) {
                final RecognizedSymbol recognizedSymbol = item.getModelObject();
                item.add(new Label("recognize", recognizedSymbol.getName()));
            }
        };

        listPengenalan.add(listview);
        add(listPengenalan);

        //=====================================form 2======================================================//


        final Form<Void> form2 = new Form<>("form2");
        final ListModel<FileUpload> filesModel2 = new ListModel<>();
        final FileUploadField fileFld2 = new FileUploadField("fileFld2", filesModel2);
        form2.add(fileFld2);

        final Model<GrayscaleMode> modeImageModel2 = new Model<>(GrayscaleMode.WHITE_ON_BLACK);
        final RadioChoice<GrayscaleMode> modeImage2 = new RadioChoice<>("modeImage2",
                modeImageModel2, ImmutableList.copyOf(GrayscaleMode.values()));
        form2.add(modeImage2);

        final DynamicImageResource origImgRes2 = new DynamicImageResource("png") {
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
        final Image origImg2 = new Image("origImg2", origImgRes);
        origImg2.setOutputMarkupId(true);
        form2.add(origImg2);
        form2.add(new LaddaAjaxButton("loadBtn2", new Model<>("Load"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                log.info("mode: {} ", modeImage2.getModelObject());
                final int mode = modeImage2.getModelObject() == GrayscaleMode.BLACK_ON_WHITE ? 1 : 0;
                final FileUpload first = filesModel2.getObject().get(0);
                chainCodeService.loadInput(first.getContentType(), first.getBytes(), mode);

                hasilPengenalan = new ArrayList<>();

                for (int i = 0; i < chainCodeService.getGeometries().size(); i++) {
                    final Geometry charPlat = chainCodeService.getGeometries().get(i);
                    for (int j = 0; j < dataTraining.size(); j++) {
                        final Geometry trainingCode = dataTraining.get(j);
                        final String resampledPlat = Geometry.resample(charPlat.getKodeBelok(), trainingCode.getKodeBelok().length());
                        final double confidence = Geometry.match(resampledPlat, trainingCode.getKodeBelok());
                        if (confidence >= 0.6) {
                            log.info("Matched {}% {}: actual={} training={}",
                                    Math.round(confidence * 100), trainingCode.getCharacter(), resampledPlat, trainingCode.getKodeBelok());
                            hasilPengenalan.add(new RecognizedSymbol(trainingCode.getCharacter(), charPlat, confidence));
                            break;
                        }
                    }
                }

                for (int i = 0; i < hasilPengenalan.size(); i++) {
                    final RecognizedSymbol recognized = hasilPengenalan.get(i);
                    log.info("Found #{} {}% at ({},{}): {}",
                            i, Math.round(recognized.getConfidence() * 100),
                            recognized.getGeometry().getX(), recognized.getGeometry().getY(), recognized.getName());
                }

                info("Loaded file " + first.getClientFileName() + " (" + first.getContentType() + ")");
                target.add(origImg2, listPengenalan,notificationPanel);
            }
        });
        add(form2);
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
