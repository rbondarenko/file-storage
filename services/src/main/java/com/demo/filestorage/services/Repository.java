package com.demo.filestorage.services;

import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public interface Repository {
    void putFile(final InputStream inputStream, final UUID id);
    Resource getFile(final UUID id);

    void putMeta(final Map<String, String> metadata, final UUID id);
    Map<String, String> getMeta(final UUID id);

    Stream<UUID> listAll();
}
