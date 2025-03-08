package com.dionext.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:git.properties", ignoreResourceNotFound = true)
@Slf4j
@Getter
@Setter
public class GitMavenProperties {
    @Value("${git.commit.id:unknown}")
    private String commitId;

    @Value("${git.build.time:unknown}")
    private String buildTime;

    @Value("${git.branch:unknown}")
    private String branch;

    @Value("${git.build.version:unknown}")
    private String buildVersion;

    @Value("${git.tags:unknown}")
    private String tags;

    @PostConstruct
    public void init() {
        print();
    }

    public void print() {
        log.info("commitId: " + commitId);
        log.info("buildTime: " + buildTime);
        log.info("buildVersion: " + buildVersion);
        log.info("branch: " + branch);
        log.info("tags: " + tags);
    }
}
