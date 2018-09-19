package com.hathor.docs.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hathor.docs.auth.AuthJwtToken;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ua.ardas.jwt.AuthConst;

import java.util.Date;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CommonControllerTest extends BaseControllerTest {

    @Test
    public void testVersion() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/docs/version")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(equalTo("latest"));
    }

    @Test
    public void testToken() throws JsonProcessingException {
        given()
                .when()
                .delete("/docs/tmp-file/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("Token is not set."));

        given()
                .when()
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .delete("/docs/tmp-file/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("Xsrf token is not set."));

        given()
                .when()
                .header(new Header(AuthConst.XSRF_TOKEN_HEADER, UUID.randomUUID().toString()))
                .cookie(ADMIN_RIGHT_AUTHORIZATION_COOKIE)
                .delete("/docs/tmp-file/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("Xsrf token does not match."));

        Cookie tokenCookie = new Cookie.Builder(
                AuthConst.JWT_TOKEN_COOKIE,
                "eyJhbGciOiJSUzUxMiJ9.eyJhdXRoX3Rva2VuIjoie1widXNlcl9pZ" +
                        "FwiOjMzNSxcImRlcGFydG1lbnRfaWRcIjpudWxsLFwibG9jYWxlX2tleVwiO" +
                        "lwidWFfVUFcIixcInhzcmZfdG9rZW5cIjpcIjY2M2M5NGMyLTEyYzMtNG" +
                        "UzMy05YmQ2LWI2NzlmOGRasdadjZWUwZlwiLFwicm9sZV9pZFwiOjAsXCJ1c2VyX3R5cGVfa2" +
                        "V5XCI6XCJyb290XCJ9IiwiZXhwIjoxNTE2NTcyNzQ0fQ.giK9KVhpuSgrXH0rVHoZz70iwazOgv" +
                        "OiUMtNqZzmYH4VwgPe9QQjUvlksasAouOqpYdQhz7p2DTwQRkN04oriBhN9lwrIoNd4_Q0sxk33HDgDuqj" +
                        "LScI9p1zMbfjB7QFUkg5J6tt8CISitFTQFuT-NSA0Yj84O-ZfQD7BDqBtBxTNKoW" +
                        "ZfQlWiAgvKHuEy7Z3JMpdck3kYkNrYsSvUip5X-jU-x6_ki7bsjtsQU-3Y77IxomO" +
                        "uj3gySmzfeQt5JcAKgCrtfxDd5OBnfrtY26WBBTKhHJ1H5fxY_XskXsXjVQHaSX0" +
                        "o0nJiVGDefLZ-R9lEleHdXqxQa1O8WKXsanSQ"
        ).build();

        given()
                .when()
                .cookie(tokenCookie)
                .header(RIGHT_HEADER)
                .delete("/docs/tmp-file/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("Invalid token."));

        Date expireDate = new Date(new Date().getTime() - authProperties.getTokenExpireHours() * 3600);
        String token = jwtService.createJwtToken(
                AuthJwtToken.builder()
                        .userId(ADMIN_ID)
                        .xsrfToken(AUTH_ID.toString())
                        .build(),
                expireDate
        );

        tokenCookie = new Cookie.Builder(AuthConst.JWT_TOKEN_COOKIE, token).build();

        given()
                .when()
                .header(RIGHT_HEADER)
                .cookie(tokenCookie)
                .delete("/docs/tmp-file/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("Token is expired."));
    }
}
