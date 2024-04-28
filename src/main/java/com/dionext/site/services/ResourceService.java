package com.dionext.site.services;

import com.dionext.site.dto.MediaImageInfo;
import com.dionext.site.dto.PageUrl;
import com.dionext.site.dto.PageUrlAlt;
import com.dionext.utils.FileUtils;
import com.dionext.utils.OUtils;
import com.dionext.utils.exceptions.DioRuntimeException;
import com.dionext.utils.exceptions.ResourceFindException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
public class ResourceService {
    private TemplateEngine templateEngine;

    private ResourceLoader resourceLoader;

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new ResourceFindException(e);
        }
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Autowired
    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String findI18nResourceAsString(String[] prefixes, String lang, String defaultLang, String relPath) {
        Resource resource = findI18nResource(prefixes, lang, defaultLang, relPath);
        return asString(resource);
    }

    public Resource findI18nResource(String[] prefixes, String lang, String defaultLang, String relPath) {
        for (String prefix : prefixes) {
            if (lang != null) {
                Resource resource = resourceLoader.getResource(prefix + "/" + lang + "/" + relPath);
                if (resource.exists()) return resource;
            }
            if (defaultLang != null && !defaultLang.equals(lang)) {
                Resource resource = resourceLoader.getResource(prefix + "/" + defaultLang + "/" + relPath);
                if (resource.exists()) return resource;
            }
            Resource resource = resourceLoader.getResource(prefix + "/" + relPath);
            if (resource.exists()) return resource;

        }
        throw new ResourceFindException("Can not find or get resource with relative path: " + relPath + " for lang " + lang + " and defaultLang " + defaultLang
                + " and prefixes:  " + Arrays.toString(prefixes));
    }

    public Resource findResource(String[] prefixes, String relPath) {
        for (String prefix : prefixes) {
            String path = prefix + File.separator + relPath;
            Resource resource = resourceLoader.getResource(path);
            if (resource.exists()) return resource;
        }
        throw new ResourceFindException("Can not find or get resource with relative path: " + relPath
                + " and prefixes:  " + Arrays.toString(prefixes));
    }

    public String findResourceAsString(String[] prefixes, String relPath) {
        Resource resource = findResource(prefixes, relPath);
        return asString(resource);
    }

    public String readFileToString(String path) {
        Resource resource = resourceLoader.getResource(path);
        return asString(resource);
    }

    private String getFilePathForMediaInfo(String mediaFileName) {
        File f = new File(mediaFileName);
        if (f.getParent() != null) {
            mediaFileName = Paths.get(f.getParent()).resolve(
                    FileUtils.getFileNameWithoutExtension(mediaFileName)) + ".mediainfo";
        } else {
            mediaFileName = FileUtils.getFileNameWithoutExtension(mediaFileName) + ".mediainfo";
        }
        return mediaFileName;
    }

    private String getFilePathForMref(String mediaFileName) {
        File f = new File(mediaFileName);
        if (f.getParent() != null) {
            mediaFileName = Paths.get(f.getParent()).resolve(
                    FileUtils.getFileNameWithoutExtension(mediaFileName)) + ".mref";
        } else {
            mediaFileName = FileUtils.getFileNameWithoutExtension(mediaFileName) + ".mref";
        }
        return mediaFileName;
    }

    public MediaImageInfo getMediaInfo(String[] prefixes, String mediaFileName) {
        try {
            mediaFileName = getMref(prefixes, mediaFileName);
            String infoFileName = getFilePathForMediaInfo(mediaFileName);
            String str = findResourceAsString(prefixes, infoFileName);
            return OUtils.loadJsonFromString(OUtils.replaceUT8BOM(str), MediaImageInfo.class);
        } catch (InvalidPathException | ResourceFindException ex) {
            return null;
        } catch (JsonProcessingException e) {
            throw new DioRuntimeException(e);
        }
    }

    private String getMref(String[] prefixes, String mediaFileName) {
        try {
            String mrefFileName = getFilePathForMref(mediaFileName);
            String src = findResourceAsString(prefixes, mrefFileName);
            if (src != null) {
                src = OUtils.replaceUT8BOM(src);
                File f = new File(mediaFileName);
                if (f.getParent() != null)
                    mediaFileName = Paths.get((new File(mediaFileName)).getParent()).resolve(src).toString();
                else
                    mediaFileName = src;
            }
        } catch (Exception ex) {
            //no mref
        }
        return mediaFileName;
    }

    public String generatePageFromTemplate(String templateName, Locale locale, Map<String, String> varMap) {
        Context ctx = new Context(locale);
        ctx.setVariable("lang", locale.getLanguage());
        for (var e : varMap.entrySet()) {
            ctx.setVariable(e.getKey(), e.getValue());
        }
        return templateEngine.process(templateName, ctx);
    }
    public List<PageUrl> findAllPages(String[] prefixes, String[] langs) {
        Map<String, PageUrl> filesMap = new HashMap<>();
        if (langs != null) {
            for (String lang : langs) {
                for (String prefix : prefixes) {
                    findAllPagesForLang(lang, prefix, filesMap);
                }
            }
        }
        else{
            for (String prefix : prefixes) {
                findAllPagesForLang(null, prefix, filesMap);
            }
        }
        return filesMap.values().stream().toList();
    }

    private void findAllPagesForLang(String lang, String prefix, Map<String, PageUrl> filesMap) {
        Resource resource = resourceLoader.getResource(prefix + (lang != null ? ("/" + lang):""));
        try {
            if (resource.exists()) {
                Path rootPath = resource.getFile().toPath();
                try (Stream<Path> stream = Files.find(rootPath,
                        Integer.MAX_VALUE,
                        (filePath, fileAttr) -> {
                            if (!fileAttr.isRegularFile()) return false;
                            String fileName = filePath.getName(filePath.getNameCount() - 1).toString();
                            Path relPath = rootPath.relativize(filePath);
                            String firstFolder = relPath.getNameCount() > 0 ? relPath.getName(0).toString(): null;

                            if (".git".equals(firstFolder)) return false;
                            if ("images".equals(firstFolder)) return false;
                            if ("LICENSE".equals(firstFolder)) return false;
                            if (fileName.startsWith("_")) return false;
                            return true;
                        }  )) {
                    stream.forEach(filePath -> {
                        String relPath = rootPath.relativize(filePath).toString().replace('\\', '/');
                        PageUrl pageUrl = filesMap.computeIfAbsent(relPath, k -> {
                            PageUrl p = new PageUrl();
                            p.setRelativePath(relPath);
                            return p;
                        });
                        if (lang != null) {
                            PageUrlAlt pageUrlAlt = new PageUrlAlt();
                            pageUrlAlt.setLang(lang);
                            pageUrl.getAltUrls().add(pageUrlAlt);//to do
                        }
                    });
                }
            }
        } catch (IOException e) {
            throw new DioRuntimeException(e);
        }
    }

}


