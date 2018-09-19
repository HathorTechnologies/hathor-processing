package com.hathor.docs.services;

import com.hathor.docs.entities.FileData;
import com.hathor.docs.repositories.FilesRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleOfCleaning {

    private static final Log LOG = LogFactory.getLog(ScheduleOfCleaning.class);

    private FilesRepository filesRepository;
    private StorageService storageService;

    public ScheduleOfCleaning(FilesRepository filesRepository, StorageService storageService) {
        this.filesRepository = filesRepository;
        this.storageService = storageService;
    }

    @Scheduled(cron = "${schedulers.cleaner}")
    public void cleanTmpStorage() {
        LOG.info(String.format("The tmp storage cleaner has started at %s", LocalDateTime.now()));
        List<FileData> tmpFiles = filesRepository.findByDataIdIsNull();
        if (CollectionUtils.isEmpty(tmpFiles)) {
            LOG.info("The tmp storage cleaner has found nothing");
            return;
        }
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        cleanProcess(tmpFiles, yesterday);
        LOG.info(String.format("The tmp storage cleaner has finished cleaning at %s", LocalDateTime.now()));
    }

    private void cleanProcess(List<FileData> files, LocalDateTime date) {
        files.stream()
                .filter(item -> item.getUpdatedTime().isBefore(date))
                .map(FileData::getFileId)
                .peek(item -> LOG.info(String.format("File for deleting: %s", item)))
                .forEach(item -> {
                    try {
                        storageService.deleteTmpFileWithoutChecking(item);
                    } catch (Exception e) {
                        LOG.warn(String.format("The storage cleaner could not delete file: %s", item), e);
                    }
                });
    }
}
