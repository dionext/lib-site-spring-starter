package com.dionext.site.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
public class SiteSettings {
    private String[] siteLangs;
    private boolean siteLangInPath = true;
    private String siteDomain;// "somedomain.com"
    private String siteProtocol = "https";

    private String siteStaticStoragePath;
    private String[] siteStoragePaths;
    private String[] siteInternalStoragePaths;
    private boolean useExternalUrlForImages;

    private boolean createMetaOg;
    private String pageFaceImage;
    private boolean createMetaForIcons;

    private List<NavItem> nav;

    private TextItem keywords;
    private TextItem description;
    private TextItem siteTitle;

    private String[] apiPrefixes = {
            "api"
    };
    private String[] staticPrefixes = {
            "images"
    };
    //,
    //            "apple-touch-icon.png",
    //            "favicon-32x32.png",
    //            "favicon-16x16.png",
    //            "site.webmanifest",
    //            "favorite.ico"

    private String mainPage = "index.htm";
    private String listPageDelimiter = "_p_";
    private String listPrefix = "list";

    /**
     * A tag ID is an identifier that you put on your page to load a given Google tag.
     * Examples of tag IDs include GT-XXXXXXXXX, G-XXXXXXXXX, and AW-XXXXXXXXX.
     * A single Google tag can have multiple tag IDs.
     * https://support.google.com/tagmanager/answer/12326985?sjid=14390354239745976899-NA
     */
    private String googleTagID;

}
