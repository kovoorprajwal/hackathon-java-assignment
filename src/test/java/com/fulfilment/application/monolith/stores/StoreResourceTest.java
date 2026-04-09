package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class StoreResourceTest {

    @Test
    void testListAllStores() {
        given()
            .when().get("/store")
            .then()
            .statusCode(200);
    }

    @Test
    void testGetStoreById_NotFound() {
        given()
            .when().get("/store/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void testCreateStore_Success() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"RESOURCE-TEST-STORE\",\"quantityProductsInStock\":5}")
            .when().post("/store")
            .then()
            .statusCode(201)
            .body(containsString("RESOURCE-TEST-STORE"));
    }

    @Test
    void testCreateStore_WithIdSet_Returns422() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"id\":999,\"name\":\"BAD-STORE\",\"quantityProductsInStock\":0}")
            .when().post("/store")
            .then()
            .statusCode(422);
    }

    @Test
    void testUpdateStore_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"UPDATED\",\"quantityProductsInStock\":3}")
            .when().put("/store/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void testUpdateStore_MissingName_Returns422() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"quantityProductsInStock\":3}")
            .when().put("/store/1")
            .then()
            .statusCode(422);
    }

    @Test
    void testDeleteStore_NotFound() {
        given()
            .when().delete("/store/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void testPatchStore_MissingName_Returns422() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"quantityProductsInStock\":3}")
            .when().patch("/store/1")
            .then()
            .statusCode(422);
    }

    @Test
    void testGetStoreById_Found() {
        int id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"GET-BY-ID-STORE\",\"quantityProductsInStock\":2}")
            .when().post("/store")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .when().get("/store/" + id)
            .then()
            .statusCode(200)
            .body(containsString("GET-BY-ID-STORE"));
    }

    @Test
    void testUpdateStore_Success() {
        int id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"UPDATE-STORE\",\"quantityProductsInStock\":2}")
            .when().post("/store")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"UPDATED-STORE\",\"quantityProductsInStock\":5}")
            .when().put("/store/" + id)
            .then()
            .statusCode(200)
            .body(containsString("UPDATED-STORE"));
    }

    @Test
    void testPatchStore_Success() {
        int id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"PATCH-STORE\",\"quantityProductsInStock\":2}")
            .when().post("/store")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"PATCHED-STORE\",\"quantityProductsInStock\":7}")
            .when().patch("/store/" + id)
            .then()
            .statusCode(200)
            .body(containsString("PATCHED-STORE"));
    }

    @Test
    void testDeleteStore_Success() {
        int id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"DELETE-ME-STORE\",\"quantityProductsInStock\":1}")
            .when().post("/store")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .when().delete("/store/" + id)
            .then()
            .statusCode(204);
    }
}
