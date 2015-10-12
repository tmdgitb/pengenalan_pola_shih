package id.ac.itb.sigit.pengenalanpola.web;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.SingleThemeProvider;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.stereotype.Component;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

@Component("daemonApp")
public class MyWebApplication extends WebApplication {
    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    public void mountPages() {
        new AnnotatedMountScanner().scanPackage(MyWebApplication.class.getPackage().getName()).mount(this);
    }

    @Override
    public void init() {
        super.init();
        getDebugSettings().setAjaxDebugModeEnabled(false);
        getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_EXCEPTION_PAGE);
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        final IBootstrapSettings bootstrapSettings = new BootstrapSettings()
                .useCdnResources(getConfigurationType() == RuntimeConfigurationType.DEPLOYMENT)
                .setThemeProvider(new SingleThemeProvider(BootswatchTheme.Cosmo));
        Bootstrap.install(this, bootstrapSettings);

        //Howler.install(this);

        mountPages();
    }
}
