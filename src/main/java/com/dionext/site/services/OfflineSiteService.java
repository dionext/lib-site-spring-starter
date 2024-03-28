package com.dionext.site.services;

import com.dionext.site.components.PageInfo;
import com.dionext.site.dto.OfflinePage;
import com.dionext.utils.exceptions.DioRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class OfflineSiteService {

    private ResourceLoader resourceLoader;

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<OfflinePage> findAllPages(String[] prefixes, String lang, String defaultLang) {

        Map<String, OfflinePage> filesMap = new HashMap<>();
        for (String prefix : prefixes) {
            findAllPagesForLang(defaultLang, prefix, filesMap);
        }
        for (String prefix : prefixes) {
            findAllPagesForLang(lang, prefix, filesMap);
        }
        return filesMap.values().stream().toList();
    }

    private void findAllPagesForLang(String lang, String prefix, Map<String, OfflinePage> filesMap) {
        Resource resource = resourceLoader.getResource(prefix + "/" + lang);
        try {
            if (resource.exists()) {
                Path rootPath = resource.getFile().toPath();
                try (Stream<Path> stream = Files.find(rootPath,
                        Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile())) {
                    stream.forEach(filePath -> {
                        OfflinePage offlinePage = new OfflinePage();
                        offlinePage.setPath(filePath);
                        offlinePage.setRelativePath(rootPath.relativize(filePath));
                        filesMap.put(offlinePage.getRelativePath().toString(), offlinePage);
                    });
                }
            }
        } catch (IOException e) {
            throw new DioRuntimeException(e);
        }
    }

    public String downloadPage(String base, OfflinePage page, boolean offlineCreationMode) {
        URI uri;
        try {
            uri = new URI(base);
            uri = uri.resolve(page.getRelativePath().toString().replace('\\', '/'));
        } catch (URISyntaxException e) {
            throw new DioRuntimeException(e);
        }
        WebClient client = WebClient.create(uri.toString());

        ResponseEntity<String> result = client.get()
                .headers(httpHeaders -> {
                    if (offlineCreationMode)
                        httpHeaders.add(PageInfo.REQUEST_HEADER_OFFLINE_CREATION_MODE, "true");
                })
                .retrieve().toEntity(String.class)
                .block();
        return result != null ? result.getBody() : null;
    }

}
