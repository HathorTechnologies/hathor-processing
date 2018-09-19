package com.hathor.docs.services;

import com.hathor.docs.controllers.BaseControllerTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ua.ardas.db.checker.DbChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScheduleOfCleaningTest extends BaseControllerTest {

    @Autowired
    private ScheduleOfCleaning scheduleOfCleaning;

    private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @Before
    public void prepare() {
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.initialize();
    }

    @After
    public void stopExecutor() {
        executor.shutdown();
    }

    @Test
    @SqlGroup({@Sql("classpath:test-clean.sql"), @Sql("CleanServiceTest.sql")})
    public void testTmpCleaner() throws IOException, InterruptedException {
        String fileId = "00000000-0000-0000-0000-000000000001";
        File file = new File(tmpStorage, fileId);
        Files.copy(new File(testDir, "empty-pdf.pdf").toPath(), file.toPath());
        assertTrue(file.exists());
        executor.execute(scheduleOfCleaning::cleanTmpStorage);
        Thread.sleep(1000);
        assertFalse(file.exists());
        dbChecker.checkDb(new DbChecker.ExpectedData().addRow("0"), "SELECT count(*) FROM files WHERE file_id = '" + fileId + "'");
    }
}
