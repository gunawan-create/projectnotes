package com.example.projectnotes;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {

    public static Context setLocale(Context context, String language) {

        if (language == null || language.isEmpty()) {
            language = "id"; // default aman
        }

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config =
                new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
