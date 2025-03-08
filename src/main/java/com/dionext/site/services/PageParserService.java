package com.dionext.site.services;

import com.dionext.site.components.PageInfo;
import com.dionext.site.dto.MediaImageInfo;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.utils.HtmlUtils;
import com.dionext.utils.UriUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@Slf4j
@SuppressWarnings({"java:S6541", "java:S5663", "java:S3776", "java:S1192"})
public class PageParserService {

    PageInfo pageInfo;
    ResourceService resourceService;

    public SrcPageContent getSimpleSitePageSource(String relPath) {
        if (relPath.indexOf(".") < 0) relPath = relPath + ".htm";
        String src = resourceService.findI18nResourceAsString(pageInfo.getSiteStoragePaths(),
                pageInfo.getSiteSettings().isSiteLangInPath() ? pageInfo.getPathLang() : null,
                pageInfo.getSiteSettings().isSiteLangInPath() ? pageInfo.getDefaultLang() : null,
                relPath);
        return processPage(src);
    }


    protected void makeTableOfContents(Element body) {
        //to do
        if (body.text().contains("[««]") || body.text().contains("[»»]")) return;//for old paginated pages

        int hCount = 0;

        StringBuilder tbc = new StringBuilder();
        tbc.append("\n\r");
        tbc.append("""
                <ul  class="list-unstyled" id="TOC">""");

        //count stage
        HashSet<String> ids = new HashSet<>();
        var xpath = "//*[self::h2 or self::h3 or self::h4 or self::h5 or self::h6]";
        Elements headers = body.selectXpath(xpath);
        if (headers != null) {
            for (var header : headers) {
                Element a = header.selectXpath(".//a[@name]").first();
                if (a != null) {
                    String hrefName = a.attr("name");
                    if (!Strings.isNullOrEmpty(hrefName)) {
                        ids.add(hrefName);
                    }
                }
            }
        }
        //main stage
        if (headers != null) {
            for (var header : headers) {
                String id = null;
                Element aName = header.selectXpath(".//a[@name]").first();
                if (aName != null) {
                    String hrefName = aName.attr("name");
                    if (!Strings.isNullOrEmpty(hrefName)) {
                        id = hrefName;
                    }
                }
                if (id == null) {
                    for (int i = 0; i < 200000000; i++) {
                        if (!ids.contains("id" + i)) {
                            id = "id" + i;
                            ids.add(id);
                            break;
                        }
                    }
                }
                //find for additional href
                Element aHref = header.selectXpath(".//a[@href]").first();
                String href = null;
                if (aHref != null) {
                    href = aHref.attr("href");
                }
                String he = header.text().strip();
                if (he.length() > 0) {
                    header.html("<a name=\"" + id + "\"></a>" + ((href != null) ? ("<a href=\"" + href + "\">" + he + " </a>") : he));

                    tbc.append("\n\r");
                    tbc.append("<li><a href=\"");
                    tbc.append("#" + id);
                    tbc.append("\">");
                    int level = Integer.parseInt(header.nodeName().substring(1));//to do ????
                    for (int i = 2; i < level; i++) {
                        tbc.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    }
                    tbc.append(he);
                    tbc.append("</a>");
                    if (href != null) {
                        tbc.append("<a href=\"" + href + "\">" + " [>>]" + " </a>");
                    }
                    tbc.append("</li>");
                    hCount++;
                }
            }

            tbc.append("\n\r");
            tbc.append("</ul>");

            //insert table of contents
            if (hCount > 0 && body != null) {
                Element newNode = new Element(Tag.valueOf("div"), "");
                newNode.html(tbc.toString());

                Element ul = body.select("ul[id=TOC]").first();
                if (ul != null) {
                    Element parentDiv = ul.parent();
                    if ("div".equals(parentDiv.nodeName()))
                        parentDiv.replaceWith(newNode);
                    else
                        ul.replaceWith(newNode);
                } else {

                    Element h1Node = body.select("h1").first();
                    if (h1Node == null) {
                        if (body.children().first() != null) {
                            body.children().first().before(newNode);
                        } else {
                            body.appendChild(newNode);
                        }
                    } else {
                        h1Node.after(newNode);
                    }
                }
            }
        } //headers != null
    } //method

