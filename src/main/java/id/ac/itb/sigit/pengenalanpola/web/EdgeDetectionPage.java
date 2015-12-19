

package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import id.ac.itb.sigit.pengenalanpola.ImageSharpnessContainer;
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
 * Created by Sigit on 19/12/2015.
 */
public class EdgeDetectionPage extends PubLayout {
    private static final Logger log = LoggerFactory.getLogger(EdgeDetectionPage.class);
    @Inject
    private ImageSharpnessContainer zn;
    private FileUploadField fileUpload = new FileUploadField("fileUpload");
    private String UPLOAD_FOLDER = "C:\\";
    private List<SelectOption> OPERATOR_SHARPNESS;
    private SelectOption selected;

    public EdgeDetectionPage(PageParameters parameters) {
        super(parameters);
        zn.setInput("A.jpg");
        zn.processImg(1);
        OPERATOR_SHARPNESS = new ArrayList<>();
        OPERATOR_SHARPNESS.add(new SelectOption("Operator Homogen", OperatorOption.HOMOGEN_SHARPNESS));
        OPERATOR_SHARPNESS.add(new SelectOption("Operator Difference", OperatorOption.DIFFERENCE_SHARPNESS));
        selected = OPERATOR_SHARPNESS.get(1);
        final Form<Void> form = new Form<Void>("form");
        DropDownChoice<SelectOption> listOperator;
        PropertyModel<SelectOption> dropdownmod = new PropertyModel<SelectOption>(this, "selected");
        listOperator = new DropDownChoice<SelectOption>("sites", dropdownmod, OPERATOR_SHARPNESS);
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
        form.add(new LaddaAjaxButton("klik", new Model<>("Operasikan Operator"), Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                final FileUpload uploadedFile = fileUpload.getFileUpload();
                if (uploadedFile != null) {
                    zn.setInput(uploadedFile.getBytes());
                    zn.processImg(selected.getValue());
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
        //zn.processImg(selected.getValue());
        return zn.getOutput();
    }


    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Deteksi Tepi Orde 0");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Deteksi Tepi Orde 0");
    }
}
