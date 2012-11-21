package de.agilecoders.wicket.markup.html.references;

import com.google.common.collect.Lists;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

/**
 * Language specific datepicker resource reference.
 *
 * @author miha
 * @version 1.0
 */
public class BootstrapDatepickerLangJsReference extends JavaScriptResourceReference {
    private static final long serialVersionUID = 1L;

    /**
     * Private constructor.
     */
    public BootstrapDatepickerLangJsReference(final String language) {
        super(BootstrapDatepickerLangJsReference.class, "js/lang/bootstrap-datepicker." + language + ".js");
    }

    @Override
    public Iterable<? extends HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());
        dependencies.add(JavaScriptHeaderItem.forReference(BootstrapDatepickerJsReference.INSTANCE));

        return dependencies;
    }
}
