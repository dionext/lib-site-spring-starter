package com.dionext.site.components;

import com.dionext.site.properties.SiteSettings;
import com.dionext.utils.HtmlUtils;
import com.dionext.utils.services.I18nService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Locale;

@Component
@RequestScope
@Slf4j
public class PageInfo {
    public static final String REQUEST_HEADER_OFFLINE_CREATION_MODE = "REQUEST_HEADER_OFFLINE_CREATION_MODE";
    I18nService i18n;
    SiteSettings siteSettings;
    private String relativePath;
    private String extension;
    private String entityName;
    private String id;
    private int pageNum;
    private boolean list;
    private String pathLang;
    private Locale locale = Locale.ENGLISH;
    /**
     * count of segments in request.getRequestURI()
     */
    private int level;
    private String siteContextPrefix;
    private String pageTitle;
    private String keywords;
    //The suggested length of meta description is somewhere between 150 to 160 characters including spaces.
    private String description;
    //private String url;//abs
    private String pageImage;//abs

    private HttpServletRequest request;

    @Autowired
    public void setI18n(I18nService i18n) {
        this.i18n = i18n;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getSiteContextPrefix() {
        return siteContextPrefix;
    }

    public void setSiteContextPrefix(String siteContextPrefix) {
        this.siteContextPrefix = siteContextPrefix;
    }

    public String[] getPageLangs() {
        if (siteSettings == null) return new String[]{};
        return siteSettings.getSiteLangs();
    }

    public boolean isSiteLangInPath() {
        return siteSettings.isSiteLangInPath();
    }


    public String getSiteProtocol() {
        return siteSettings.getSiteProtocol();
    }

    /**
     * @return request path without context, lang and leading slash
     * For example: for http://sitename.com/dioportal/en/folder/page.htm  it will be: folder/page.htm
     */
    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getPathLang() {
        return pathLang;
    }
    public String getLocaleLang() {
        return locale.getLanguage();
    }

    public void setPathLang(String pathLang) {
        this.pathLang = pathLang;
    }

    public String getDefaultLang() {
        if (siteSettings == null || siteSettings.getSiteLangs() == null || siteSettings.getSiteLangs().length == 0 )
            return locale.getLanguage();
        return siteSettings.getSiteLangs()[0];
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getSiteDomain() {
        return siteSettings.getSiteDomain();
    }

    public String getPageTitle() {
        if (pageTitle != null) return pageTitle;
        else {
            if (siteSettings != null)
                return siteSettings.getSiteTitle().getLabel(i18n);
            else return null;
        }
    }
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void addPageTitle(String pageTitle) {
        StringBuilder str = new StringBuilder();
        if (pageTitle != null) {
            str.append(pageTitle);
            String siteTitle = siteSettings.getSiteTitle().getLabel(i18n);
            if (siteTitle != null) {
                if (str.length() > 0 && !str.toString().endsWith("."))
                    str.append(". ");
                str.append(siteTitle);
            }
            this.pageTitle =  str.length() > 0 ? str.toString() : "";
        }
    }

    public String getKeywords() {
        if (keywords != null) return keywords;
        else {
            if (siteSettings != null)
                return siteSettings.getKeywords().getLabel(i18n);
            else return null;
        }
    }
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void addKeywords(String keywords) {
        StringBuilder str = new StringBuilder();
        if (keywords != null) {
            str.append(keywords);
            String siteKeywords = siteSettings.getKeywords().getLabel(i18n);
            if (siteKeywords != null) {
                if (str.length() > 0) str.append(", ");
                str.append(siteKeywords);
            }
            this.keywords = str.length() > 0 ? str.toString() : null;
        }
    }

    public String getDescription() {
        if (description != null) return description;
        else {
            if (siteSettings != null)
                return siteSettings.getDescription().getLabel(i18n);
            else return null;
        }
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void addDescription(String description)
    {
        StringBuilder str = new StringBuilder();
        if (description != null) {
            str.append(description);
            String siteDescription = siteSettings.getDescription().getLabel(i18n);
            if (siteDescription != null) {
                if (str.length() > 0 && !str.toString().endsWith("."))
                    str.append(". ");
                str.append(siteDescription);
            }
            this.description = str.length() > 0 ? str.toString() : null;
        }
    }

    public String getPageImage() {
        return pageImage != null ? pageImage : siteSettings.getPageFaceImage();
    }

    public void setPageImage(String pageImage) {
        this.pageImage = pageImage;
    }

    public boolean isAnotherLangPageExist() {
        return (siteSettings.getSiteLangs().length > 1);
    }

    public String[] getSiteStoragePaths() {
        return siteSettings.getSiteStoragePaths();
    }

    public String[] getSiteInternalStoragePaths() {
        return siteSettings.getSiteInternalStoragePaths();
    }


    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getId() {
        return id;
    }

    public boolean isList() {
        return list;
    }

    public String getExtension() {
        return extension;
    }

    public String getEntityName() {
        return entityName;
    }

    public int getPageNum() {
        return pageNum;
    }

    public String getSiteTitle() {
        return siteSettings.getSiteTitle().getLabel(i18n);
    }


    public SiteSettings getSiteSettings() {
        return siteSettings;
    }

    public void setSiteSettings(SiteSettings siteSettings) {
        this.siteSettings = siteSettings;
    }

    //////////////

    public String getPageUrl() {
        return getDomainUrl() + (getPathLang() != null ?("/" + getPathLang()):"") + "/" + getRelativePath();
    }

    public String getDomainUrl() {
        return getSiteProtocol() + "://" + getSiteDomain();
    }

    private String getLevelString(int level) {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < level; i++) {
            str.append("../");
        }
        return str.toString();
    }

    /**
     * Example:
     * http://domain.com/context/lang/folder1/folder2/page.htm
     * level is 5
     * offset is 2 if context and lang used for this site
     * http://domain.com/context/lang/folder1/folder2/../../page.htm
     * new position
     * http://domain.com/context/lang/newpage.htm
     *
     * http://domain.com/context/folder1/folder2/page.htm
     * level is 4
     * offset is 2 if context used for this site, but lang is not
     * http://domain.com/context/folder1/folder2/../../page.htm
     * new position
     * http://domain.com/context/newpage.htm
     * @return
     */
    public String getOffsetStringToNavigationRoot() {
        int offset = getLevel() - 1;
        if (siteContextPrefix != null) offset--;
        if (pathLang != null) offset--;
        return getLevelString(offset);
    }

    /**
     * http://domain.com/context/lang/folder1/folder2/page.htm
     * level is 5
     * offset is 3
     * http://domain.com/context/lang/folder1/folder2/../../../page.htm
     * new position
     * http://domain.com/context/newpage.htm
     * @return
     */
    public String getOffsetStringToContextLevel() {
        int offset = getLevel() - 1;
        if (siteContextPrefix != null && level > 0) offset--;
        return getLevelString(offset);
    }

    public boolean isOfflineCreationMode() {
        return (request.getHeader(REQUEST_HEADER_OFFLINE_CREATION_MODE) != null);
    }

    public void parseAdditionalParams() {
        String itemId = this.getRelativePath();
        int dotIndex = itemId.lastIndexOf(".");
        if (dotIndex > -1) {
            extension = itemId.substring(dotIndex + 1);
            itemId = itemId.substring(0, dotIndex);
        }
        int slashIndex = itemId.indexOf("/");
        if (slashIndex > -1) {
            entityName = itemId.substring(0, slashIndex);
            itemId = itemId.substring(slashIndex + 1);
            pageNum = 0;
            int pIndex = itemId.lastIndexOf(siteSettings.getListPageDelimiter());
            if (pIndex > -1) {
                try {
                    pageNum = Integer.parseInt(itemId.substring(pIndex + 3));
                } catch (Exception e) {
                    pageNum = 0;
                }
                itemId = itemId.substring(0, pIndex);
            }
            list = siteSettings.getListPrefix().equals(itemId);
            if (!list) id = HtmlUtils.urlDecode(itemId);
        }
    }

}