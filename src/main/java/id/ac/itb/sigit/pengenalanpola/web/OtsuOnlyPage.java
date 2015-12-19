package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.OtsuThresholdingContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;

import javax.inject.Inject;

/**
 * Created by Sigit A on 10/30/2015.
 */
public class OtsuOnlyPage extends PubLayout {
    @Inject
    private OtsuThresholdingContainer zn ;
    private FileUploadField fileUpload = new FileUploadField("fileUpload");
    private String UPLOAD_FOLDER = "C:\\";

    public OtsuOnlyPage(PageParameters parameters) {
        super(parameters);
        zn.setInput("A.jpg");
        final Form<Void> form = new Form<Void>("form");
        Image inputan  = new Image("inputZhangSuen", new DynamicImageResource("image/png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return getInput();
            }
        });
        Image otsuan  = new Image("otsuThreshold", new DynamicImageResource("image/png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return getOtsuResult();
            }
        });

        Label markupLabel = new Label("markupLabel", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return zn.getHist().uniqueColor+", Threshold Value : "+zn.getThresholdValue();
            }
        });

        form.add(inputan);
        inputan.setOutputMarkupId(true);
        form.add(otsuan);
        otsuan.setOutputMarkupId(true);

        form.add(fileUpload);
        form.add(markupLabel);
        markupLabel.setOutputMarkupId(true);

        form.add(new LaddaAjaxButton("klik",new Model<>("Convert to Binnary"), Buttons.Type.Default){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload uploadedFile = fileUpload.getFileUpload();
                if (uploadedFile != null) {
                    zn.setInput(uploadedFile.getBytes());
                    target.add(inputan,otsuan,markupLabel);
                }
            }
        });
        add(form);
    }


    public byte[] getInput(){
        return zn.getInput();
    }

    public byte[] getOutput(){
        return zn.getOutput();
    }

    public byte[] getOtsuResult(){
        zn.setOutput2();
        return zn.getOtsuResult();
    }

    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Otsu Thresholding Algorithm");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Otsu Thresholding Algorithm");
    }


}
