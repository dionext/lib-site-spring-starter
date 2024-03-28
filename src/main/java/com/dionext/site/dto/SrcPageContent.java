package com.dionext.site.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SrcPageContent {
    private String body;
    private String title;
    private String description;
    private String keywords;

    public SrcPageContent(String body, String title) {
        this.body = body;
        this.title = title;
    }

    public SrcPageContent() {
    }

    public SrcPageContent(String body) {
        this.body = body;
    }

    public SrcPageContent(String body, String title, String description, String keywords) {
        this.body = body;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }
}
