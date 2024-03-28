package com.dionext.site.services;

import com.dionext.site.components.PageInfo;
import com.dionext.site.dto.Align;
import com.dionext.site.dto.ImageDrawInfo;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.site.properties.NavItem;
import com.dionext.utils.services.I18nService;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@SuppressWarnings({"java:S1192", "java:S5663", "java:S3776", "java:S135"})
@RequiredArgsConstructor
public class PageCreatorService {

    protected PageInfo pageInfo;
    protected ResourceService resourceService;
    protected I18nService i18n;
    PageParserService pageParserService;

    @Autowired
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Autowired
    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Autowired
    public void setPageParserService(PageParserService pageParserService) {
        this.pageParserService = pageParserService;
    }

    @Autowired
    public void setI18n(I18nService i18n) {
        this.i18n = i18n;
    }

    public String createHeadBottom() {
        return MessageFormat.format("""
                        <link href="{0}" rel="stylesheet"/>""",
                pageInfo.getOffsetStringToImageLevel() + "images/main.css");

    }

    public String createBodyScripts() {
        return null;
    }



    protected String dfs(String value) {
        return StringUtils.defaultString(value);
    }

    public String createHtmlAll(SrcPageContent srcPageContent) {

        if (!Strings.isNullOrEmpty(srcPageContent.getTitle())) pageInfo.setPageTitle(srcPageContent.getTitle());
        if (!Strings.isNullOrEmpty(srcPageContent.getKeywords())) pageInfo.setKeywords(srcPageContent.getKeywords());
        if (!Strings.isNullOrEmpty(srcPageContent.getDescription()))
            pageInfo.setDescription(srcPageContent.getDescription());

        StringBuilder html = new StringBuilder();
        html.append(createHtmlBeginTag());
        html.append("<head>");
        html.append(dfs(createHeadContentType()));
        html.append(dfs(createHeadTitle()));
        if (!pageInfo.isOfflineCreationMode()) {
            html.append(dfs(createHeadLocaleLinks()));
        }
        if (!pageInfo.isOfflineCreationMode()) {
            html.append(dfs(createHeadMetaDescription()));
        } else html.append(dfs(copyHeadMeta(srcPageContent)));
        if (!pageInfo.isOfflineCreationMode()) {
            html.append(dfs(createHeadMetaForIcons()));
            html.append(dfs(createHeadMetaForSocialMedia()));
            html.append(dfs(createHeadBootstrap()));
            html.append(dfs(createHeadBottom()));
        }
        html.append("</head>");
        html.append("<body>");
        if (!pageInfo.isOfflineCreationMode()) {
            html.append(dfs(createBodyTop()));
        }
        if (!pageInfo.isOfflineCreationMode()) {
            html.append(dfs(createBodyMainSection(srcPageContent.getBody())));
        } else html.append(dfs(srcPageContent.getBody()));
        if (!pageInfo.isOfflineCreationMode()) {
            html.append(dfs(createBodyBottomHtml()));
            html.append(dfs(createBodyBootstrap()));
            html.append(dfs(createBodyScripts()));
            html.append(dfs(createBodySearchEngineScripts()));
        }
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }


    public String createBodyTopBanner() {

        return MessageFormat.format("""
                <div class="page-header">
                   <h1 class="text-primary"><span class="font-italic">{0}</span></h1>
                </div>""", pageInfo.getSiteTitle());
    }

    public String createBodyTop() {

        return """
                <div class="container">""" +
                createBodyTopBanner() +
                createBodyTopMenu() +
                "</div>";
    }

    public String createBodyTopMenuStyle() {
        return """
                <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
                """;//<nav class="navbar navbar-expand-lg navbar-light bg-light">
    }

