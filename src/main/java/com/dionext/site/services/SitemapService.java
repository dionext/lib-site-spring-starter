package com.dionext.site.services;

import com.dionext.site.dto.PageUrl;
import com.dionext.site.dto.PageUrlAlt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
public class SitemapService {


    public String createSitemap(List<PageUrl> pages, PageCreatorService pageCreatorService,
                                boolean langSupport) {
        StringBuilder str = new StringBuilder();
        if (langSupport)
            str.append("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
                      xmlns:xhtml="http://www.w3.org/1999/xhtml">
                    """);
        else
            str.append("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                    """);
        for(PageUrl pageUrl : pages) {
            str.append("""
                    <url>
                        <loc>""");
            str.append(pageCreatorService.makeFullPageAddressForDefaultLang(pageUrl));
            str.append("""
                    </loc>
                    """);
            if (langSupport) {
                for (PageUrlAlt pageUrlAlt : pageUrl.getAltUrls()) {
                    str.append(
                            MessageFormat.format("""
                                            <xhtml:link rel="alternate" hreflang="{0}" href="{1}" />""",
                                    pageUrlAlt.getLang(),
                                    pageCreatorService.makeFullPageAddress(pageUrl, pageUrlAlt))
                    );
                }
            }
            str.append("""
                    </url>
                    """);
        }
        str.append("""
                    </urlset>
                    """);
        return str.toString();
    }

}
