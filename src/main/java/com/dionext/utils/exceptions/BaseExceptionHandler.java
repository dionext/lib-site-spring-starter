package com.dionext.utils.exceptions;

import com.dionext.site.components.PageInfo;
import com.dionext.site.dto.SrcPageContent;
import com.dionext.site.services.PageCreatorService;
import com.dionext.utils.services.I18nService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

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
            String body = makeErrorPageBody(message, errorMessage, status);
            return new ResponseEntity<>(pageCreatorService.createHtmlAll(new SrcPageContent(body)), status);
        } catch (Exception ex1) {
            if (status == HttpStatus.NOT_FOUND)
                return new ResponseEntity<>(makeSimpleErrorPageBody("Page or resource not found", ex.toString(), ex1.toString()), status);
            else
                return new ResponseEntity<>(makeSimpleErrorPageBody("Internal Server Error", ex.toString(), ex1.toString()), status);
        }
    }

    protected String makeSimpleErrorPageBody(String message, String errorMessage, String errorMessage1) {
        return MessageFormat.format("""
                        <html>
                            <head>
                            </head>
                            <body>
                            <h4>{0}</h4>
                            <!-- {1}  -->
                            <!-- {2}  -->
                            </body>
                        </html>
                        """,
                message, errorMessage, errorMessage1);
    }

    protected String getImageForPageNotFoundStatus() {
        return "images/page-not-found.png";
    }

    protected String getImageForInternalServerErrorStatus() {
        return "images/internal-server-error.png";
    }

    protected String makeErrorPageBody(String message, String errorMessage, HttpStatusCode status) {
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
                        </div>
                        </div>
                        """,
                pageInfo.getOffsetStringToContextLevel() + image, message, errorMessage);
    }

}
