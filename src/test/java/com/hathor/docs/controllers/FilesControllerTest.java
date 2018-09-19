package com.hathor.docs.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Sets;
import com.hathor.docs.dto.MoveFileDto;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ua.ardas.db.checker.DbChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SqlGroup({@Sql("classpath:test-clean.sql"), @Sql("FilesControllerTest.sql")})
public class FilesControllerTest extends BaseControllerTest {

    private File pdfFile;
    private File wordFile;
    private File tmpFile;
    private static final String PDF_ID = "00000000-0000-0000-0000-000000000001";
    private static final String WORD_ID = "00000000-0000-0000-0000-000000000002";
    private static final String TMP_FILE_ID = "00000000-0000-0000-0000-000000000003";

    @Before
    public void prepareFiles() throws IOException {
        pdfFile = new File(stableStorage, PDF_ID);
        wordFile = new File(stableStorage, WORD_ID);
        tmpFile = new File(tmpStorage, TMP_FILE_ID);

        Files.copy(new File(testDir, "empty-pdf.pdf").toPath(), pdfFile.toPath());
        Files.copy(new File(testDir, "word.doc").toPath(), wordFile.toPath());
        Files.copy(new File(testDir, "excel.xls").toPath(), tmpFile.toPath());

        assertTrue(pdfFile.exists());
        assertTrue(wordFile.exists());
        assertTrue(tmpFile.exists());
    }

    @Test
    public void downloadFile() throws IOException {
        byte [] bytesFile = Files.readAllBytes(wordFile.toPath());

        given()
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .get("/docs/data/77777777-7777-7777-7777-777777777777/files/00000000-0000-0000-0000-000000000002")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is(new String(bytesFile)));
    }

    @Test
    public void notFoundDownloadFile() {
        given()
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .get("/docs/data/" + UUID.randomUUID() + "/files/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void deleteFile() {
        given()
                .param("data_id", "77777777-7777-7777-7777-777777777777", "66666666-6666-6666-6666-666666666666")
                .param("file_id", "00000000-0000-0000-0000-000000000001", "00000000-0000-0000-0000-000000000002")
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .delete("/docs/files")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        assertFalse(pdfFile.exists());
        assertFalse(wordFile.exists());
        dbChecker.checkDb(new DbChecker.ExpectedData().addRow("0"),
                "SELECT count(*) FROM files WHERE data_id in ('77777777-7777-7777-7777-777777777777', '66666666-6666-6666-6666-666666666666') AND file_id in ('" + PDF_ID + "', '" + WORD_ID + "')");
    }

    @Test
    public void moveFiles() throws JsonProcessingException {
        MoveFileDto moveFileDto = MoveFileDto.builder()
                .dataId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .fileId(UUID.fromString(TMP_FILE_ID))
                .build();

        given()
                .contentType(ContentType.JSON)
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .body(objectMapper.writeValueAsString(Collections.singletonList(moveFileDto)))
                .put("/docs/files")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("", hasSize(1))
                .body("[0].file_id", equalTo(TMP_FILE_ID))
                .body("[0].data_id", equalTo("99999999-9999-9999-9999-999999999999"))
                .body("[0].file_name", equalTo("excel.xls"));

        assertFalse(tmpFile.exists());
        assertTrue(new File(stableStorage, TMP_FILE_ID).exists());
        dbChecker.expectOne("SELECT count(*) FROM files WHERE data_id = '99999999-9999-9999-9999-999999999999' AND file_id = '" + TMP_FILE_ID + "'");
    }

    @Test
    public void notFoundMoveFiles() {
        MoveFileDto moveFileDto = MoveFileDto.builder()
                .dataId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .fileId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .build();

        given()
                .contentType(ContentType.JSON)
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .body(Collections.singletonList(moveFileDto))
                .put("/docs/files")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Data file not found"));
    }

    @Test
    public void getFiles() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .param("file_id", Sets.newHashSet(
                        "00000000-0000-0000-0000-000000000001", "00000000-0000-0000-0000-000000000002"))
                .param("data_id", Sets.newHashSet(
                        "66666666-6666-6666-6666-666666666666", "77777777-7777-7777-7777-777777777777"))
                .get("/docs/files")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("", hasSize(2))
                .body("[0].file_id", equalTo("00000000-0000-0000-0000-000000000001"))
                .body("[0].data_id", equalTo("66666666-6666-6666-6666-666666666666"))
                .body("[0].file_name", equalTo("empty-pdf.pdf"))
                .body("[1].file_id", equalTo("00000000-0000-0000-0000-000000000002"))
                .body("[1].data_id", equalTo("77777777-7777-7777-7777-777777777777"))
                .body("[1].file_name", equalTo("word.doc"));
    }

}
