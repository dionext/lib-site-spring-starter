package com.dionext.site.controllers;

import com.dionext.site.components.PageInfo;
import com.dionext.site.services.PageCreatorService;
import com.dionext.site.services.PageParserService;
import com.dionext.utils.HtmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

//@Slf4j
public class BaseSiteController {


    protected PageInfo pageInfo;

    @Autowired
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    protected String createSimpleSitePage(PageParserService pageParserService, PageCreatorService pageCreatorService) {
        String relPath = pageInfo.getRelativePath();
        return pageCreatorService.createHtmlAll(pageParserService.getSimpleSitePageSource(relPath));
    }

    protected ResponseEntity<String> sendOk(String data) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + "; charset=utf-8");
        return new ResponseEntity<>(HtmlUtils.prettyPrint(data), responseHeaders, HttpStatus.OK);
    }
    protected ResponseEntity<String> sendStringRestOk(String data) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + "; charset=utf-8");
        return new ResponseEntity<>(data, responseHeaders, HttpStatus.OK);
    }
    protected ResponseEntity<String> sendFragment(String data) {
        return sendFragment(data, null);
    }
    protected ResponseEntity<String> sendFragment(String data, HttpHeaders responseHeaders) {
        if (responseHeaders == null) responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + "; charset=utf-8");
        return new ResponseEntity<>(data, responseHeaders, HttpStatus.OK);
    }

}