    @Autowired
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Autowired
    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public SrcPageContent processPage(String html) {
        Document doc = parsePage(html);
        SrcPageContent srcPageContent = new SrcPageContent();
        if (html != null) {
            srcPageContent.setTitle(getTitleFromPage(doc));
            srcPageContent.setDescription(getMetaDescriptionFromPage(doc));
            srcPageContent.setKeywords(getMetaKeywordsFromPage(doc));
            Element body = getBodyFromPage(doc);
            if (body != null) {
                adjustBody(body);
                String docHtml = body.html();
                srcPageContent.setBody(docHtml);
            }
        }
        return srcPageContent;
    }

    public String getMetaKeywordsFromPage(Document doc) {
        Element head = doc.select("head").first();
        if (head != null) {
            Element meta = head.select("meta[name=keywords]").first();
            if (meta != null) return meta.attr("content");
        }
        return null;
    }

    public String getMetaDescriptionFromPage(Document doc) {
        Element head = doc.select("head").first();
        if (head != null) {
            Element meta = head.select("meta[name=description]").first();
            if (meta != null) return meta.attr("content");
        }
        return null;
    }

    public Element getBodyFromPage(Document doc) {
        Element body = doc.select("body").first();
        if (body != null) {
            Element mainSection = body.select("section[id=main]").first();
            if (mainSection != null) {
                Element mainContainer = mainSection.select("div.container").first();
                if (mainContainer != null) body = mainContainer;
                else body = mainSection;
            }
        }
        return body;
    }

    public String getTitleFromPage(Document doc) {

        String titleStr = "";
        //get title
        Element body = doc.select("body").first();
        if (body != null) {
            Element h1 = body.select("h1").first();
            if (h1 != null) titleStr = h1.text();
            else {
                Element head = doc.select("head").first();
                if (head != null) {
                    Element title = head.select("title").first();
                    if (title != null) titleStr = title.text();
                    if ("Без названия 1".equals(titleStr) || "Untitled 1".equals(titleStr)) titleStr = null; //frontpage
                    if (titleStr != null) titleStr = titleStr.replace('\r', ' ').replace('\n', ' ').trim();
                }
            }
        }
        return titleStr;
    }

    protected Document parsePage(String html) {
        return Jsoup.parse(html, "", org.jsoup.parser.Parser.xmlParser());
    }

    protected void addAttributeValue(Element el, String attributeName, String valueToAdd) {
        String attrValue = el.attr(attributeName);
        if (!Strings.isNullOrEmpty(attrValue)) {
            if (attrValue.indexOf(valueToAdd) < 0) {
                el.attr(attributeName, attrValue + " " + valueToAdd);
            }
        } else
            el.attr(attributeName, valueToAdd);
    }

    protected void adjustBody(Element body) {

        adjustHrefs(body);
        //img
        adjustImages(body);
        //frame
        adjustIframes(body);

        //blockquote
        adjustBlockquotes(body);

        //table of contents
        makeTableOfContents(body);
    }

    protected void adjustBlockquotes(Element body) {
        Elements blockquoteList = body.selectXpath(".//blockquote");
        if (blockquoteList != null) {
            for (var blockquote : blockquoteList) {
                adjustBlockquote(blockquote);
            }
        }
    }

    protected void adjustIframes(Element body) {
        Elements iframeList = body.selectXpath(".//iframe");
        if (iframeList != null) {
            for (var iframe : iframeList) {
                String src = iframe.attr("src");
                if (!Strings.isNullOrEmpty(src)) {
                    adjustIframe(iframe);
                }
            }
        }
    }

