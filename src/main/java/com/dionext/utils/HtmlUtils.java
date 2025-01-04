package com.dionext.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HtmlUtils {
    private HtmlUtils() {
    }

    public static String urlDecode(String title) {
        return URLDecoder.decode(title, StandardCharsets.UTF_8);
    }

    /** use this function for encode query params
     * @param queryParam
     * @return
     */
    public static String urlEncode(String queryParam ) {
        return URLEncoder.encode(queryParam, StandardCharsets.UTF_8);
    }

    /** use this function for encode urp path (not for query params after '?' sign)
     * https://www.baeldung.com/java-url-encoding-decoding
     * it returned the encoded value, and + is not encoded because it is a value character in the path component.
     *
     * @param path
     * @return
     */
    public static String urlEncodePath(String path) {
        path = org.springframework.web.util.UriUtils.encodePath(path, StandardCharsets.UTF_8.toString());
        return path;
    }

    public static String prettyPrint(String html) {
        Document doc = Jsoup.parse(html, "", org.jsoup.parser.Parser.xmlParser());
        doc.outputSettings().prettyPrint(true);
        doc.outputSettings().indentAmount(4);
        return doc.html();
    }

}
