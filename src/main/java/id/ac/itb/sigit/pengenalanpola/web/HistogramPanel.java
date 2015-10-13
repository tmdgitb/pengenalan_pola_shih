package id.ac.itb.sigit.pengenalanpola.web;

import id.ac.itb.sigit.pengenalanpola.Histogram;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * Created by ceefour on 13/10/2015.
 */
public class HistogramPanel extends GenericPanel<int[]> {

    public HistogramPanel(String id, IModel<int[]> model) {
        super(id, model);
        final LoadableDetachableModel<String> valuesModel = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return Histogram.histToJson(model.getObject());
            }
        };
        add(new Label("values", valuesModel).setEscapeModelStrings(false));
    }
}
