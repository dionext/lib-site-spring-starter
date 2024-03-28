package com.dionext.site.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaImageAltInfo {
    /**
     * original url
     * example https://cdn.pixabay.com/photo/2017/02/27/00/03/acadia-2101773_640.jpg
     */
    @JsonProperty("SourceUrl")
    private String sourceUrl;
    @JsonProperty("StoredFilePath")
    private String storedFilePath;
    @JsonProperty("Width")
    private int width;
    @JsonProperty("Height")
    private int height;
    @JsonProperty("IsMain")
    private boolean isMain;

    public final String getSourceUrl() {
        return sourceUrl;
    }

    public final void setSourceUrl(String value) {
        sourceUrl = value;
    }

    public final String getStoredFilePath() {
        return storedFilePath;
    }

    public final void setStoredFilePath(String value) {
        storedFilePath = value;
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

    public final boolean isMain() {
        return isMain;
    }

    public final void setMain(boolean value) {
        isMain = value;
    }
}

