package com.hathor.docs.controllers;

import com.hathor.docs.dto.FileDto;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ua.ardas.db.checker.DbChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TemporaryControllerTest extends BaseControllerTest {

    @Test
    public void uploadTmpFileTest() throws IOException {
        File file = new File(testDir, "excel.xls");
        String response = given()
                .multiPart("file", file)
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .post("/docs/tmp-file")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("data_id", isEmptyOrNullString())
                .body("file_name", equalTo("excel.xls"))
                .extract().asString();
        FileDto dto = objectMapper.readValue(response, FileDto.class);

        String id = dto.getFileId().toString();
        assertTrue(new File(tmpStorage, id).exists());

        dbChecker.expectOne("SELECT count(*) FROM files WHERE file_id = '" + id + "'");
    }

    @Test
    @SqlGroup({@Sql("classpath:test-clean.sql"), @Sql("TemporaryControllerTest.sql")})
    public void deleteTmpFile() throws IOException {
        String fileId = "00000000-0000-0000-0000-000000000001";
        File file = new File(tmpStorage, fileId);
        Files.copy(new File(testDir, "empty-pdf.pdf").toPath(), file.toPath());
        assertTrue(file.exists());

        given()
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .delete("/docs/tmp-file/" + fileId)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        assertFalse(file.exists());
        dbChecker.checkDb(new DbChecker.ExpectedData().addRow("0"),"SELECT count(*) FROM files WHERE file_id = '" + fileId + "'");
    }

    @Test
    public void notAcceptableExtensionOfFile() {
        File file = new File(testDir, "text.txt");
        given()
                .multiPart("file", file)
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .post("/docs/tmp-file")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void tmpFileNotFound() {
        given()
                .when()
                .header(RIGHT_HEADER)
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .delete("/docs/tmp-file/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
