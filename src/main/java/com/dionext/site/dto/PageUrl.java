package com.dionext.site.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PageUrl {
    public PageUrl(){

    }
    public PageUrl(String relativePath){
        this.relativePath = relativePath;
    }
    public PageUrl(String relativePath, String[] langs){
        this.relativePath = relativePath;
        if (langs != null) {
            for (String lang : langs) {
                altUrls.add(new PageUrlAlt(lang));
            }
        }
    }
    private String relativePath;
    List<PageUrlAlt> altUrls = new ArrayList<>();

}
