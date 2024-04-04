package com.dionext.configuration;


import com.dionext.site.components.PageInfo;
import com.dionext.site.properties.SiteSettings;
import com.dionext.site.properties.WebSitesConfig;
import com.dionext.utils.exceptions.ResourceFindException;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

@Slf4j
@SuppressWarnings({"java:S6541", "java:S3776"})
public class SiteContextHandler implements HandlerInterceptor {

    public static final String LANG_PREFIX = "Lang prefix ";
    private WebSitesConfig webSitesConfig;
    private PageInfo pageInfo;
    private ApplicationContext applicationContext;

    @Autowired
    public void setWebSitesConfig(WebSitesConfig webSitesConfig) {
        this.webSitesConfig = webSitesConfig;
    }

    @Autowired
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Autowired
    public void setContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws IOException {

        String contextUrl = request.getRequestURI();
        String[] tokens = request.getRequestURI().trim().split("/");
        log.debug("contextUrl: " + contextUrl);

        int level = 0;
        SiteSettings siteSettings = null;
        String contextName = tokens.length > 1 ? tokens[1] : "";
        if (!Strings.isNullOrEmpty(contextName)
                && webSitesConfig.getMultiSites() != null && webSitesConfig.getMultiSites().size() > 0) {
            siteSettings = webSitesConfig.getMultiSites().get(contextName);
        }
        if (siteSettings != null) {
            level++;
        } else {
            //to do default
        }
        pageInfo.setRequest(request);
        pageInfo.setSiteContextPrefix(contextName);
        pageInfo.setSiteSettings(siteSettings);

        //static, api
        if (tokens.length > (level + 1)) {// "", "context", "images" or "api"
            if (Arrays.stream(siteSettings.getStaticPrefixes()).anyMatch(tokens[level + 1]::equals))
                return true;
            if (Arrays.stream(siteSettings.getApiPrefixes()).anyMatch(tokens[level + 1]::equals))
                return true;
        }


        Locale locale = null;
        if (siteSettings.isSiteLangInPath()) {
            String lang = tokens.length > (level + 1) ? tokens[level + 1] : null;
            if (lang == null || lang.isBlank()) {
                log.debug("Lang prefix not found in url but required for site settings");
                response.sendRedirect(contextUrl + (contextUrl.endsWith("/") ? "" : "/")
                        + siteSettings.getSiteLangs()[0] + "/"
                        + siteSettings.getMainPage());
                return false;

            }
            if (siteSettings.getSiteLangs() != null && siteSettings.getSiteLangs().length > 0
                    && Arrays.stream(siteSettings.getSiteLangs()).noneMatch(lang::equals)) {
                throw new ResourceFindException(LANG_PREFIX + lang + " is not in site langs settings ");
                //to do redirect
            }
            locale = Locale.of(lang);
            if (Strings.isNullOrEmpty(locale.getLanguage())) {
                //empty when unsupported lang
                throw new ResourceFindException(LANG_PREFIX + lang + " is not valid value for locale");
                //to do redirect
            }
            level++;
        } else {

            AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = applicationContext.getBean(AcceptHeaderLocaleResolver.class);
            Locale requestLocale = acceptHeaderLocaleResolver.resolveLocale(request);
            log.debug("locale: " + requestLocale);
            if (Arrays.stream(siteSettings.getSiteLangs()).noneMatch(requestLocale.getLanguage()::equals)) {
                log.warn("User requested locale " + requestLocale.getLanguage() + " is not in site settings");
                String lang = null;

                if (siteSettings.getSiteLangs() != null && siteSettings.getSiteLangs().length > 0) {
                    lang = siteSettings.getSiteLangs()[0];
                } else {
                    lang = "en";
                }

                locale = Locale.of(lang);
                if (Strings.isNullOrEmpty(locale.getLanguage())) {
                    //empty when unsupported lang
                    throw new ResourceFindException(LANG_PREFIX + lang + " is not valid value for locale");
                }
            } else locale = requestLocale;
        }
        pageInfo.setLang(locale.getLanguage());
        LocaleContextHolder.setLocale(locale);

        level++;
        int startRelativePathIndex = -1;
        for (int i = 0; i < level; i++) {
            startRelativePathIndex = contextUrl.indexOf("/", startRelativePathIndex + 1);
        }

        String relativePath = null;
        if (startRelativePathIndex != -1)
            relativePath = contextUrl.substring(startRelativePathIndex + 1);

        if (relativePath == null || relativePath.trim().isEmpty()) {
            //send 302
            response.sendRedirect(contextUrl + (contextUrl.endsWith("/") ? "" : "/") + siteSettings.getMainPage());
            //send 301
            //use response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            //use response.setHeader("Location", newURL);
            return false;

        }

        int occurrence = StringUtils.countOccurrencesOf(relativePath, "/");
        level += occurrence;

        pageInfo.setRelativePath(relativePath);
        pageInfo.setLevel(level);
        pageInfo.parseAdditionalParams();

        log.debug("Processing relativePath: " + pageInfo.getRelativePath() + " level: " + pageInfo.getLevel());

        return true;
    }
}