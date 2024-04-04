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
    private String lang;
    private Locale locale = Locale.ENGLISH;
    private int level; //page level
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDefaultLang() {
        return siteSettings.getSiteLangs().length > 0 ? siteSettings.getSiteLangs()[0] : null;
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
        return pageTitle != null ? pageTitle : siteSettings.getSiteTitle().getLabel(i18n);
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getKeywords() {
        StringBuilder str = new StringBuilder();
        if (keywords != null) str.append(keywords);
        String siteKeywords = siteSettings.getKeywords().getLabel(i18n);
        if (!isOfflineCreationMode() && siteKeywords != null) {
            if (str.length() > 0) str.append(", ");
            str.append(siteKeywords);
        }
        return str.length() > 0 ? str.toString() : null;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        StringBuilder str = new StringBuilder();
        if (description != null) str.append(description);
        String siteDescription = siteSettings.getDescription().getLabel(i18n);
        if (!isOfflineCreationMode() && siteDescription != null) {
            if (str.length() > 0) str.append(". ");
            str.append(siteDescription);
        }
        return str.length() > 0 ? str.toString() : null;
    }

    public void setDescription(String description) {
        this.description = description;
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
    public boolean getRu() {
        return "ru".equals(getLang());
    }

    public String getPageUrl() {
        return getDomainUrl() + "/" + getLang() + "/" + getRelativePath();
    }

    public String getLangUrl() {
        return getDomainUrl() + "/" + getLang();
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

    public String getOffsetStringToNavigationRoot() {
        int offset = getLevel() - 1;
        if (siteContextPrefix != null) offset--;
        if (siteSettings.isSiteLangInPath()) offset--;
        return getLevelString(offset);
    }

    public String getOffsetStringToContextLevel() {
        int offset = getLevel() - 1;
        if (siteContextPrefix != null) offset--;
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