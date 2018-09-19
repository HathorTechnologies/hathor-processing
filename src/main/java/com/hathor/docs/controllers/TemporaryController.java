package com.hathor.docs.controllers;

import com.hathor.docs.dto.FileDto;
import com.hathor.docs.services.StorageService;
import org.apache.tika.exception.TikaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping(value = Api.ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class TemporaryController {

    private StorageService storageService;

    public TemporaryController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(Api.TemporaryFile.TMP_FILE)
    @PreAuthorize("hasAuthority('docs.create')")
	public FileDto uploadFile(@RequestParam("file") MultipartFile file) throws IOException, TikaException, SAXException {
		return storageService.uploadFile(file);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(Api.TemporaryFile.TMP_FILE_BY_ID)
    @PreAuthorize("hasAuthority('docs.delete')")
	public void deleteFile(@PathVariable("file_id") UUID fileId) throws IOException {
        storageService.deleteTmpFile(fileId);
	}
}
