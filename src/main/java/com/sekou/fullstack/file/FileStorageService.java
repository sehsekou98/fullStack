package com.sekou.fullstack.file;


import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;
    public String saveFile(
            @NotNull MultipartFile sourceFile,
            @NotNull String userId
            ){
        final String fileUploadSubPath = "user" + separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(
           @NotNull MultipartFile sourceFile,
           @NotNull String fileUploadSubPath
    ) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetedFolder = new File(finalUploadPath);
        if (!targetedFolder.exists()) {
            boolean folderCreated = targetedFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create the target folder.");
                return null;
            }
        }
            final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
            String targetFilePath = finalUploadPath + separator + System.currentTimeMillis() + "." + fileExtension;
            Path tragetPath = Paths.get(targetFilePath);
            try {
                Files.write(tragetPath, sourceFile.getBytes());
                log.info("File save to {}", targetFilePath);
                return targetFilePath;
            } catch (IOException e) {
                log.error("File was not save.", e);
            }
            return null;
        }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}

