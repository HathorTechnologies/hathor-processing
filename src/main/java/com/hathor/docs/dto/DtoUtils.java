package com.hathor.docs.dto;

import com.hathor.docs.entities.FileData;

import java.util.Optional;

public class DtoUtils {
    public static FileDto convertToDto(FileData entity) {
        return FileDto.builder()
                .fileId(entity.getFileId())
                .dataId(Optional.ofNullable(entity.getDataId()).orElse(null))
                .fileName(entity.getOriginalName())
                .build();
    }
}
