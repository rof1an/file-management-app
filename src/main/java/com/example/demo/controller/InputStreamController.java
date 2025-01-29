package com.example.demo.controller;

import com.example.demo.service.InputStreamService;
import com.example.demo.util.FileUtil;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("api/v1/input-streams")
@RequiredArgsConstructor
public class InputStreamController {

    private final InputStreamService inputStreamService;

    @PostMapping
    public String uploadFile(
            HttpServletRequest request,
            @RequestHeader("File-Name") String fileName,
            @RequestHeader("File-Extension") String fileExtension) {

        ServletInputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            log.warn("Не удалось прочитать InputStream");
            throw new RuntimeException(e);
        }

        return inputStreamService.saveFile(inputStream, fileName, fileExtension);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFileById(@PathVariable("id") Long id) {
        Resource resourceById = inputStreamService.getFileById(id);

        if (resourceById == null || !resourceById.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = FileUtil.determineContentType(resourceById);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resourceById);
    }
}
