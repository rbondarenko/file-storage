package com.demo.filestorage.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileSystemRepository implements Repository {

    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final Logger log = LoggerFactory.getLogger(FileSystemRepository.class);
    private final Path rootDir;
    private final FileSystemObjectLocationStrategy locationStrategy;

    public FileSystemRepository(
            FileSystemRepositoryProperties properties,
            FileSystemObjectLocationStrategy locationStrategy) {

        this.rootDir = Paths.get(properties.getRootDir()).toAbsolutePath().normalize();
        this.locationStrategy = locationStrategy;

        try {
            Files.createDirectories(this.rootDir);
        } catch (IOException ex) {
            throw new RepositoryException("File system repository initialization failed", ex);
        }
    }


    @Override
    public void putFile(final InputStream inputStream, final UUID id) {
        final Path filePath = getFilePathFor(id);

        try {
            Files.createDirectories(filePath.getParent());
            Files.copy(inputStream, filePath); // exception
        } catch (IOException ex) {
            throw new RepositoryException("Failed to store file: " + filePath, ex);
        }
    }

    @Override
    public Resource getFile(final UUID id) {
        final Path filePath = getFilePathFor(id);

        try {
            final Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new NotFoundException("Could not read file: " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("Could not read file: " + filePath, ex);
        }
    }

    @Override
    public void putMeta(final Map<String, String> metadata, final UUID id) {
        if (metadata == null || metadata.isEmpty()) {
            return;
        }

        final Path filePath = getFilePathFor(id);

        final UserDefinedFileAttributeView view = Files.getFileAttributeView(filePath, UserDefinedFileAttributeView.class);

        if (view == null) {
            throw new RepositoryException("Failed to get file attributes: " + filePath);
        }

        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            try {
                final byte[] bytes = entry.getValue().getBytes(StandardCharsets.UTF_8);
                final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
                buffer.put(bytes);
                buffer.flip();

                view.write(entry.getKey(), buffer);
            } catch (IOException ex) {
                throw new RepositoryException("Failed to store file attribute: " + id, ex);
            }
        }
    }

    @Override
    public Map<String, String> getMeta(final UUID id) {
        final Path filePath = getFilePathFor(id);

        try {
            final UserDefinedFileAttributeView view = Files.getFileAttributeView(filePath, UserDefinedFileAttributeView.class);

            if (view == null) {
                throw new RepositoryException("Failed to get file attributes: " + filePath);
            }

            final Map<String, String> result = new HashMap<>(10);

            for (String name : view.list()) {
                final ByteBuffer buffer = ByteBuffer.allocate(view.size(name));
                view.read(name, buffer);
                buffer.flip();

                final String value = new String(buffer.array(), StandardCharsets.UTF_8);

                result.put(name, value);
            }

            return result;
        } catch (IOException ex) {
            throw new RepositoryException("Failed to read file attributes: " + id, ex);
        }
    }

    @Override
    public Stream<UUID> listAll() {
        try {
            return Files.walk(this.rootDir, this.locationStrategy.getMaxDepth())
                    .filter(path -> !path.equals(this.rootDir) && path.getFileName().toString().length() > 2)
                    .map(path -> UUID.fromString(path.getFileName().toString()));
        } catch (IOException ex) {
            throw new RepositoryException("Failed to read stored files", ex);
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkForNewFiles() {
        try {
            final List<Path> newFiles = new ArrayList<>();

            Files.walkFileTree(this.rootDir, Collections.emptySet(), this.locationStrategy.getMaxDepth(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    if (attrs.isRegularFile() && isYoungerThan(attrs.creationTime(), ONE_HOUR)) {
                        newFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            if (!newFiles.isEmpty()) {
                log.info("New files found: " + newFiles);
            } else {
                log.debug("No new files found");
            }
        } catch (IOException ex) {
            throw new RepositoryException("Failed to read stored files", ex);
        }
    }

    private boolean isYoungerThan(final FileTime creationTime, final long period) {
        return (new Date().getTime() - creationTime.toMillis()) < period;
    }

    private Path getFilePathFor(UUID id) {
        final Path path = locationStrategy.pathFor(id);
        return this.rootDir.resolve(path).normalize();
    }
}
