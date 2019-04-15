package com.demo.filestorage.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public interface StorageService {
    UUID store(MultipartFile file, Map<String, String> userDefinedMetadata);
    Resource loadAsResource(final UUID id);
    Map<String, String> loadUserDefinedMetadata(final UUID id);

    String getContectType(UUID id);
    String getOriginalFileName(UUID id);

    Stream<UUID> listAll();
    Stream<UUID> listByCriteria(final Map<String, String> criteria);
}