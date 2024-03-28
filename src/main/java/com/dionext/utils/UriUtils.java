package com.dionext.utils;

import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {
    private UriUtils() {
    }

    public static boolean isAbsoluteUrl(String href) {
        final URI u;
        try {
            u = new URI(href);
        } catch (URISyntaxException e) {
            return false;
        }
        return u.isAbsolute();
    }

    //to do
    public static String replaceLastPathofUrl(String mediaUri, String newMediaUri) {
        int occurance = StringUtils.countOccurrencesOf(newMediaUri, "/");
        while (occurance > -1) {
            int i = mediaUri.lastIndexOf('/');
            if (i < 0) {
                mediaUri = "";
                break;
            }
            mediaUri = mediaUri.substring(0, i);
            occurance--;
        }
        return mediaUri + "/" + newMediaUri;
    }
}
