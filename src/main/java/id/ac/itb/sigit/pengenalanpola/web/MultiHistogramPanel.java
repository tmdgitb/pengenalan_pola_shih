package id.ac.itb.sigit.pengenalanpola.web;

import com.google.common.collect.ImmutableList;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import id.ac.itb.sigit.pengenalanpola.Histogram;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by ceefour on 13/10/2015.
 */
public class MultiHistogramPanel extends GenericPanel<Histogram> {

    public MultiHistogramPanel(String id, IModel<Histogram> model) {
        super(id, model);
        setOutputMarkupId(true);
//        final LoadableDetachableModel<String> valuesModel = new LoadableDetachableModel<String>() {
//            @Override
//            protected String load() {
//                return "var values = " + Histogram.histToJson(model.getObject()) + ";\n" +
//                        "var svgParent = d3.select('#" + getMarkupId() + "');";
//            }
//        };
//        add(new Label("values", valuesModel).setEscapeModelStrings(false));

        final LoadableDetachableModel<String> c3Model = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                final Histogram histogram = model.getObject();
                final String dataJson = Histogram.histToJsonC3(Histogram.AxisScale.LOG2, histogram.getGrayscale(),
                        histogram.getRed(), histogram.getGreen(), histogram.getBlue());
                return "var chart = c3.generate({\n" +
                        "    bindto: '#" + getMarkupId() + " .chart',\n" +
                        "    data: {" +
                        "        columns: " + dataJson + "," +
                        "        colors: {grayscale: 'gray', red: 'red', green: 'green', blue: 'blue'},\n" +
                        "        types: {grayscale: 'bar'}\n" +
                        "    }" +
                        "});";
            }
        };
        add(new Label("c3", c3Model).setEscapeModelStrings(false));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final WebjarsJavaScriptResourceReference D3_JS = new WebjarsJavaScriptResourceReference("d3/current/d3.js");
        final WebjarsCssResourceReference C3_CSS = new WebjarsCssResourceReference("c3/current/c3.css");
        final WebjarsJavaScriptResourceReference C3_JS = new WebjarsJavaScriptResourceReference("c3/current/c3.js") {
            @Override
            public List<HeaderItem> getDependencies() {
                return ImmutableList.of(
                        CssHeaderItem.forReference(C3_CSS),
                        JavaScriptHeaderItem.forReference(D3_JS));
            }
        };
        response.render(JavaScriptHeaderItem.forReference(C3_JS));
    }
}
