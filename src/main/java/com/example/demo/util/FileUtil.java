package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {

    public static String determineContentType(Resource file) {
        String contentType = "application/octet-stream";

        try {
            Path path = Paths.get(file.getFile().getAbsolutePath());
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            log.warn("Не удалось определить MIME-тип файла, используем default");
        }

        return contentType;
    }
}
