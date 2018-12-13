package com.hathor.docs.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hathor.docs.DocsApplication;
import com.hathor.docs.auth.AuthJwtToken;
import com.hathor.docs.config.TestConfig;
import com.hathor.docs.properties.AuthProperties;
import com.hathor.docs.properties.StorageProperties;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.ardas.db.checker.DbChecker;
import ua.ardas.jwt.AuthConst;
import ua.ardas.jwt.JwtService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Sql("classpath:test-clean.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DocsApplication.class, DbChecker.class, TestConfig.class})
public abstract class BaseControllerTest {

    private static final String TMP = "tmp";
    private static final String STORAGE = "storage";

    protected String testDir = new File(getClass().getClassLoader().getResource("files").getPath()).getAbsolutePath();
    protected String tmpStorage = new File(testDir, TMP).getAbsolutePath();
    protected String stableStorage = new File(testDir, STORAGE).getAbsolutePath();

    protected static final int ADMIN_ID = 1;
    protected static final UUID AUTH_ID = UUID.randomUUID();
    protected Cookie ADMIN_RIGHT_AUTHORIZATION_COOKIE;
    protected Header RIGHT_HEADER;
    protected Header WRONG_JWT_HEADER;

    @Autowired
    protected AuthProperties authProperties;
    @Autowired
    protected ObjectMapper objectMapper;
    @LocalServerPort
    private Integer port;
    @Autowired
    protected DbChecker dbChecker;
    @MockBean
    private StorageProperties storageProperties;
    @SpyBean
    protected JwtService jwtService;

    @Value("#{'${storage.accept-formats}'.split(',')}")
    private List<String> acceptFormats;

    @Value("#{'${storage.accept-mimes}'.split(',')}")
    private List<String> acceptMimes;

    @Before
    public void setUp() throws IOException {
        RestAssured.port = port;

        BDDMockito.given(storageProperties.getTmpPath()).willReturn(tmpStorage);
        BDDMockito.given(storageProperties.getStoragePath()).willReturn(stableStorage);
        BDDMockito.given(storageProperties.getFileSizeNameLimitChar()).willReturn(100);
        BDDMockito.given(storageProperties.getAcceptFormats()).willReturn(acceptFormats);
        BDDMockito.given(storageProperties.getAcceptMimes()).willReturn(acceptMimes);

        Files.createDirectories(new File(tmpStorage).toPath());
        Files.createDirectories(new File(stableStorage).toPath());


        String adminJwtToken = jwtService.createJwtToken(
                AuthJwtToken.builder()
                        .userId(ADMIN_ID)
                        .email("test@test.com")
                        .xsrfToken(AUTH_ID.toString())
                        .build());

        Date expiryDate = new Date(new Date().getTime() + authProperties.getTokenExpireHours() * 3600);
        ADMIN_RIGHT_AUTHORIZATION_COOKIE = new Cookie.Builder(AuthConst.JWT_TOKEN_COOKIE, adminJwtToken)
                .setHttpOnly(true)
                .setExpiryDate(expiryDate)
                .build();

        RIGHT_HEADER = new Header(AuthConst.XSRF_TOKEN_HEADER, AUTH_ID.toString());
        WRONG_JWT_HEADER = new Header(AuthConst.XSRF_TOKEN_HEADER, UUID.randomUUID().toString());
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(new File(tmpStorage));
        FileUtils.deleteDirectory(new File(stableStorage));
    }

}