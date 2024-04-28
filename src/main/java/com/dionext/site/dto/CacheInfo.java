package com.dionext.site.dto;

import java.util.Set;

public record CacheInfo(
        String name, int size, Set<String> keys, String stats) {}
