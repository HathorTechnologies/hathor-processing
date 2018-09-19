package com.hathor.docs.controllers;

import com.hathor.docs.dto.FileDto;
import com.hathor.docs.dto.FileResponseDto;
import com.hathor.docs.dto.MoveFileDto;
import com.hathor.docs.services.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = Api.ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class FilesController {

    private final StorageService storageService;

    @GetMapping(Api.Files.FILES_BY_ID)
    public ResponseEntity<byte[]> downloadFile(@PathVariable("data_id") UUID dataId, @PathVariable("file_id") UUID fileId) throws IOException {
        FileResponseDto responseDto = storageService.downloadFile(dataId, fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(responseDto.getMimeType()));
        return new ResponseEntity<>(responseDto.getFile(), headers, HttpStatus.OK);
    }

    @DeleteMapping(Api.Files.FILES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFiles(@RequestParam("data_id") Set<UUID> dataId, @RequestParam("file_id") Set<UUID> fileId) {
        storageService.deleteFile(dataId, fileId);
    }

    @PutMapping(Api.Files.FILES)
    public List<FileDto> moveFiles(@RequestBody List<MoveFileDto> moveFileDtos) throws IOException {
        return storageService.moveFiles(moveFileDtos);
    }

    @GetMapping(Api.Files.FILES)
    public List<FileDto> getFiles(@RequestParam("data_id") Set<UUID> dataIds, @RequestParam("file_id") Set<UUID> fileIds) {
        return storageService.getFiles(dataIds, fileIds);
    }
}
