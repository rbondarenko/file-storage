package com.demo.filestorage.services;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fs-repo")
public class FileSystemRepositoryProperties {
    private String rootDir;

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }
}
