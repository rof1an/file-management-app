package com.example.demo.controller;

import com.example.demo.service.MultipartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("api/v1/multipart-files")
@RequiredArgsConstructor
public class MultipartController {

    private final MultipartService multipartService;

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        return multipartService.saveFile(multipartFile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFileById(@PathVariable("id") Long id) {
        return multipartService.getFileById(id)
                .map(resource -> {
                    String contentType = "application/octet-stream";

                    try {
                        Path path = Paths.get(multipartService.getFileNameById(id));
                        contentType = Files.probeContentType(path);
                    } catch (IOException e) {
                        log.warn("Не удалось определить MIME-тип файла, используем default");
                    }

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header("Content-Disposition", "inline")
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
