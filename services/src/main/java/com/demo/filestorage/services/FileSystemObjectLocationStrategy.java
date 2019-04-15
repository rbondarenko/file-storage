package com.demo.filestorage.services;

import java.nio.file.Path;
import java.util.UUID;

public interface FileSystemObjectLocationStrategy {
    Path pathFor(final UUID id);
    int getMaxDepth();
}
