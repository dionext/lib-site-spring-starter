package com.dionext.configuration;

import com.dionext.site.properties.SiteSettings;
import com.dionext.site.properties.WebSitesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//https://stackoverflow.com/questions/42393211/how-can-i-serve-static-html-from-spring-boot
@Configuration
//@EnableWebMvc
@Slf4j
public class StaticResourceHandler implements WebMvcConfigurer {

    private WebSitesConfig webSitesConfig;

    @Autowired
    public void setWebSitesConfig(WebSitesConfig webSitesConfig) {
        this.webSitesConfig = webSitesConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("====================== StaticResourceHandler ");
        //to do root
        SiteSettings siteSettings = webSitesConfig.getWebsite();
        if (siteSettings != null && siteSettings.getStaticPrefixes() != null) {
            for (String folder : siteSettings.getStaticPrefixes()) {
                //only folders supported, not files
                String resourceHandler;
                String resourceLocations;
                resourceHandler = "/" + folder + "/**";
                resourceLocations = siteSettings.getSiteStaticStoragePath() + "/" + folder + "/";
                registry
                        .addResourceHandler(resourceHandler)
                        .addResourceLocations(resourceLocations)
                        .addResourceLocations("/public" + "/" + folder + "/")
                        .addResourceLocations("classpath:/static" + "/" + folder + "/");
                //.setCacheControl(CacheControl.maxAge(Duration.ofDays(365)))

                log.debug("static: " + resourceHandler + " ===> " + resourceLocations);
            }
        }
        registry
                .addResourceHandler("/**")
                .addResourceLocations("/public" +  "/")
                .addResourceLocations("classpath:/static" + "/");

        //order https://stackoverflow.com/questions/51768263/resourcehandler-conflict-with-wildcards-in-controllers
        //registry.setOrder(-1);
    }

}