    public String createBodyTopMenuHierLevel(List<NavItem> items, int level) {
        StringBuilder str = new StringBuilder();
        if (items != null) {
            for (NavItem item : items) {
                if (item.getSubitems() != null && !item.getSubitems().isEmpty()) {
                    str.append("""            
                            <li class="nav-item dropdown">""");
                    str.append("""            
                            <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#" role="button" aria-expanded="false">""");
                    str.append(item.getLabel(i18n));
                    str.append("</a>");
                } else {
                    if (level == 0) {
                        str.append("""            
                                <li class="nav-item active">""");
                    } else {
                        str.append("""            
                                <li>""");
                    }
                    if (item.getUrl() != null) {
                        str.append(MessageFormat.format("""
                                        <a class="{0}" href="{1}">{2}</a>""",
                                ((level == 0) ? "nav-link" : "dropdown-item"),
                                (pageInfo.getOffsetStringToNavigationRoot() + item.getUrl()),
                                item.getLabel(i18n)
                        ));
                    } else {
                        str.append(item.getLabel(i18n));
                    }
                }

                if (item.getSubitems() != null && !item.getSubitems().isEmpty()) {
                    str.append("""            
                                <ul class="dropdown-menu">
                            """);
                    str.append(createBodyTopMenuHierLevel(item.getSubitems(), level + 1));
                    str.append("""            
                                </ul>
                            """);
                }
                str.append("</li>");
            }
        }
        return str.toString();
    }

    public String createBodyTopMenu() {
        //https://getbootstrap.com/docs/5.0/components/navbar/
        StringBuilder str = new StringBuilder();

        str.append("""
                <section id="topmenu">""");
        str.append("""
                <div>""");
        // Note: using  <div class="container-fluid"> give unexpected x gutter
        //About gutters see  https://getbootstrap.com/docs/5.0/layout/gutters/
        str.append(createBodyTopMenuStyle());//nav
        str.append("""
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbar_d" aria-controls="navbar_d" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                """);
        //left
        str.append("""
                <div class="collapse navbar-collapse" id="navbar_d">
                """);//<div class="collapse navbar-collapse" id="navbarSupportedContent">
        if (pageInfo.getSiteSettings().getNav() != null) {
            str.append("""            
                       <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    """);
            //                       <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            str.append(createBodyTopMenuHierLevel(pageInfo.getSiteSettings().getNav(), 0));
            str.append("""            
                        </ul>
                    """);
        }

        str.append("</div>");

        //right
        str.append("""
                <div class="collapse navbar-collapse justify-content-md-end" id="navbar">""");
        str.append("""
                <ul class="navbar-nav">""");
        if (pageInfo.getPageLangs().length > 1 && pageInfo.getRu()
                && pageInfo.isAnotherLangPageExist()) {
            str.append(createBodyTopMenuLangSelector());
        }
        str.append("</ul>");
        str.append("</div>");
        str.append("</nav>");
        str.append("</div>");
        str.append("</section>");
        return str.toString();
    }


    public String createBodyBottomInformation() {
        return null;
    }

    public String createBodyBottomHtml() {
        return MessageFormat.format("""
                <section id="footer">
                    <div class="container">
                        <footer class="pt-4 my-md-5 pt-md-5 border-top">
                            <div class="row">
                                <div class="col-12 col-md">
                                {0}
                                </div>
                                {1}
                            </div>
                        </footer>
                    </div>
                </section>""", dfs(createBodyBottomInformation()), dfs(createBodyBottomMenu()));
    }

