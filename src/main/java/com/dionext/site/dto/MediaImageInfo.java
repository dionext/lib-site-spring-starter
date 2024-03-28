package com.dionext.site.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * .mediaifo
 * .mref
 */
public class MediaImageInfo {
    /**
     * Name
     * makes sense when the real names of several files include the dimension
     * for example, on Wikipedia this is the name of the picture page
     * in the photo bank the name of the picture without size prefixes
     * in TMDb, pictures of different sizes have the same name, but are contained in different directories
     * Example: acadia-national-park-maine-2101773
     */
    @JsonProperty("Name")
    private String name;
    /**
     * object identifier for which this image exists in the source storage
     * URL of the image page (if available)
     * for example "Q876281"
     * or image page address
     * example: "https://pixabay.com/photos/acadia-national-park-maine-2101773/"
     */
    @JsonProperty("SrcId")
    private String srcId;
    /**
     * if the picture is in svg format, then we write here, because it has no dimension
     */
    @JsonProperty("SvgSourceUrl")
    private String svgSourceUrl;
    @JsonProperty("SvgStoredFilePath")
    private String svgStoredFilePath;
    @JsonProperty("Width")
    private int width;
    @JsonProperty("Height")
    private int height;
    @JsonProperty("AltList")
    private List<MediaImageAltInfo> altList = new ArrayList<>();
    @JsonProperty("Author")
    private String author;
    /**
     * hyperlink address to the author
     * example "//commons.wikimedia.org/wiki/User:Franzfoto"
     * or
     * full tag hyperlinks to the author's page
     * example "Image by <a href=\"https://pixabay.com/users/lperron-2587970/?utm_source=link-attribution&utm_medium=referral&utm_campaign=image&utm_content=2101773\">lperron</a> from <a href=\ "https://pixabay.com/?utm_source=link-attribution&utm_medium=referral&utm_campaign=image&utm_content=2101773\">Pixabay</a>"
     */
    @JsonProperty("AuthorHref")
    private String authorHref;

    public final String getName() {
        return name;
    }

    public final void setName(String value) {
        name = value;
    }

    public final String getSrcId() {
        return srcId;
    }

    public final void setSrcId(String value) {
        srcId = value;
    }

    public final String getSvgSourceUrl() {
        return svgSourceUrl;
    }

    public final void setSvgSourceUrl(String value) {
        svgSourceUrl = value;
    }

    public final String getSvgStoredFilePath() {
        return svgStoredFilePath;
    }

    public final void setSvgStoredFilePath(String value) {
        svgStoredFilePath = value;
    }

    public final int getWidth() {
        return width;
    }

    public final void setWidth(int value) {
        width = value;
    }

    public final int getHeight() {
        return height;
    }

    public final void setHeight(int value) {
        height = value;
    }

    public final String getAuthor() {
        return author;
    }

    public final void setAuthor(String value) {
        author = value;
    }

    public final String getAuthorHref() {
        return authorHref;
    }

    public final void setAuthorHref(String value) {
        authorHref = value;
    }

    public List<MediaImageAltInfo> getAltList() {
        return altList;
    }

    public void setAltList(List<MediaImageAltInfo> altList) {
        this.altList = altList;
    }
}
