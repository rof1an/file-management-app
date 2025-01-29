package com.example.demo.service;

import com.example.demo.entity.FileMetadata;
import com.example.demo.repo.FileMetadataRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class InputStreamService {

    @Value("${upload.path}")
    private String UPLOAD_PATH;

    private final FileMetadataRepository fileMetadataRepository;

    public Resource getFileById(Long id) {
        FileMetadata fileMetadataById = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id = " + id));

        try {
            String filePath = fileMetadataById.getFilePath();
            return new UrlResource(Paths.get(filePath).toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveFile(InputStream inputStream, String fileName, String fileExtension) {
        try {
            return saveFile(inputStream.readAllBytes(), fileName, fileExtension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveFile(byte[] bytes, String fileName, String fileExtension) {
        String randomId = UUID.randomUUID().toString();
        String resultFileName = randomId + "." + fileName + "." + fileExtension;

        File uploadDir = new File(UPLOAD_PATH);
        File destinationFile = new File(uploadDir, resultFileName);

        try {
            Files.write(destinationFile.getAbsoluteFile().toPath(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String destinationFileName = destinationFile.getName();
        String destinationFileExtension =
                destinationFileName.substring(destinationFileName.lastIndexOf('.'));

        FileMetadata fileMetadata = FileMetadata.builder()
                .fileName(fileName)
                .filePath(destinationFile.getAbsolutePath())
                .contentType(destinationFileExtension)
                .build();

        FileMetadata savedMetadata = fileMetadataRepository.save(fileMetadata);
        log.info("Data id when saving = {}", savedMetadata.getId());
        return savedMetadata.getFilePath();
    }
}
