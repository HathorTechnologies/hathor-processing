package com.hathor.docs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("storage")
public class StorageProperties {
	private String storagePath;
	private String tmpPath;
	private int fileSizeNameLimitChar;
	private List<String> acceptFormats;
	private List<String> acceptMimes;
}
