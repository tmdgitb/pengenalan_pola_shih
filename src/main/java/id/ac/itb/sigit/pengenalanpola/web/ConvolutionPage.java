package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.ConvolutionContainer;
import id.ac.itb.sigit.pengenalanpola.OperatorOption;
import id.ac.itb.sigit.pengenalanpola.SelectOption;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit A on 11/1/2015.
 */
public class ConvolutionPage extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(ConvolutionPage.class);
    @Inject
    private ConvolutionContainer zn;
    private FileUploadField fileUpload = new FileUploadField("fileUpload");
    private String UPLOAD_FOLDER = "C:\\";
    private List<SelectOption> OPERATOR_CITRA;
    private SelectOption selected;

    public ConvolutionPage(PageParameters parameters) {
        super(parameters);
        zn.setInput("A.jpg");
        zn.processInput();
        OPERATOR_CITRA = new ArrayList<>();
        OPERATOR_CITRA.add(new SelectOption("Operator Custom", OperatorOption.CUSTOM_OPERATOR));
        OPERATOR_CITRA.add(new SelectOption("Operator Sobel", OperatorOption.SOBEL_OPERATOR));
        OPERATOR_CITRA.add(new SelectOption("Operator Prewit", OperatorOption.PREWIT_OPERATOR));
        selected = OPERATOR_CITRA.get(1);
        final Form<Void> form = new Form<Void>("form");
        DropDownChoice<SelectOption> listOperator;
        PropertyModel<SelectOption> dropdownmod = new PropertyModel<SelectOption>(this, "selected");
        listOperator = new DropDownChoice<SelectOption>("sites", dropdownmod, OPERATOR_CITRA);
        form.add(listOperator);
        Image inputan = new Image("input", new DynamicImageResource("image/png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return getInput();
            }
        });
        Image outputan = new Image("output", new DynamicImageResource("image/png") {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return getOutput();
            }
        });
        form.add(inputan);
        inputan.setOutputMarkupId(true);
        form.add(outputan);
        outputan.setOutputMarkupId(true);
        form.add(fileUpload);
        form.add(new LaddaAjaxButton("klik", new Model<>("Konvolusi Operator"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload uploadedFile = fileUpload.getFileUpload();
                if (uploadedFile != null) {
                    zn.setInput(uploadedFile.getBytes());
                    if (selected.getValue() != 0) {
                        zn.setOperatorKernel(selected.getValue());
                    } else {
                        zn.setOperatorKernel(10,1,1);
                    }
                    log.info("Yang terpilih {}", selected.getValue());
                    target.add(inputan, outputan);
                }
            }
        });
        add(form);
    }


    public byte[] getInput() {
        return zn.getInput();
    }

    public byte[] getOutput() {
        //zn.setOutput();
        zn.processInput();
        return zn.getConvolutedOutput();
    }


    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Deteksi Tepi");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Deteksi Tepi");
    }


}
