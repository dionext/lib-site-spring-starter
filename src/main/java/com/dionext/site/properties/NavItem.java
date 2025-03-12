package com.dionext.site.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
public class NavItem extends TextItem {
    private String url;
    private List<NavItem> subitems;
    private List<String> roles;
}
