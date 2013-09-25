package de.agilecoders.wicket.core.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * A special theme providers that contains only a single theme.
 *
 * @author miha
 */
public class SingleThemeProvider implements ThemeProvider {
    private final ITheme theme;

    /**
     * Construct.
     *
     * @param theme the theme to provide
     */
    public SingleThemeProvider(ITheme theme) {
        this.theme = theme;
    }

    @Override
    public ITheme byName(String name) {
        return theme;
    }

    @Override
    public List<ITheme> available() {
        final List<ITheme> themes = new ArrayList<ITheme>();
        themes.add(theme);

        return themes;
    }

    @Override
    public ITheme defaultTheme() {
        return theme;
    }
}
