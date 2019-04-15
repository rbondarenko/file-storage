package com.demo.filestorage.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class NewFilesReporterTask {

    private static final Logger log = LoggerFactory.getLogger(NewFilesReporterTask.class);
    private final Path rootDir;

    public NewFilesReporterTask(FileSystemRepositoryProperties properties) {
        this.rootDir = Paths.get(properties.getRootDir()).toAbsolutePath().normalize();
    }

}
