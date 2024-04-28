package com.dionext.site.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageUrlAlt {
    public PageUrlAlt(){

    }
    public PageUrlAlt(String lang){
        this.lang = lang;
    }
    private String lang;
}
