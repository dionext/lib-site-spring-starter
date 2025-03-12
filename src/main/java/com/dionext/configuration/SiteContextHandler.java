package com.dionext.configuration;


import com.dionext.site.components.PageInfo;
import com.dionext.site.properties.WebSitesConfig;
import com.dionext.utils.exceptions.ResourceFindException;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

@Slf4j
@SuppressWarnings({"java:S6541", "java:S3776"})
public class SiteContextHandler implements HandlerInterceptor {
    private static final String[] SEARCH_ENGINE_USER_AGENTS = {
            "Googlebot",
            "Bingbot",
            "Slurp",
            "DuckDuckBot",
            "Baiduspider",
            "YandexBot",
            "Sogou",
            "Exabot",
            "facebot",
            "ia_archiver"
    };

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
                (pageInfo.getLevel() == 1?("/"):"") +
                pageInfo.getOffsetStringToContextLevel() +
                (pageInfo.isSiteLangInPath()?(pageInfo.getLocaleLang() + "/"):"") +
                pageInfo.getSiteSettings().getMainPage();
        response.setHeader("Location", redirectedUrl);
        return false;
    }
    private boolean redirectToMainPageAbsolute(HttpServletResponse response)  {
        //send 302 response.sendRedirect
        //send 301
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        String redirectedUrl =  pageInfo.getDomainUrl() +"/" +
                (pageInfo.isSiteLangInPath()?(pageInfo.getLocaleLang() + "/"):"") +
                pageInfo.getSiteSettings().getMainPage();
        response.setHeader("Location", redirectedUrl);
        return false;
    }
    private ResponseEntity<Void> redirect(String redirectRelAddres) {
        String newUrl = pageInfo.getDomainUrl() +"/"+ redirectRelAddres;
        //log.debug("newUrl: " + newUrl);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(
                URI.create(newUrl)).build();
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
        pageInfo.setSiteSettings(webSitesConfig.getWebsite());
        pageInfo.setSearchEngine(isSearchEngine(request.getHeader("User-Agent")));
        String[] tokens = parsePathTokens(request);
        if (tokens.length > 0 && "error".equals(tokens[0])) return true; //spring error page

        if (tokens.length == 0) return redirectToMainPageAbsolute(response);

        /*
        if (pageInfo.getDefaultLang() != null &&
                !pageInfo.getLocaleLang().equals(pageInfo.getDefaultLang())) {
            pageInfo.setLocale(Locale.of(pageInfo.getDefaultLang()));
            if (Strings.isNullOrEmpty(pageInfo.getLocale().getLanguage())) {
                //empty when unsupported lang
                throw new ResourceFindException(LANG_PREFIX + pageInfo.getDefaultLang() + " is not valid value for locale");
            }
        }
         */

        //if (tokens.length <= 0) {
          //  return redirectToMainPageRelative(response);
        //}

        String lang = detectLang(request, tokens);
        Locale locale = detectLocale(lang);

        pageInfo.setLocale(locale);
        LocaleContextHolder.setLocale(locale);

        if (pageInfo.getPathLang() != null && tokens.length < 2) return redirectToMainPageAbsolute(response);

        /*
        String langToken = request.getParameter("lang");
        if (langToken == null) langToken = tokens[0];

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
                    if (pageInfo.getSiteSettings().isRedirectToLangPage())
                        return redirectToLangPage(response);
                    else
                        return true;
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


        if (tokens.length <= 0
                + (pageInfo.getPathLang() != null?1:0)){
            return redirectToMainPageRelative(response);
        }
         */

        //relative path recognition
        setRelativePath(tokens);
        pageInfo.parseAdditionalParams();

        log.debug("Successfully processed relativePath: " + pageInfo.getRelativePath() +
                " locale lang: " + pageInfo.getLocaleLang() +
                " level: " + pageInfo.getLevel());

        return true;
    }

    private Locale detectLocale(String lang) {
        Locale locale = null;
        if (lang != null) locale = Locale.of(lang);
        else if (pageInfo.getDefaultLang() != null) locale = Locale.of(pageInfo.getDefaultLang());
        else locale = Locale.ENGLISH;

        //test locale
        if (Strings.isNullOrEmpty(locale.getLanguage())) {
            log.warn("Detected locale " + lang + " is not valid locale");
            locale = Locale.ENGLISH;
        }
        return locale;
    }

    private @Nullable String detectLang(HttpServletRequest request, String[] tokens) {
        //lang detection
        String lang = null;
        //1. by path
        if (pageInfo.getSiteSettings().isSiteLangInPath() && pageInfo.getSiteSettings().getSiteLangs() != null && tokens.length > 0){
            for(String l : pageInfo.getSiteSettings().getSiteLangs()){
                if (tokens[0].equals(l)){
                    lang = l;
                    pageInfo.setPathLang(lang);
                    break;
                }
            }
        }
        //2. by param
        if (lang == null) {
            lang = request.getParameter("lang");
            //3. by request
            if (lang == null) {
                AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = applicationContext.getBean(AcceptHeaderLocaleResolver.class);
                Locale requestLocale = acceptHeaderLocaleResolver.resolveLocale(request);
                lang = requestLocale.getLanguage();
            }
            //test lang
            if (lang != null){
                if (Arrays.stream(pageInfo.getSiteSettings().getSiteLangs()).noneMatch(lang::equals)) {
                    log.warn("Detected locale " + lang + " is not in site settings");
                    lang = null;
                }
            }
        }
        return lang;
    }

    private String @NotNull [] parsePathTokens(HttpServletRequest request) {
        String contextUrl = request.getRequestURI();
        Path contextPath = Paths.get(contextUrl).normalize();
        String[] tokens = new String[contextPath.getNameCount()];
        for (int i = 0; i < contextPath.getNameCount(); i++){
            tokens[i] = contextPath.getName(i).toString();
        }
        pageInfo.setLevel(tokens.length);
        log.debug("Start processing contextUrl: " + contextUrl);
        return tokens;
    }

    private void setRelativePath(String[] tokens) {
        int i = 0;
        if (pageInfo.getPathLang() != null) i++;
        StringBuilder relativePath = new StringBuilder();
        for (; i < tokens.length; i++){
            if (relativePath.length() > 0) relativePath.append("/");
            relativePath.append(tokens[i]);
        }
        pageInfo.setRelativePath(relativePath.toString());

    }
    private boolean isSearchEngine(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return false;
        }

        for (String botUserAgent : SEARCH_ENGINE_USER_AGENTS) {
            if (userAgent.contains(botUserAgent)) {
                return true;
            }
        }

        return false;
    }

}