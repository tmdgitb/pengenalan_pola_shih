package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import id.ac.itb.sigit.pengenalanpola.Histogram;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
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
        setOutputMarkupId(true);
        final LoadableDetachableModel<String> valuesModel = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return "var values = " + Histogram.histToJson(model.getObject()) + ";\n" +
                        "var svgParent = d3.select('#" + getMarkupId() + "');";
            }
        };
        add(new Label("values", valuesModel).setEscapeModelStrings(false));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(
                new WebjarsJavaScriptResourceReference("d3/3.5.6/d3.js")));
    }
}
