package com.hathor.docs.services;

import com.hathor.docs.properties.StorageProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static java.nio.file.Files.*;

@Service
public class StorageUtilsService {

    private static final int BYTES_IN_KB = 1024;

    private final StorageProperties storageProperties;

    @Value("${spring.http.multipart.max-file-size: 1}")
    private int maxFileSize;

    private static final Log LOG = LogFactory.getLog(StorageUtilsService.class);

    @Autowired
    public StorageUtilsService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    public double transformBytesToKb(long sizeKb) {
        return sizeKb/(double)BYTES_IN_KB;
    }

    public boolean checkAcceptFileFormat(String fileExtension) {
        return storageProperties.getAcceptFormats().contains(fileExtension.toLowerCase());
    }

    public boolean checkAcceptMimeTypeFile(String mimeType) {
        return storageProperties.getAcceptMimes().contains(mimeType.toLowerCase());
    }

    public boolean checkFileNameLengthLimit(String fileBaseName) {
        return fileBaseName.length() <= storageProperties.getFileSizeNameLimitChar();
    }

    public void fileMoveToStorage(UUID fileId) throws IOException {
        Path path = Paths.get(storageProperties.getStoragePath());
        if (!exists(path)) {
            LOG.info("Make a stable storage");
            createDirectories(path);
        }

        Path tmpDirPath = Paths.get(storageProperties.getTmpPath());

        Path fileTempPath = tmpDirPath.resolve(fileId.toString());
        Path fileStoragePath = path.resolve(fileId.toString());
        move(fileTempPath, fileStoragePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void deleteFile(UUID fileId) {
        try {
            deleteTmpFile(fileId);
            Path stableFilePath = Paths.get(storageProperties.getStoragePath()).resolve(fileId.toString());
            deleteIfExists(stableFilePath);
        } catch (IOException e) {
            LOG.error(String.format("Could not delete file with id: %s", fileId), e);
        }
    }

    public void deleteTmpFile(UUID fileId) throws IOException {
        Path tmpFilePath = Paths.get(storageProperties.getTmpPath()).resolve(fileId.toString());
        deleteIfExists(tmpFilePath);
    }

    public void saveInTmpStorageDir(String updatedName, MultipartFile multipartFile) throws IOException {

        Path tmpDirPath = Paths.get(storageProperties.getTmpPath());

        if (!exists(tmpDirPath)) {
            LOG.info("Make a tmp storage");
            java.nio.file.Files.createDirectories(tmpDirPath);
        }

        Path fileTempPath = tmpDirPath.resolve(updatedName);
        Files.copy(multipartFile.getInputStream(), fileTempPath);
    }

    public byte[] getFile(UUID fileId) throws IOException {
        Path fileTmpPath = Paths.get(storageProperties.getTmpPath()).resolve(fileId.toString());
        Path fileStoragePath = Paths.get(storageProperties.getStoragePath()).resolve(fileId.toString());

        if (exists(fileTmpPath)) {
            LOG.info(String.format("File in tmp storage: fileId = %s", fileId));
            return readAllBytes(fileTmpPath);
        } else if (exists(fileStoragePath)) {
            LOG.info(String.format("File in stable storage: fileId = %s", fileId));
            return readAllBytes(fileStoragePath);
        }
        LOG.warn(String.format("File does not exist: fileId = %s", fileId));
        return new byte[0];
    }
}

