package com.demo.filestorage.services;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

@Service
public final class FileStorageService implements StorageService {

    private static final String SYSTEM_DEFINED_PREFIX = "system-defined.";
    private static final String USER_DEFINED_PREFIX = "user-defined.";

    private final IdGenerator idGenerator;
    private final Repository repo;

    public FileStorageService(final IdGenerator idGenerator,
                              final Repository repo) {
        this.idGenerator = idGenerator;
        this.repo = repo;
    }

    @Override
    public UUID store(final MultipartFile file, final Map<String, String> metadata) {
        final String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (file.isEmpty()) {
                throw new RepositoryException("Failed to store empty file: " + fileName);
            }

            final UUID id = idGenerator.generateId();

            try (InputStream inputStream = file.getInputStream()) {
                repo.putFile(inputStream, id); // exception
            }

            final Map<String, String> meta = new HashMap<>(metadata.size() + 3);

            metadata.forEach((k, v) -> meta.put(USER_DEFINED_PREFIX + k, v));

            meta.put(SYSTEM_DEFINED_PREFIX + "original-filename", file.getOriginalFilename());
            meta.put(SYSTEM_DEFINED_PREFIX + "size", String.valueOf(file.getSize()));
            meta.put(SYSTEM_DEFINED_PREFIX + "content-type", file.getContentType());

            repo.putMeta(meta, id);

            return id;
        } catch (IOException ex) {
            throw new RepositoryException("Failed to store file: " + fileName, ex);
        }
    }

    @Override
    public Map<String, String> loadUserDefinedMetadata(final UUID id) {
        final Map<String, String> result = new HashMap<>();
        repo.getMeta(id).forEach((String k, String v) -> {
            if (k.startsWith(USER_DEFINED_PREFIX)) {
                result.put(k.substring(USER_DEFINED_PREFIX.length()), v);
            }
        });
        return result;
    }

    @Override
    public String getContectType(final UUID id) {
        return repo.getMeta(id).getOrDefault(SYSTEM_DEFINED_PREFIX + "content-type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    @Override
    public String getOriginalFileName(final UUID id) {
        return repo.getMeta(id).get(SYSTEM_DEFINED_PREFIX + "original-filename");
    }

    @Override
    public Resource loadAsResource(final UUID id) {
        return repo.getFile(id);
    }

    @Override
    public Stream<UUID> listAll() {
        return repo.listAll();
    }

    @Override
    public Stream<UUID> listByCriteria(Map<String, String> criteria) {
        return repo.listAll().filter(id -> {
            Map<String, String> meta = this.loadUserDefinedMetadata(id);
            Map<String, String> result = new HashMap<>(meta);
            criteria.forEach((k, v) -> result.merge(k, v, (v1, v2) -> "*".equals(v2) || v1.equals(v2) ? v1 : v1 + "|" + v2));
            return result.equals(meta);
        });
    }
}