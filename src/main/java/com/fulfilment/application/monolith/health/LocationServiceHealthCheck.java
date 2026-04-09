package com.fulfilment.application.monolith.health;

import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class LocationServiceHealthCheck implements HealthCheck {

    @Inject
    LocationResolver locationResolver;

    @Override
    public HealthCheckResponse call() {
        try {
            boolean available = locationResolver.resolveByIdentifier("AMSTERDAM-001") != null;
            return HealthCheckResponse.named("location-service")
                    .status(available)
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("location-service")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}
