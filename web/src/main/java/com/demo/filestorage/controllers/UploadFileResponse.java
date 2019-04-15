package com.demo.filestorage.controllers;

public class UploadFileResponse {

    private String name;
    private String downloadContentUri;
    private String downloadMetadataUri;
    private String type;
    private long size;

    public UploadFileResponse(String fileName, String fileDownloadUri, String downloadMetadataUri, String fileType, long size) {
        this.name = fileName;
        this.downloadContentUri = fileDownloadUri;
        this.downloadMetadataUri = downloadMetadataUri;
        this.type = fileType;
        this.size = size;
    }

    public String getDownloadContentUri() {
        return downloadContentUri;
    }

    public void setDownloadContentUri(String downloadContentUri) {
        this.downloadContentUri = downloadContentUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDownloadMetadataUri() {
        return downloadMetadataUri;
    }

    public void setDownloadMetadataUri(String downloadMetadataUri) {
        this.downloadMetadataUri = downloadMetadataUri;
    }
}
