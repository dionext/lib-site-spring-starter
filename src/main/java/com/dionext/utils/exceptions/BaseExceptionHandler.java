package com.dionext.utils.exceptions;

import com.dionext.site.components.PageInfo;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.site.services.PageCreatorService;
import com.dionext.utils.services.I18nService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

@Slf4j
public class BaseExceptionHandler {

    protected I18nService i18n;

    protected PageInfo pageInfo;

    @Autowired
    public void setI18n(I18nService i18n) {
        this.i18n = i18n;
    }

    @Autowired
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    protected ResponseEntity<Object> processException(Exception ex, HttpServletRequest request, HttpStatusCode status, PageCreatorService pageCreatorService) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + "; charset=utf-8");
        try {
            String contextPath = "";
            if (request != null) {
                contextPath = "Error processing context path: " + request.getRequestURI() + " ";
            }
            String errorMessage = ex.toString() + contextPath;
            String message;
            if (status == HttpStatus.NOT_FOUND) {
                message = i18n.getString("site-spring-starter.page-not-found");
                log.error(errorMessage);
            } else {
                message = i18n.getString("site-spring-starter.internal-server-error");
                log.error(errorMessage, ex);
            }
            if (pageInfo.getSiteSettings() != null) {
                String body = makeErrorPageBody(message, errorMessage, printStackTrace(ex), status);
                return new ResponseEntity<>(pageCreatorService.createHtmlAll(new SrcPageContent(body)), responseHeaders,  status);
            }
            else{
                return new ResponseEntity<>(makeSimpleErrorPageBody(message, ex.toString(),
                        printStackTrace(ex), "", ""), responseHeaders, status);
            }
        } catch (Exception ex1) {
            if (status == HttpStatus.NOT_FOUND)
                return new ResponseEntity<>(makeSimpleErrorPageBody("Page or resource not found", ex.toString(),
                        printStackTrace(ex), ex1.toString(), printStackTrace(ex1)), responseHeaders, status);
            else
                return new ResponseEntity<>(makeSimpleErrorPageBody("Internal Server Error", ex.toString(),
                        printStackTrace(ex), ex1.toString(), printStackTrace(ex1)), responseHeaders, status);
        }
    }
    private String printStackTrace(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    protected String makeSimpleErrorPageBody(String message, String errorMessage, String stackTrace, String errorMessage1, String stackTrace1) {
        return MessageFormat.format("""
                        <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
                            <head>
                                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                                <meta charset="utf-8"/>
                                <meta http-equiv="Content-Language" content="en"/>
                                <title>Error</title>
                            </head>
                            <body>
                            <h3 align="center">{0}</h3>
                            <!-- {1}  -->
                            <!-- {2}  -->
                            <!-- {3}  -->
                            <!-- {4}  -->
                            </body>
                        </html>
                        """,
                message, errorMessage, stackTrace, errorMessage1, stackTrace1);
    }

    protected String getImageForPageNotFoundStatus() {
        return "images/page-not-found.png";
    }

    protected String getImageForInternalServerErrorStatus() {
        return "images/internal-server-error.png";
    }

    protected String makeErrorPageBody(String message, String errorMessage, String stackTrace, HttpStatusCode status) {
        String image = null;
        if (status == HttpStatus.NOT_FOUND)
            image = getImageForPageNotFoundStatus();
        else
            image = getImageForInternalServerErrorStatus();
        return MessageFormat.format("""
                        <div class="container" align="center">
                        <h4>{1}</h4>
                        <a data-bs-toggle="collapse" href="#collapseError" role="button" aria-expanded="false" aria-controls="collapseError">
                        <figure class="figure">
                          <img src="{0}" class="figure-img img-fluid rounded" alt="Error">
                        </figure>
                        </a>
                        <div class="collapse" id="collapseError">
                          <div class="card card-body">
                          {2}
                          </div>
                          <!--{3} -->
                        </div>
                        </div>
                        """,
                pageInfo.getOffsetStringToContextLevel() + image, message, errorMessage, stackTrace);
    }

}
