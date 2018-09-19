package com.hathor.docs.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Api.ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class VersionController {

    @Value("${app.version}")
    private String version;

    @GetMapping(value = Api.BuildVersion.VERSION)
    public String getVersion() {
        return version;
    }

}
