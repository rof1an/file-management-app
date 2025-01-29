package com.example.demo.service;

import com.example.demo.entity.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class MultipartService {

    @Value("${upload.path}")
    private String UPLOAD_PATH;

    private final FileMetadataRepository fileMetadataRepository;

    public String saveFile(MultipartFile file) {
        String randomId = UUID.randomUUID().toString();
        String resultFileName = randomId + "." + file.getOriginalFilename() + "." + file.getOriginalFilename();

        File destinationFile;
        try {
            File uploadDir = new File(UPLOAD_PATH);
            destinationFile = new File(uploadDir, resultFileName);
            file.transferTo(destinationFile);

            String destinationFileName = destinationFile.getName();
            String fileExtension =
                    destinationFileName.substring(destinationFileName.lastIndexOf('.'));

            FileMetadata fileMetadata = FileMetadata.builder()
                    .fileName(destinationFileName)
                    .filePath(destinationFile.getAbsolutePath())
                    .contentType(fileExtension)
                    .build();

            FileMetadata savedMetadata = fileMetadataRepository.save(fileMetadata);
            return savedMetadata.getFileName();
        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }
    }

    public Optional<Resource> getFileById(Long id) {
        String fileName = getFileNameById(id);

        try {
            Path filePath = Paths.get(UPLOAD_PATH, fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return Optional.of(resource);
            } else {
                log.warn("File not found or not readable: {}", filePath);
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading file", e);
        }
    }

    public String getFileNameById(Long id) {
        return fileMetadataRepository.findById(id)
                .map(FileMetadata::getFileName)
                .orElseThrow(() -> new EntityNotFoundException("File metadata not found for ID: " + id));
    }
}