    protected void adjustImages(Element body) {
        Elements imgList = body.selectXpath(".//img");
        if (imgList != null) {
            for (var img : imgList) {
                String src = img.attr("src");
                if (!Strings.isNullOrEmpty(src)) {
                    boolean absoluteUrl = UriUtils.isAbsoluteUrl(src);
                    if (!absoluteUrl) {
                        String srcRel = removeLevelString(pageInfo.getLevel(), src);
                        MediaImageInfo info = resourceService.getMediaInfo(pageInfo.getSiteInternalStoragePaths(), srcRel);

                        if (info != null) {
                            if (info.getAuthorHref() != null || info.getAuthor() != null) {
                                String caption = """
                                        <small><a rel="nofollow" target="_blank" class="external" href=\""""
                                        + info.getSrcId()
                                        + "\">" + info.getAuthor() +
                                        "</a></small>";
                                if ( !(
                                        (img.parent() != null && "figure".equals(img.parent().nodeName()))
                                        || (img.parent() != null && img.parent().parent() != null && "figure".equals(img.parent().parent().nodeName()))
                                    )
                                ) {
                                    adjustImageByFigure(img, caption);
                                }
                            }
                            if (pageInfo.getSiteSettings().isUseExternalUrlForImages()) {
                                //replace url to absolute url
                                for (var inf : info.getAltList()) {
                                    if (src.endsWith(inf.getStoredFilePath()) && inf.getSourceUrl() != null) {
                                        img.attr("src", inf.getSourceUrl());
                                        break;
                                    }
                                }
                            } else {
                                String storedPath = info.getAltList().get(info.getAltList().size() - 1).getStoredFilePath();
                                if (!storedPath.equals(srcRel)) {
                                    if ( !(
                                            (img.parent() != null && "a".equals(img.parent().nodeName()))
                                                    || (img.parent() != null && img.parent().parent() != null && "a".equals(img.parent().parent().nodeName()))
                                    )) {
                                        wrapImageByA(img, UriUtils.replaceLastPathofUrl(src, storedPath));
                                    }
                                }
                            }
                        } else {
                            adjustImage(img);
                        }
                    } else {
                        adjustImage(img);
                    }
                }
            }
        }
    }

    protected void adjustHrefs(Element body) {
        Elements aList = body.selectXpath(".//a");
        if (aList != null) {
            for (var a : aList) {
                adjustHref(a);
            }
        }
    }
    protected void adjustHref(Element a) {
        String href = a.attr("href");
        if (!Strings.isNullOrEmpty(href)) {
            try {
                boolean isUri = UriUtils.isAbsoluteUrl(href);
                if (isUri) {
                    //rel=""nofollow""
                    a.attr("rel", "nofollow");
                    a.attr("target", "_blank");
                    addAttributeValue(a, "class", "external");
                    if (!Strings.isNullOrEmpty(a.text()) && a.text().contains("%")) {
                        String decIn = HtmlUtils.urlDecode(a.text().replace("\r", "").replace("\n", "").replace("\t", ""));
                        String decHref = HtmlUtils.urlDecode(href);
                        if (decIn.equals(decHref)) {
                            a.text(decIn);
                        }
                    }
                }
            } catch (RuntimeException ex) {
                log.error("Error parsing uri: " + href + " " + ex);
            }
        }

    }

    protected String removeLevelString(int level, String path) {
        for (int i = 0; i < level; i++) {
            if (path.startsWith("../")) {
                path = path.substring(3);
            }
        }
        return path;
    }

    protected void adjustImage(Element img) {
        String style = img.attr("style");
        String cl = img.attr("class");
        if (style.contains("float: left") && !cl.contains("float-left")) {
            cl = cl + " float-left";
            style = style.replace("float: left", "");
        }
        if (style.contains("float: right") && !cl.contains("float-right")) {
            cl = cl + " float-right";
            style = style.replace("float: right", "");
        }
        if (!cl.contains("img-fluid")) {
            cl = cl + " img-fluid";
        }
        if (!cl.contains("rounded")) {
            cl = cl + " rounded";
        }
        img.attr("class", cl);
        img.attr("style", style);
    }