    public String createBodyBottomMenu() {
        StringBuilder str = new StringBuilder();
        boolean first = true;
        for (var g : pageInfo.getSiteSettings().getNav()) {
            if (!first && (g.getSubitems() == null || g.getSubitems().isEmpty())) {
                //in the bottom menu (since it is always open) we do not show groups without child records if they are already found in top groups
                //find
                boolean found = false;
                for (var g1 : pageInfo.getSiteSettings().getNav()) {
                    if (g.getUrl().equals(g1.getUrl())) {
                        found = true;
                        break;
                    }
                    if (g.getSubitems() != null) {
                        for (var m1 : g.getSubitems()) {
                            if (g.getUrl().equals(m1.getUrl())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
                if (found) {
                    continue;
                }
            }
            str.append("""
                    <div class="col-6 col-md">""");
            str.append("""
                    <h5>""");
            if (g.getUrl() != null) {
                str.append(MessageFormat.format("""
                                <a class="text-muted" href="{0}">{1}</a>""",
                        pageInfo.getOffsetStringToNavigationRoot() + g.getUrl(),
                        g.getLabel(i18n)));
            } else {
                str.append(g.getLabel(i18n));
            }
            str.append("""
                    </h5>""");
            str.append("""
                    <ul class="list-unstyled text-small">""");
            if (g.getSubitems() != null) {
                for (var m : g.getSubitems()) {
                    str.append(MessageFormat.format("""
                                    <li><a class="text-muted" href="{0}">{1}</a></li>""",
                            pageInfo.getOffsetStringToNavigationRoot() + m.getUrl(),
                            m.getLabel(i18n)));
                }
            }
            str.append("""
                    </ul>""");
            str.append("""
                    </div>""");
            first = false;
        }
        return str.toString();
    }

    public String createHeadTitle() {
        return MessageFormat.format("<title>{0}</title>", pageInfo.getPageTitle());
    }

    public String createBodyMainSection(String pageBody) {
        return MessageFormat.format("""
                <section id="main">
                    <div class="container">
                    {0}
                    </div>
                </section>""", pageBody);
    }

    public String createHeadLocaleLinks() {
        String relUri = pageInfo.getRelativePath();
        StringBuilder str = new StringBuilder();
        if (pageInfo.getPageLangs().length > 1) {
            for (String lang : pageInfo.getPageLangs()) {
                str.append(MessageFormat.format("""
                                <link rel="alternate" hreflang="{0}" href="{1}" />""",
                        lang, pageInfo.getDomainUrl() + "/" + lang + "/" + relUri));
            }
        }

        String lang = pageInfo.getDefaultLang();
        if (lang != null) {
            str.append(MessageFormat.format("""
                            <link rel="alternate" hreflang="x-default" href="{0}" />""",
                    pageInfo.getDomainUrl() + "/" + (pageInfo.isSiteLangInPath() ? (lang + "/") : "") + relUri));
        }
        return str.toString();
    }

    public String createHeadContentType() {
        return MessageFormat.format("""
                        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                        <meta charset="utf-8"/>
                        <meta http-equiv="Content-Language" content="{0}"/>""",
                pageInfo.getLang());

    }

    public String createHeadMetaForIcons() {
        //https://stackoverflow.com/questions/4014823/does-a-favicon-have-to-be-32x32-or-16x16
        //https://realfavicongenerator.net/
        if (pageInfo.getSiteSettings().isCreateMetaForIcons()) {
            return MessageFormat.format("""
                    <link rel="apple-touch-icon" sizes="180x180" href="{0}/apple-touch-icon.png"/>
                    <link rel="icon" type="image/png" sizes="32x32" href="{0}/favicon-32x32.png"/>
                    <link rel="icon" type="image/png" sizes="16x16" href="{0}/favicon-16x16.png"/>
                    <link rel="manifest" href="{0}/site.webmanifest"/>
                    <link rel="mask-icon" href="{0}/safari-pinned-tab.svg" color="#5bbad5"/>
                    <meta name="msapplication-TileColor" content="#da532c"/>
                    <meta name="theme-color" content="#ffffff"/>
                    <link rel="shortcut icon" href="{0}/favicon.ico"/>
                    <meta name="msapplication-config" content="{0}/browserconfig.xml"/>
                    """, pageInfo.getOffsetStringToImageLevel() + "images");
        } else return null;
    }

    public String createHeadMetaDescription() {
        StringBuilder str = new StringBuilder();

        if (!Strings.isNullOrEmpty(pageInfo.getDescription())) {
            str.append(MessageFormat.format("""
                    <meta name="description" content="{0}"/>""", pageInfo.getDescription()));
        }
        if (!Strings.isNullOrEmpty(pageInfo.getKeywords())) {
            str.append(MessageFormat.format("""
                    <meta name="keywords" content="{0}"/>""", pageInfo.getKeywords()));
        }
        return str.toString();
    }

    private String copyHeadMeta(SrcPageContent srcPageContent) {

        return MessageFormat.format("""
                <meta name="description" content="{0}"/>""", dfs(srcPageContent.getDescription())) +
                MessageFormat.format("""
                        <meta name="keywords" content="{0}"/>""", dfs(srcPageContent.getKeywords()));
    }

    public String createHeadMetaForSocialMedia() {
        StringBuilder str = new StringBuilder();
        if (pageInfo.getSiteSettings().isCreateMetaOg()) {
            str.append(MessageFormat.format("""
                    <meta property="og:title" content="{0}"/>""", pageInfo.getPageTitle()));
            str.append(MessageFormat.format("""
                    <meta property="og:description" content="{0}"/>""", pageInfo.getDescription()));
            if (pageInfo.getPageImage() != null) {
                if (!pageInfo.getPageImage().startsWith("http")) {
                    pageInfo.setPageImage(pageInfo.getDomainUrl() + "/" + pageInfo.getPageImage());
                }
                //https://www.h3xed.com/web-and-internet/how-to-use-og-image-meta-tag-facebook-reddit
                //In short, the best size to make your image is 1200 x 1200 or larger with a square aspect ratio. This is due to the different aspect ratios Facebook, Reddit, and other sharing sites use.
                //Facebook: Minimum size in pixels is 600x315
                //Recommended size is 1200x630 - Images this size will get a larger display treatment.
                //Aspect ratio should be 1.91:1
                str.append(MessageFormat.format("""
                        <meta property="og:image" content="{0}"/>""", pageInfo.getPageImage()));
            }
            str.append(MessageFormat.format("""
                    <meta property="og:url" content="{0}"/>""", pageInfo.getPageUrl()));
            str.append(MessageFormat.format("""
                    <meta property="og:type" content="{0}"/>""", "article"));
            str.append(MessageFormat.format("""
                    <meta property="og:site_name" content="{0}"/>""", pageInfo.getPageTitle()));
            str.append(MessageFormat.format("""
                    <meta property="og:locale" content="{0}"/>""", pageInfo.getLang()));
        }
        return str.toString();
    }


    //to do multi lang
    public String createBodyTopMenuLangSelector() {
        boolean ru = "ru".equals(pageInfo.getLang());

        String enLink = """
                <span class="flag-icon flag-icon-gb"> </span> En""";
        String ruLink = """
                <span class="flag-icon flag-icon-ru"> </span> Ru""";

        return """
                <li class="nav-item dropdown active">""" +
                """
                        <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#" role="button" aria-expanded="false">
                        """ +
                (ru ? ruLink : enLink) +
                "</a>" +
                """
                        <ul class="dropdown-menu">""" +
                """
                        <li><a class="dropdown-item" href=\"""" +
                pageInfo.getOffsetStringToLangLevel() +
                (!ru ? "ru/" : "en/") +
                pageInfo.getRelativePath() +
                """
                        ">""" +
                (!ru ? ruLink : enLink) +
                "</a><li>" +
                """
                        </li>""";
    }


    public String createImage(String imagePath, int width, int height, String title, String href, boolean blank) {
        ImageDrawInfo tempVar = new ImageDrawInfo();
        tempVar.setImagePath(imagePath);
        tempVar.setWidth(width);
        tempVar.setHeight(height);
        tempVar.setTitle(title);
        tempVar.setHref(href);
        tempVar.setBlank(blank);
        return createImage(tempVar);
    }

    public String createImage(ImageDrawInfo imageDrawInfo) {
        if (imageDrawInfo == null) return "";
        StringBuilder str = new StringBuilder();
        if (imageDrawInfo.isInDiv()) {
            str.append("""
                    <div""");
            if (imageDrawInfo.getAlign() == Align.RIGHT || imageDrawInfo.getAlign() == Align.CENTER) {
                str.append("""
                         class="
                        """.stripTrailing());
                str.append(imageDrawInfo.getAlign() == Align.RIGHT ? "float-right" : "center-block"); // text-center
                str.append("""
                        \"""");
            }
            str.append("""
                    >""");
        }


        if (imageDrawInfo.getHref() != null) {
            str.append("""
                    <a""");
            if (imageDrawInfo.getTitle() != null) {
                str.append("""
                         title="
                        """.stripTrailing());

                str.append(imageDrawInfo.getTitle());
                str.append("""
                        \"""");
            }
            if (imageDrawInfo.isBlank()) {
                str.append("""
                         rel="nofollow" target="_blank"
                        """.stripTrailing());
                if (!imageDrawInfo.isNoExt()) {
                    str.append("""
                             class="external"
                            """.stripTrailing());
                }
            }
            str.append("""
                     href="
                    """.stripTrailing());
            if (!imageDrawInfo.getHref().startsWith("http")
                    && imageDrawInfo.getHref().startsWith("images")) {
                str.append(pageInfo.getOffsetStringToImageLevel());
            }
            str.append(imageDrawInfo.getHref());
            str.append("""
                    ">""");
        }
        str.append("""
                <img class=\"""");
        if (imageDrawInfo.isTumb()) {
            str.append("""
                    img-thumbnail""");
        } else if (imageDrawInfo.isImgCard()) {
            str.append("""
                    card-img-top""");
        }
        if (imageDrawInfo.getAddClass() != null) {
            str.append(" " + imageDrawInfo.getAddClass());
        }
        str.append("""
                \"""");
        if (imageDrawInfo.getStyle() != null) {
            str.append("""
                     style="
                    """.stripTrailing());
            str.append(imageDrawInfo.getStyle());
            str.append("""
                    \"""");
        }
        str.append("""
                 src="
                """.stripTrailing());
        if (!imageDrawInfo.getImagePath().startsWith("http")) {
            str.append(pageInfo.getOffsetStringToImageLevel());
        }
        str.append(imageDrawInfo.getImagePath());
        str.append("""
                \"""");
        if (imageDrawInfo.getWidth() > 0) {
            str.append("""
                     width="
                    """.stripTrailing());
            str.append(imageDrawInfo.getWidth());
            str.append("""
                    \"""");
        }
        if (imageDrawInfo.getHeight() > 0) {
            str.append("""
                     height="
                    """.stripTrailing());
            str.append(imageDrawInfo.getHeight());
            str.append("""
                    \"""");
        }

        str.append("""
                >""");
        if (imageDrawInfo.getHref() != null) {
            str.append("""
                    </a>""");
        }
        if (imageDrawInfo.isInDiv()) {
            str.append("""
                    </div>""");
        }
        return str.toString();
    }

    public String createPagination(PageInfo pageInfo, int allPagesCount) {
        return createPagination(pageInfo.getSiteSettings().getListPrefix(), pageInfo.getSiteSettings().getListPageDelimiter(), pageInfo.getExtension(), pageInfo.getPageNum(), allPagesCount);
    }

    public String createPagination(String pageName, String prefix, String ext, int curPageNum, int allPagesCount) {

        int maxVisible = 8;
        int start = curPageNum - (maxVisible / 2);
        if (start < 0) {
            start = 0;
        }
        int end = allPagesCount - 1;
        if (start + maxVisible < allPagesCount - 1) {
            end = curPageNum + (maxVisible / 2);
            if (end >= allPagesCount) {
                end = allPagesCount - 1;
                if (end - maxVisible < 0) {
                    start = 0;
                }
            }
        }


        StringBuilder str = new StringBuilder();
        str.append("""
                <div class="container">""");

        str.append("""
                <ul class="pagination justify-content-center">""");

        //prev
        str.append(createPaginationElement("""
                <span aria-hidden="true">&laquo;</span><span class="sr-only">Previous</span>""", pageName + ((curPageNum != 1) ? (prefix + (curPageNum - 1)) : "")
                + (ext != null ? "." + ext : ""), false, (curPageNum == 0)));
        //first
        if (start > 0) {
            str.append(createPaginationElement("1", pageName + (ext != null ? "." + ext : ""), (curPageNum == 0), false));
            //...
            str.append(createPaginationElement("...", "#", false, true));
            str.append(createPaginationElement("...", "#", false, true));
        }
        for (int i = start; i <= end; i++) {
            str.append(createPaginationElement(String.valueOf(i), pageName + ((i != 0) ? (prefix + i) : "") + (ext != null ? "." + ext : ""), (i == curPageNum), false));

        }
        if (end < (allPagesCount - 1)) {
            //...
            str.append(createPaginationElement("...", "#", false, true));
            str.append(createPaginationElement("...", "#", false, true));
            //last
            str.append(createPaginationElement(String.valueOf(allPagesCount), pageName + (prefix + (allPagesCount - 1)) + (ext != null ? "." + ext : ""), false, (curPageNum >= (allPagesCount - 1))));
        }
        //next
        str.append(createPaginationElement("""
                <span aria-hidden="true">&raquo;</span><span class="sr-only">Next</span>""", pageName + (prefix + (curPageNum + 1) + (ext != null ? "." + ext : "")), false, ((curPageNum + 1) > (allPagesCount - 1))));

        str.append("""
                </ul>""");
        str.append("</div>");
        return str.toString();
    }

    public String createPaginationElement(String text, String href, boolean active, boolean disable) {
        StringBuilder str = new StringBuilder();
        str.append("""
                <li class="page-item""");
        if (active) {
            str.append(" active");
        }
        if (disable) {
            str.append(" disabled");
        }
        str.append("""
                ">""");
        str.append("""
                <a class="page-link" href=\"""");
        str.append(href);
        str.append("""
                \"""");
        str.append("""
                 aria-label="
                """.stripTrailing());
        str.append("""
                ">""");
        str.append(text);
        str.append("""
                </a>""");
        str.append("""
                </li>""");
        return str.toString();
    }

    public String generateBlockAttributeLine(String name, String value) {
        if (Strings.isNullOrEmpty(value)) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append("""
                <li class="">"""); //list-group-item
        if (!Strings.isNullOrEmpty(name)) {
            str.append(name);
            str.append("""
                    : """);
            if (!name.endsWith(" "))
                str.append(" ");
        }
        str.append(value);
        str.append("</li>");
        return str.toString();
    }

    public String createExtLink(String href, String prefix, String text, String title) {
        if (Strings.isNullOrEmpty(href)) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append("""
                <a""");
        if (title != null) {
            str.append("""
                     title="
                    """.stripTrailing());
            str.append(title);
            str.append("""
                    \"""");
        }
        str.append("""
                 target="_blank" rel="nofollow" class="external" href="
                """.stripTrailing());
        if (prefix != null) {
            str.append(prefix);
        }
        str.append(href);//to do
        str.append("""
                ">""");
        str.append(text);
        str.append("</a>");
        return str.toString();
    }

    public String createHtmlBeginTag() {
        return """
                <html xmlns="http://www.w3.org/1999/xhtml" xmlns:og="http://ogp.me/ns#" lang="   """
                + pageInfo.getLang()
                + """
                ">
                """;
    }

    public String createHeadBootstrap() {
        return """
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous"/>
                """;
    }

    public String createBodyBootstrap() {
        return """
                <!-- Optional JavaScript -->
                <!-- jQuery first (! not slim), then Bootstrap JS (Popper.js included in bootstrap.bundle.min.js)-->
                <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
                <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js" integrity="sha256-lSjKY0/srUM9BE3dPm+c4fBo1dky2v27Gdjm2uoZaL0=" crossorigin="anonymous"></script>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
                """;

    }

    public String createBodySearchEngineScripts() {
        if (!Strings.isNullOrEmpty(pageInfo.getSiteSettings().getGoogleTagID())) {
            return MessageFormat.format("""
                    <!-- Google tag (gtag.js) -->
                    <script async src="https://www.googletagmanager.com/gtag/js?id={0}"></script>

                      gtag('config', '{0}');
                    </script>
                    """, pageInfo.getSiteSettings().getGoogleTagID())
                    + """
                            <script>
                              window.dataLayer = window.dataLayer || [];
                              function gtag(){dataLayer.push(arguments);}
                              gtag('js', new Date());
                    """
                    + MessageFormat.format("""
                      gtag('config', '{0}');
                    </script>
                    """, pageInfo.getSiteSettings().getGoogleTagID());
        } else return "";
    }

}
