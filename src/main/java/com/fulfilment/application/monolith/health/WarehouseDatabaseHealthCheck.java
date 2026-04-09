package com.fulfilment.application.monolith.health;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class WarehouseDatabaseHealthCheck implements HealthCheck {

    @Inject
    WarehouseRepository warehouseRepository;

    @Override
    public HealthCheckResponse call() {
        try {
            long count = warehouseRepository.count();
            return HealthCheckResponse.named("warehouse-database")
                    .up()
                    .withData("warehouseCount", count)
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("warehouse-database")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}
