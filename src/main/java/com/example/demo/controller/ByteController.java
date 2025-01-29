package com.example.demo.controller;

import com.example.demo.service.InputStreamService;
import com.example.demo.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/bytes")
@RequiredArgsConstructor
public class ByteController {

    private final InputStreamService inputStreamService;

    @PostMapping
    public String uploadFile(
            @RequestBody byte[] bytesFileData,
            @RequestHeader("File-Name") String fileName,
            @RequestHeader("File-Extension") String fileExtension) {

        return inputStreamService.saveFile(bytesFileData, fileName, fileExtension);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFileById(@PathVariable("id") Long id) {
        Resource resourceById = inputStreamService.getFileById(id);
        String contentType = FileUtil.determineContentType(resourceById);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resourceById);
    }
}
