package com.dionext.site.dto;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
public class OfflinePage {
    private Path relativePath;
    private Path path;
}
