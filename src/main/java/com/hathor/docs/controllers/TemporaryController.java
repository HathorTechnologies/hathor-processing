package com.hathor.docs.controllers;

import com.hathor.docs.dto.FileDto;
import com.hathor.docs.services.StorageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping(value = Api.ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class TemporaryController {

    private final StorageService storageService;

    @ApiOperation(value = "Upload tmp file", notes = "Need JWT token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success create tmp file."),
            @ApiResponse(code = 400, message = "File has incorrect mime-type or name length or format.")
    })
    @PostMapping(Api.TemporaryFile.TMP_FILE)
	public FileDto uploadFile(@RequestParam("file") MultipartFile file) throws IOException, TikaException, SAXException {
		return storageService.uploadFile(file);
	}

    @ApiOperation(value = "Delete tmp file by fileId", notes = "Need JWT token.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Success delete."),
            @ApiResponse(code = 404, message = "File not found.")
    })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(Api.TemporaryFile.TMP_FILE_BY_ID)
	public void deleteFile(@PathVariable("file_id") UUID fileId) throws IOException {
        storageService.deleteTmpFile(fileId);
	}
}
