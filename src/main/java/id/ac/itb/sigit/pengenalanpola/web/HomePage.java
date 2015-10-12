package id.ac.itb.sigit.pengenalanpola.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HomePage extends PubLayout {

    public HomePage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public IModel<String> getTitleModel() {
        return new Model<>("Pengenalan Pola: Meraba citra, mencari arti.");
    }

    @Override
    public IModel<String> getMetaDescriptionModel() {
        return new Model<>("Pengenalan Pola: Meraba citra, mencari arti.");
    }
}