    protected void adjustIframe(Element iframe) {

        if (!Strings.isNullOrEmpty(iframe.attr("allowfullscreen"))) {
            String cl = iframe.attr("class");
            if (!cl.contains("embed-responsive-item")) {
                cl = cl + " embed-responsive-item";
            }

            iframe.attr("class", cl);

            Element div = new Element(Tag.valueOf("div"), "");

            Element newElement;
            div.attr("class", "embed-responsive embed-responsive-16by9");
            //halfSize
            Element container = new Element(Tag.valueOf("div"), "");
            container.attr("class", "container");
            Element row = new Element(Tag.valueOf("row"), "");
            row.attr("class", "row justify-content-center");
            container.appendChild(row);
            div.attr("class", "col-sm-6 " + div.attr("class", ""));
            row.appendChild(div);
            newElement = container;
            iframe.replaceWith(newElement);
            div.appendChild(iframe);
        }
    }

    protected void adjustImageByFigure(Element img, String caption) {
        Element div = new Element(Tag.valueOf("div"), "");

        Element figure = new Element(Tag.valueOf("figure"), "");
        figure.attr("class", "figure");

        img.replaceWith(div);
        div.appendChild(figure);
        figure.appendChild(img);//to do
        if (caption != null) {
            Element figcaption = new Element(Tag.valueOf("figcaption"), "");
            figcaption.attr("class", "figcaption"); // "figcaption text-right"
            figcaption.html(caption);
            figure.appendChild(figcaption);
        }
        String style = img.attr("style");
        String cl = img.attr("class");
        if (style.contains("float: left") && !cl.contains("float-left")) {
            cl = cl + " float-left";
            style = style.replace("float: left", "");

            div.attr("class", "float-left");
        }
        if (style.contains("float: right") && !cl.contains("float-right")) {
            cl = cl + " float-right";
            style = style.replace("float: right", "");

            div.attr("class", "float-right");
        }
        if (!cl.contains("img-fluid")) {
            cl = cl + " img-fluid";
        }
        if (!cl.contains("rounded")) {
            cl = cl + " rounded";
        }
        if (!cl.contains("figure-img")) {
            cl = cl + " figure-img"; //!!!
        }

        img.attr("class", cl);
        img.attr("style", style);
    }

    protected void wrapImageByA(Element img, String href) {
        Element a = new Element(Tag.valueOf("a"), "");
        img.replaceWith(a);
        a.appendChild(img);
        a.attr("href", href);
    }

    protected void adjustBlockquote(Element blockquote) {

        if (!"figure".equals(blockquote.parent().nodeName()) ) {
            Element figure = new Element(Tag.valueOf("figure"), "");
            figure.attr("class", "bg-light p-3");
            Element blockquoteNew = new Element(Tag.valueOf("blockquote"), "");
            blockquoteNew.attr("class", "blockquote");
            figure.appendChild(blockquoteNew);

            String html = blockquote.html();
            int i = html.lastIndexOf("<br");
            if (i > -1) {
                int j = html.indexOf(">", i);
                if (j > -1 && j < html.length() - 1) {
                    String text = html.substring(0, i);
                    String ref = html.substring(j + 1);

                    blockquoteNew.html(text);
                    Element footer = new Element(Tag.valueOf("figcaption"), "");
                    footer.attr("class", "blockquote-footer");
                    Element cite = new Element(Tag.valueOf("cite"), "");
                    footer.appendChild(cite);
                    cite.html(ref);
                    figure.appendChild(footer);
                }
                else blockquoteNew.html(html);
            }
            else blockquoteNew.html(html);

            blockquote.replaceWith(figure);
        }
    }

}
