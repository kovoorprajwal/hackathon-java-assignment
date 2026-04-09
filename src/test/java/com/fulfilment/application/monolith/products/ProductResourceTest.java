package com.fulfilment.application.monolith.products;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class ProductResourceTest {

    @Test
    void testGetProductById_Found() {
        int id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"GET-PRODUCT\",\"description\":\"desc\",\"price\":10.0,\"stock\":5}")
            .when().post("/product")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .when().get("/product/" + id)
            .then()
            .statusCode(200)
            .body(containsString("GET-PRODUCT"));
    }

    @Test
    void testGetProductById_NotFound() {
        given()
            .when().get("/product/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void testCreateProduct_Success() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"NEW-PRODUCT\",\"description\":\"A product\",\"price\":5.0,\"stock\":10}")
            .when().post("/product")
            .then()
            .statusCode(201)
            .body(containsString("NEW-PRODUCT"));
    }

    @Test
    void testCreateProduct_WithIdSet_Returns422() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"id\":999,\"name\":\"BAD-PRODUCT\",\"price\":1.0,\"stock\":1}")
            .when().post("/product")
            .then()
            .statusCode(422);
    }

    @Test
    void testUpdateProduct_Success() {
        int id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"UPDATE-PRODUCT\",\"description\":\"desc\",\"price\":10.0,\"stock\":5}")
            .when().post("/product")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"UPDATED-PRODUCT\",\"description\":\"new desc\",\"price\":15.0,\"stock\":8}")
            .when().put("/product/" + id)
            .then()
            .statusCode(200)
            .body(containsString("UPDATED-PRODUCT"));
    }

    @Test
    void testUpdateProduct_MissingName_Returns422() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"price\":5.0,\"stock\":3}")
            .when().put("/product/1")
            .then()
            .statusCode(422);
    }

    @Test
    void testUpdateProduct_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"GHOST\",\"price\":1.0,\"stock\":1}")
            .when().put("/product/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void testDeleteProduct_NotFound() {
        given()
            .when().delete("/product/99999")
            .then()
            .statusCode(404);
    }
}
