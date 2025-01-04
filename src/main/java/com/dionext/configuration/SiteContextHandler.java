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
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private boolean redirectToMainPageRelative(HttpServletResponse response)  {
        //send 302 response.sendRedirect
        //send 301
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        String redirectedUrl = (pageInfo.getRequest().getRequestURI().endsWith("/")?"../":"") +
                (pageInfo.getLevel() == 1?(pageInfo.getSiteContextPrefix() + "/"):"") +
                pageInfo.getOffsetStringToContextLevel() +
                (pageInfo.isSiteLangInPath()?(pageInfo.getLocaleLang() + "/"):"") +
                pageInfo.getSiteSettings().getMainPage();
        response.setHeader("Location", redirectedUrl);
        return false;
    }
    private boolean redirectToLangPage(HttpServletResponse response) {
        // response.sendRedirect send 302
        // send 301
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        String redirectedUrl =
                pageInfo.getOffsetStringToContextLevel() +
                (pageInfo.isSiteLangInPath()?(pageInfo.getLocaleLang() + "/"):"") +
                pageInfo.getRelativePath() +
                (pageInfo.getRequest().getQueryString() != null ?("?" + pageInfo.getRequest().getQueryString()): "");
        response.setHeader("Location", redirectedUrl);
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws IOException {

        pageInfo.setRequest(request);
        String contextUrl = request.getRequestURI();

        Path contextPath = Paths.get(contextUrl).normalize();
        String[] tokens = new String[contextPath.getNameCount()];
        for (int i = 0; i < contextPath.getNameCount(); i++){
            tokens[i] = contextPath.getName(i).toString();
        }
        pageInfo.setLevel(tokens.length);
        log.debug("Start processing contextUrl: " + contextUrl);
        if (tokens.length > 0 && "error".equals(tokens[0]))//spring error page
            return true;

        //context recognition
        String firstToken = tokens.length > 0 ? tokens[0] : null;
        if (webSitesConfig.getMultiSites() != null && webSitesConfig.getMultiSites().size() > 0){
            //multisite
            if (Strings.isNullOrEmpty(firstToken))
                throw new ResourceFindException("Context (first path) empty, but it must be in URL for multi contexts site");
            SiteSettings siteSettings  = webSitesConfig.getMultiSites().get(firstToken);
            if (siteSettings == null)
                throw new ResourceFindException("Context " + firstToken + " (first path of URL) not found in multi contexts from site config");
            pageInfo.setSiteContextPrefix(firstToken);
            pageInfo.setSiteSettings(siteSettings);
        }
        else {
            pageInfo.setSiteContextPrefix(null);
            pageInfo.setSiteSettings(webSitesConfig.getWebsite());
        }
        if (pageInfo.getDefaultLang() != null &&
                !pageInfo.getLocaleLang().equals(pageInfo.getDefaultLang())) {
            pageInfo.setLocale(Locale.of(pageInfo.getDefaultLang()));
            if (Strings.isNullOrEmpty(pageInfo.getLocale().getLanguage())) {
                //empty when unsupported lang
                throw new ResourceFindException(LANG_PREFIX + pageInfo.getDefaultLang() + " is not valid value for locale");
            }
        }

        if ((pageInfo.getSiteContextPrefix() != null && tokens.length <= 1)
                || (pageInfo.getSiteContextPrefix() == null && tokens.length <= 0)) {
            return redirectToMainPageRelative(response);
        }
        String langToken = pageInfo.getSiteContextPrefix() != null?tokens[1]:tokens[0];

        //static, api, etc.  - without language in path
        // "", "context", "images" or "api"
        if (Arrays.stream(pageInfo.getSiteSettings().getStaticPrefixes()).anyMatch(langToken::equals) ||
            Arrays.stream(pageInfo.getSiteSettings().getApiPrefixes()).anyMatch(langToken::equals)) {
            //no lang for this pages
        }
        else {
            //language recognition
            Locale locale;
            if (pageInfo.getSiteSettings() != null &&
                    pageInfo.getSiteSettings().isSiteLangInPath()) {
                if (pageInfo.getSiteSettings().getSiteLangs() != null && pageInfo.getSiteSettings().getSiteLangs().length > 0
                        && Arrays.stream(pageInfo.getSiteSettings().getSiteLangs()).noneMatch(langToken::equals)) {
                    //test langToken for any valid locale
                    Locale testLocale = Locale.of(langToken);
                    if (Strings.isNullOrEmpty(testLocale.getLanguage())) {
                        pageInfo.setPathLang(pageInfo.getLocale().getLanguage());
                    }
                    //else langToken is not lang token
                    //redirect to lang page
                    setRelativePath(tokens);
                    return redirectToLangPage(response);
                }
                locale = Locale.of(langToken);
                if (Strings.isNullOrEmpty(locale.getLanguage())) {
                    //empty when unsupported lang
                    throw new ResourceFindException(LANG_PREFIX + langToken + " is not valid value for locale");
                }
                pageInfo.setPathLang(locale.getLanguage());
                pageInfo.setLocale(locale);
            } else {
                //detect lang by request
                AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = applicationContext.getBean(AcceptHeaderLocaleResolver.class);
                Locale requestLocale = acceptHeaderLocaleResolver.resolveLocale(request);
                if (Arrays.stream(pageInfo.getSiteSettings().getSiteLangs()).noneMatch(requestLocale.getLanguage()::equals)) {
                    log.warn("User requested locale " + requestLocale.getLanguage() + " is not in site settings");
                } else {
                    pageInfo.setLocale(requestLocale);
                }
            }
        }
        LocaleContextHolder.setLocale(pageInfo.getLocale());

        if (tokens.length <=  ((pageInfo.getSiteContextPrefix() != null?1:0)
                + (pageInfo.getPathLang() != null?1:0))){
            return redirectToMainPageRelative(response);
        }

        //relative path recognition
        setRelativePath(tokens);

        pageInfo.parseAdditionalParams();

        log.debug("Successfully processed relativePath: " + pageInfo.getRelativePath() +
                " locale lang: " + pageInfo.getLocaleLang() +
                " level: " + pageInfo.getLevel());

        return true;
    }

    private void setRelativePath(String[] tokens) {
        int i = 0;
        if (pageInfo.getSiteContextPrefix() != null) i++;
        if (pageInfo.getPathLang() != null) i++;
        StringBuilder relativePath = new StringBuilder();
        for (; i < tokens.length; i++){
            if (relativePath.length() > 0) relativePath.append("/");
            relativePath.append(tokens[i]);
        }
        pageInfo.setRelativePath(relativePath.toString());

    }
}