package com.demo.filestorage.controllers;

import com.demo.filestorage.services.NotFoundException;
import com.demo.filestorage.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/files")
public class FilesController {

    private static final Logger logger = LoggerFactory.getLogger(FilesController.class);
    private final StorageService storageService;

    public FilesController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/{file-id:.+}")
    public ResponseEntity<?> updateFile(
            @PathVariable(name = "file-id") UUID fileId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("meta-data") String[] rawMetaData
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping
    public UploadFileResponse uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("meta-data") String[] rawMetaData
    ) {
        final UUID fileId = storageService.store(file, parseMetadata(rawMetaData));

        return new UploadFileResponse(file.getOriginalFilename(),
                makeDownloadUriFor(fileId.toString(), "content"),
                makeDownloadUriFor(fileId.toString(), "meta-data"),
                file.getContentType(),
                file.getSize());
    }

    @GetMapping("/{file-id:.+}")
    @ResponseBody
    public ResponseEntity<?> downloadFile(@PathVariable("file-id") UUID fileId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/{file-id:.+}/content")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("file-id") UUID fileId,
            HttpServletRequest request) {

        final Resource resource = storageService.loadAsResource(fileId);
        final String contentType = storageService.getContectType(fileId);
        final String originalFileName = storageService.getOriginalFileName(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                .body(resource);
    }

    @GetMapping("/{file-id:.+}/meta-data")
    public Map<String, String> downloadMetadata(@PathVariable("file-id") UUID fileId) {
        Map<String, String> metadata = null;

        try {
            metadata = storageService.loadUserDefinedMetadata(fileId);
        } catch (Exception ex) {
            logger.error("Could not read file metadata.", ex);
        }

        return metadata;
    }

    @GetMapping
    public List<String> listFiles(@RequestParam(value = "q", required = false) String query) throws IOException {
        final Stream<UUID> stream = query == null
                ? storageService.listAll()
                : storageService.listByCriteria(parseQuery(query));

        return stream
                .map(id -> makeDownloadUriFor(id.toString(), "content"))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    private Map<String, String> parseMetadata(String[] input) {
        return Arrays.stream(input)
                .map(line -> line.split("=", 2))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));
    }

    private Map<String, String> parseQuery(String input) {
        return parseMetadata(input.split(","));
    }

    private String makeDownloadUriFor(final String fileId, final String part) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("api", "v1", "files", fileId, part)
                .toUriString();
    }
}