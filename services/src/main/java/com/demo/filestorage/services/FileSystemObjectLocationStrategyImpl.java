package com.demo.filestorage.services;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileSystemObjectLocationStrategyImpl implements FileSystemObjectLocationStrategy {
    @Override
    public Path pathFor(UUID id) {
        final String fileId = id.toString();
        return Paths.get(
                fileId.substring(0, 2),
                fileId.substring(2, 4),
                fileId.substring(4, 6),
                fileId.substring(6, 8),
                fileId);
    }

    @Override
    public int getMaxDepth() {
        return 5;
    }
}
