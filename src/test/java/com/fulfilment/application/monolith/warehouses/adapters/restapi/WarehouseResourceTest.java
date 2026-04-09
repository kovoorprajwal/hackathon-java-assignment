package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class WarehouseResourceTest {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setup() {
        em.createQuery("DELETE FROM DbWarehouse").executeUpdate();
        DbWarehouse w = new DbWarehouse();
        w.businessUnitCode = "MWH.TEST.001";
        w.location = "AMSTERDAM-001";
        w.capacity = 100;
        w.stock = 50;
        w.createdAt = LocalDateTime.now();
        em.persist(w);
    }

    @Test
    void testListAllWarehouses() {
        given()
            .when().get("/warehouse")
            .then()
            .statusCode(200)
            .body(containsString("MWH.TEST.001"));
    }

    @Test
    void testGetWarehouseByCode_Found() {
        given()
            .when().get("/warehouse/MWH.TEST.001")
            .then()
            .statusCode(200)
            .body(containsString("MWH.TEST.001"), containsString("AMSTERDAM-001"));
    }

    @Test
    void testGetWarehouseByCode_NotFound() {
        given()
            .when().get("/warehouse/NONEXISTENT")
            .then()
            .statusCode(404);
    }

    @Test
    void testCreateWarehouse_Success() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"businessUnitCode\":\"MWH.TEST.NEW\",\"location\":\"AMSTERDAM-001\",\"capacity\":50,\"stock\":10}")
            .when().post("/warehouse")
            .then()
            .statusCode(200)
            .body(containsString("MWH.TEST.NEW"));
    }

    @Test
    void testCreateWarehouse_InvalidLocation_Returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"businessUnitCode\":\"MWH.TEST.BAD\",\"location\":\"INVALID-LOC\",\"capacity\":10,\"stock\":5}")
            .when().post("/warehouse")
            .then()
            .statusCode(400);
    }

    @Test
    void testCreateWarehouse_DuplicateCode_Returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"businessUnitCode\":\"MWH.TEST.001\",\"location\":\"AMSTERDAM-001\",\"capacity\":10,\"stock\":5}")
            .when().post("/warehouse")
            .then()
            .statusCode(400);
    }

    @Test
    void testArchiveWarehouse_Success() {
        given()
            .when().delete("/warehouse/MWH.TEST.001")
            .then()
            .statusCode(204);
    }

    @Test
    void testArchiveWarehouse_NotFound_Returns404() {
        given()
            .when().delete("/warehouse/NONEXISTENT")
            .then()
            .statusCode(404);
    }

    @Test
    void testReplaceWarehouse_Success() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"businessUnitCode\":\"MWH.TEST.001\",\"location\":\"TILBURG-001\",\"capacity\":30,\"stock\":10}")
            .when().post("/warehouse/MWH.TEST.001/replacement")
            .then()
            .statusCode(200)
            .body(containsString("TILBURG-001"));
    }

    @Test
    void testReplaceWarehouse_NotFound_Returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"businessUnitCode\":\"NONEXISTENT\",\"location\":\"TILBURG-001\",\"capacity\":30,\"stock\":10}")
            .when().post("/warehouse/NONEXISTENT/replacement")
            .then()
            .statusCode(400);
    }
}
