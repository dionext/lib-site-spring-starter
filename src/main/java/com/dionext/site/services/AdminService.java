package com.dionext.site.services;

import com.dionext.configuration.GitMavenProperties;
import com.dionext.site.dto.CacheInfo;
import com.dionext.utils.OUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class AdminService extends PageCreatorService {
    private CacheManager cacheManager;

    private GitMavenProperties gitMavenProperties;

    private Environment environment;

    @Value("${info.version:unknown}")
    String version;

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    public void setGitMavenProperties(GitMavenProperties gitMavenProperties) {
        this.gitMavenProperties = gitMavenProperties;
    }

    @Autowired(required = false)
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String createAdminPage(Map<String, String> params) {
        String op = params.get("op");
        String result = null;
        try {
            if ("clear-cache".equals(op)) {
                result = clearCache(params.get("name"));
            }
        } catch (Exception ex) {
            result = "Error: " + ex;
        }
        List<CacheInfo> cacheInfo = getCacheInfo();
        StringBuilder str = new StringBuilder();
        if (result != null) {
            str.append("<div>");
            str.append(result);
            str.append("</div>");
        }
        str.append("<h1>Cache information</h1>");

        str.append("<pre>");
        str.append(OUtils.saveJsonToString(cacheInfo));
        str.append("</pre>");

        str.append("<ul>");
        str.append("<li>");
        str.append(createHrefLink("main?op=clear-cache", "Clear all caches", null));
        str.append("</li>");
        for (CacheInfo c : cacheInfo) {
            str.append("<li>");
            str.append(createHrefLink("main?op=clear-cache&name=" + c.name(), "Clear cache " + c.name(), null));
            str.append("</li>");
        }
        str.append("</ul>");

        str.append("<h1>Git information</h1>");

        str.append("<ul>");
        if (!"unknown".equals(version)) {
            str.append("<li>");
            str.append( " Version: ");
            str.append(version);
            str.append("</li>");
        }
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles != null && activeProfiles.length > 0) {
            str.append("<li>");
            str.append( " Profiles: ");
            for(String v : activeProfiles) {
                str.append(" ");
                str.append(v);
            }
            str.append("</li>");
        }

        str.append("<li>");
        str.append( " Commit id: " + gitMavenProperties.getCommitId());
        str.append("</li>");
        str.append("<li>");
        str.append( " Build time: " + gitMavenProperties.getBuildTime());
        str.append("</li>");
        str.append("<li>");
        str.append( " Build Version: " + gitMavenProperties.getBuildVersion());
        str.append("</li>");
        str.append("<li>");
        str.append( " Branch: " + gitMavenProperties.getBranch());
        str.append("</li>");
        str.append("<li>");
        str.append( " Tags: " + gitMavenProperties.getTags());
        str.append("</li>");
        str.append("</ul>");

        return str.toString();
    }

    public List<CacheInfo> getCacheInfo() {
        return cacheManager.getCacheNames()
                .stream()
                .map(this::getCacheInfo)
                .toList();
    }

    public String clearCache(String name) {
        for (var cacheName : cacheManager.getCacheNames()) {
            if (name == null || name.equals(cacheName)) {
                Cache cache = cacheManager.getCache(cacheName);
                cache.clear();
            }
        }
        return "OK cleared " + (name == null ? " all caches" : (" cache name: " + name));
    }

    private CacheInfo getCacheInfo(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache nativeCache) {
            Set<String> keys = new HashSet<>();
            Set<Object> okeys = nativeCache.asMap().keySet();
            for (Object o : okeys) {
                keys.add(o.toString());
            }
            com.github.benmanes.caffeine.cache.stats.CacheStats stats = nativeCache.stats();
            /* https://www.javadoc.io/doc/com.github.ben-manes.caffeine/caffeine/2.2.2/com/github/benmanes/caffeine/cache/stats/CacheStats.html
                Cache statistics are incremented according to the following rules:
                When a cache lookup encounters an existing cache entry hitCount is incremented.
                When a cache lookup first encounters a missing cache entry, a new entry is loaded.
                After successfully loading an entry missCount and loadSuccessCount are incremented, and the total loading time, in nanoseconds, is added to totalLoadTime.
                When an exception is thrown while loading an entry, missCount and loadFailureCount are incremented, and the total loading time, in nanoseconds, is added to totalLoadTime.
                Cache lookups that encounter a missing cache entry that is still loading will wait for loading to complete (whether successful or not) and then increment missCount.
                When an entry is computed through the asMap the loadSuccessCount or loadFailureCount is incremented.
                When an entry is evicted from the cache, evictionCount is incremented.
                No stats are modified when a cache entry is invalidated or manually removed.
                No stats are modified on a query to Cache.getIfPresent(java.lang.Object).
                No stats are modified by non-computing operations invoked on the asMap view of the cache.
             */
            return new CacheInfo(
                    cacheName, keys.size(), null, stats.toString());
        } else {
            return new CacheInfo(
                    cacheName + " (unknown type)", 0, null, "");
        }
    }


}
