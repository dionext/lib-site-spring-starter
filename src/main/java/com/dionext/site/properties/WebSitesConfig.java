package com.dionext.site.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties("web-config")
public class WebSitesConfig {
    private SiteSettings website;
}
