package com.hathor.docs.services;

import com.hathor.docs.dto.DtoUtils;
import com.hathor.docs.dto.FileDto;
import com.hathor.docs.dto.FileResponseDto;
import com.hathor.docs.dto.MoveFileDto;
import com.hathor.docs.entities.FileData;
import com.hathor.docs.exceptions.BadRequestException;
import com.hathor.docs.repositories.FilesRepository;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@CommonsLog
@AllArgsConstructor
public class StorageService {

    private final FilesRepository filesRepository;
    private final StorageUtilsService storageUtilsService;

    @Transactional
    public void deleteTmpFile(UUID fileId) throws IOException {
        log.info(String.format("Delete tmp file: id = %s", fileId));
        FileData file = filesRepository.findByFileIdAndDataIdIsNull(fileId)
                .orElseThrow(() -> new EntityNotFoundException("Data file not found"));
        storageUtilsService.deleteTmpFile(fileId);
        filesRepository.delete(file);
    }

    @Transactional
    protected void deleteTmpFileWithoutChecking(UUID fileId) throws IOException {
        log.info(String.format("Delete tmp file: id = %s", fileId));
        FileData file = filesRepository.findByFileIdAndDataIdIsNull(fileId)
                .orElseThrow(() -> new EntityNotFoundException("Data file not found"));
        storageUtilsService.deleteTmpFile(fileId);
        filesRepository.delete(file);
    }

    @Transactional
    public void deleteFile(Set<UUID> dataIds, Set<UUID> fileIds) {
        log.info(String.format("Delete data with file: dataId = %s  fileId = %s", dataIds, fileIds));
        List<FileData> files = filesRepository.findByDataIdInAndFileIdIn(dataIds, fileIds);
        if (fileIds.size() != files.size()) {
            throw new EntityNotFoundException("Files not found");
        }
        filesRepository.deleteInBatch(files);
        fileIds.forEach(storageUtilsService::deleteFile);
    }

    @Transactional
    public void deleteFileWithoutChecking(Set<UUID> dataIds, Set<UUID> fileIds) {
        log.info(String.format("Delete data with file: dataId = %s  fileId = %s", dataIds, fileIds));
        List<FileData> files = filesRepository.findByDataIdInAndFileIdIn(dataIds, fileIds);
        filesRepository.deleteInBatch(files);
        fileIds.forEach(storageUtilsService::deleteFile);
    }

    public FileDto uploadFile(MultipartFile multipartFile) throws TikaException, SAXException, IOException {
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        log.info(String.format("Receive tmp file: extensions = %s\ncontentType = %s\nOriginalFilename = %s",
                extension,
                multipartFile.getContentType(),
                multipartFile.getOriginalFilename())
        );

        if (!storageUtilsService.checkAcceptFileFormat(extension)) {
            throw new BadRequestException(String.format("The format of file is not acceptable: %s", extension));
        }
        String fileBaseName = FilenameUtils.getBaseName(multipartFile.getOriginalFilename());
        if (!storageUtilsService.checkFileNameLengthLimit(fileBaseName)) {
            throw new BadRequestException(String.format("The name of file is so long: %s", fileBaseName));
        }

        String mimeTypeFile = getMimeTypeFile(multipartFile);
        if (!storageUtilsService.checkAcceptMimeTypeFile(mimeTypeFile)) {
            throw new BadRequestException(String.format("The mime-type is not acceptable: %s", mimeTypeFile));
        }

        UUID fileId = UUID.randomUUID();
        FileData file = FileData.builder()
                .dataId(null)
                .fileId(fileId)
                .sizeKb(BigDecimal.valueOf(storageUtilsService.transformBytesToKb(multipartFile.getSize())))
                .originalName(multipartFile.getOriginalFilename())
                .mimeType(mimeTypeFile)
                .build();

        storageUtilsService.saveInTmpStorageDir(fileId.toString(), multipartFile);
        filesRepository.save(file);
        return DtoUtils.convertToDto(file);
    }

    public FileResponseDto downloadFile(UUID dataId, UUID fileId) throws IOException {
        FileData fileData = findFileByDataIdAndFileId(dataId, fileId);
        byte[] file = storageUtilsService.getFile(fileId);
        return new FileResponseDto(file, fileData.getMimeType());
    }

    public List<FileDto> moveFiles(List<MoveFileDto> moveFileDtos) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>(moveFileDtos.size());

        for (MoveFileDto item : moveFileDtos) {
            fileDtos.add(moveFile(item.getDataId(), item.getFileId()));
        }

        return fileDtos;
    }

    private FileDto moveFile(UUID dataId, UUID fileId) throws IOException {
        log.info(String.format("Move files from tmp to stable storage: dataId = %s  fileIds = %s", dataId, fileId));
        FileData file = filesRepository.findByFileIdAndDataIdIsNull(fileId).orElseThrow(() ->
                new EntityNotFoundException("Data file not found")
        );
        storageUtilsService.fileMoveToStorage(fileId);
        file.setDataId(dataId);
        filesRepository.save(file);
        return DtoUtils.convertToDto(file);
    }

    private FileData findFileByDataIdAndFileId(UUID dataId, UUID fileId) {
        return filesRepository.findByDataIdAndFileId(dataId, fileId)
                .orElseThrow(() -> new EntityNotFoundException("Data file not found"));
    }

    private String getMimeTypeFile(MultipartFile multipartFile) throws IOException, SAXException, TikaException {
        Metadata metadata = new Metadata();
        try (InputStream stream = multipartFile.getInputStream()) {
            new AutoDetectParser().parse(stream, new BodyContentHandler(), metadata);
        }

        return metadata.get(Metadata.CONTENT_TYPE);
    }

    public List<FileDto> getFiles(Set<UUID> dataIds, Set<UUID> fileIds) {
            List<FileData> files = filesRepository.findByDataIdInAndFileIdIn(dataIds, fileIds);
            if (fileIds.size() != files.size()) {
                throw new EntityNotFoundException("Files not found");
            }
            return files.stream()
                    .map(DtoUtils::convertToDto)
                    .collect(Collectors.toList());
    }
}
