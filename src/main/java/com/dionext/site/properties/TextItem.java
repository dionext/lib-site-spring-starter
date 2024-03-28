package com.dionext.site.properties;

import com.dionext.utils.services.I18nService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class TextItem {

    private String name;
    private String key;

    public String getLabel(I18nService i18n) {
        if (key != null) return i18n.getString(key);
        else return name;
    }
}
