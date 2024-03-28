package com.dionext.utils.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class I18nService {
    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getString(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, locale);
    }

    public String getString(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    public void setLocaleByLang(String lang) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(lang));
    }
}
