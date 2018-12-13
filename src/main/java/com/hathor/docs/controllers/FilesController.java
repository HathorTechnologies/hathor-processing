package com.hathor.docs.controllers;

import com.hathor.docs.dto.FileDto;
import com.hathor.docs.dto.FileResponseDto;
import com.hathor.docs.dto.MoveFileDto;
import com.hathor.docs.services.StorageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "Download file by dataId and fileId", notes = "Need JWT token.")
    @ApiResponses({
            @ApiResponse(code = 404, message = "File not found.")
    })
    @GetMapping(Api.Files.FILES_BY_ID)
    public ResponseEntity<byte[]> downloadFile(@PathVariable("data_id") UUID dataId, @PathVariable("file_id") UUID fileId) throws IOException {
        FileResponseDto responseDto = storageService.downloadFile(dataId, fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(responseDto.getMimeType()));
        return new ResponseEntity<>(responseDto.getFile(), headers, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete files by dataIds and fileIds", notes = "Need JWT token.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successful delete."),
            @ApiResponse(code = 404, message = "File not found.")
    })
    @DeleteMapping(Api.Files.FILES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFiles(@RequestParam("data_id") Set<UUID> dataId, @RequestParam("file_id") Set<UUID> fileId) {
        storageService.deleteFile(dataId, fileId);
    }

    @ApiOperation(value = "Move tmp file to stable storage", notes = "Need JWT token.")
    @ApiResponses({
            @ApiResponse(code = 404, message = "File not found.")
    })
    @PutMapping(Api.Files.FILES)
    public List<FileDto> moveFiles(@RequestBody List<MoveFileDto> moveFileDtos) throws IOException {
        return storageService.moveFiles(moveFileDtos);
    }

    @ApiOperation(value = "Get files by fileIds", notes = "Need JWT token.")
    @ApiResponses({
            @ApiResponse(code = 404, message = "File not found.")
    })
    @GetMapping(Api.Files.FILES)
    public List<FileDto> getFiles(@RequestParam("data_id") Set<UUID> dataIds, @RequestParam("file_id") Set<UUID> fileIds) {
        return storageService.getFiles(dataIds, fileIds);
    }
}
