package com.fulfilment.application.monolith.health;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class HealthCheckTest {

    @Test
    void testReadinessCheck_Up() {
        given()
            .when().get("/q/health/ready")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.find { it.name == 'warehouse-database' }.status", equalTo("UP"));
    }

    @Test
    void testLivenessCheck_Up() {
        given()
            .when().get("/q/health/live")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.find { it.name == 'location-service' }.status", equalTo("UP"));
    }

    @Test
    void testOverallHealth_Up() {
        given()
            .when().get("/q/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }
}
