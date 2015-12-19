package id.ac.itb.sigit.pengenalanpola.web;

//import com.ocr.thinning.*;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.ColorMapGroupingContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by Sigit A on 11/15/2015.
 */
public class ColorMapGroupingPage extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(ColorMapGroupingPage.class);
    @Inject
    private ColorMapGroupingContainer colorMapGroupingContainer;
    private FileUploadField fileUpload = new FileUploadField("fileUpload");
    private FileUploadField sampleUpload = new FileUploadField("sampleUpload");

    public ColorMapGroupingPage(PageParameters parameters) {
        super(parameters);
        colorMapGroupingContainer.setInput("A.jpg");
        final Form<Void> form = new Form<Void>("form");
        Image inputan = new Image("input", new DynamicImageResource("image/png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return getInput();
            }
        });
        Image bin = new Image("bin", new DynamicImageResource("image/png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return getBin();
            }
        });
        Image outputan = new Image("output", new DynamicImageResource("image/png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return getOutput();
            }
        });
        Image sampleImage = new Image("sample", new DynamicImageResource("image/png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return getSample();
            }
        });
        form.add(inputan);
        inputan.setOutputMarkupId(true);
        form.add(sampleImage);
        sampleImage.setOutputMarkupId(true);
        form.add(outputan);
        outputan.setOutputMarkupId(true);
        form.add(bin);
        bin.setOutputMarkupId(true);
        form.add(fileUpload);
        form.add(sampleUpload);
        final TextField<String> group = new TextField<String>("group", Model.of(""));
        form.add(group);
        form.add(new LaddaAjaxButton("klik", new Model<>("Upload Image"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload uploadedFile = fileUpload.getFileUpload();
                if (uploadedFile != null) {
                    colorMapGroupingContainer.setInput(uploadedFile.getBytes());
                    colorMapGroupingContainer.colorGrouping();
                    target.add(inputan, bin, outputan);
                }
            }
        });
        form.add(new LaddaAjaxButton("klikSample", new Model<>("Upload Sample"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload uploadedFile = sampleUpload.getFileUpload();
                if (uploadedFile != null) {
                    colorMapGroupingContainer.setSample(uploadedFile.getBytes());
                    colorMapGroupingContainer.setColorMaps(group.getValue(),60);
                    target.add(sampleImage);
                }
            }
        });
        form.add(new LaddaAjaxButton("resetSample", new Model<>("Reset Sample"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                colorMapGroupingContainer.resetColorMap();
            }
        });
        add(form);
    }


    public byte[] getInput() {
        return colorMapGroupingContainer.getInput();
    }

    public byte[] getBin(){
        return colorMapGroupingContainer.getBin();
    }

    public byte[] getOutput() {
        //zn.setOutput();
        //zn.processImg(selected.getValue());
        return colorMapGroupingContainer.getOutput();
    }

    public byte[] getSample(){
        return colorMapGroupingContainer.getSample();
    }
    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Color Mapping");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Color Mapping");
    }

}


