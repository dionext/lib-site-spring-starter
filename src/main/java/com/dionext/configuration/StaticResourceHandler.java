package com.dionext.configuration;

import com.dionext.site.properties.SiteSettings;
import com.dionext.site.properties.WebSitesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//https://stackoverflow.com/questions/42393211/how-can-i-serve-static-html-from-spring-boot
@Configuration
@EnableWebMvc
@Slf4j
public class StaticResourceHandler implements WebMvcConfigurer {

    private WebSitesConfig webSitesConfig;

    @Autowired
    public void setWebSitesConfig(WebSitesConfig webSitesConfig) {
        this.webSitesConfig = webSitesConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //to do root
        SiteSettings siteSettings = webSitesConfig.getWebsite();
        for (String folder : siteSettings.getStaticPrefixes()) {
            //only folders supported, not files
            String resourceHandler;
            String resourceLocations;
            resourceHandler = "/" + folder + "/**";
            resourceLocations = siteSettings.getSiteStaticStoragePath() + "/" + folder + "/";
            registry
                    .addResourceHandler(resourceHandler)
                    .addResourceLocations(resourceLocations);
            log.debug("static: " + resourceHandler + " ===> " + resourceLocations);
        }

        //order https://stackoverflow.com/questions/51768263/resourcehandler-conflict-with-wildcards-in-controllers
        registry.setOrder(-1);
    }

}