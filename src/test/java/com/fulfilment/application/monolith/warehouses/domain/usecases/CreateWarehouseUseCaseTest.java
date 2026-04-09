package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private LocationResolver locationResolver;
    private CreateWarehouseUseCase createWarehouseUseCase;

    private static final Location VALID_LOCATION = new Location("AMSTERDAM-001", 5, 100);

    @BeforeEach
    void setup() {
        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);
        createWarehouseUseCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
    }

    // --- Positive ---

    @Test
    void testCreateWarehouseSuccessfully() {
        when(warehouseStore.findByBusinessUnitCode("MWH.NEW")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(VALID_LOCATION);

        Warehouse warehouse = buildWarehouse("MWH.NEW", "AMSTERDAM-001", 50, 10);

        assertDoesNotThrow(() -> createWarehouseUseCase.create(warehouse));

        verify(warehouseStore).create(warehouse);
        assertNotNull(warehouse.createdAt);
    }

    // --- Negative / Error ---

    @Test
    void testCreateWarehouseFailsWhenBusinessUnitCodeAlreadyExists() {
        when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(new Warehouse());

        Warehouse warehouse = buildWarehouse("MWH.001", "AMSTERDAM-001", 50, 10);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createWarehouseUseCase.create(warehouse));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void testCreateWarehouseFailsWhenLocationIsInvalid() {
        when(warehouseStore.findByBusinessUnitCode("MWH.NEW")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("INVALID-LOC")).thenReturn(null);

        Warehouse warehouse = buildWarehouse("MWH.NEW", "INVALID-LOC", 10, 5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createWarehouseUseCase.create(warehouse));

        assertTrue(ex.getMessage().contains("not valid"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void testCreateWarehouseFailsWhenCapacityExceedsLocationMax() {
        when(warehouseStore.findByBusinessUnitCode("MWH.NEW")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(VALID_LOCATION);

        Warehouse warehouse = buildWarehouse("MWH.NEW", "AMSTERDAM-001", 150, 10);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createWarehouseUseCase.create(warehouse));

        assertTrue(ex.getMessage().contains("exceeds location max capacity"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void testCreateWarehouseFailsWhenStockExceedsCapacity() {
        when(warehouseStore.findByBusinessUnitCode("MWH.NEW")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(VALID_LOCATION);

        Warehouse warehouse = buildWarehouse("MWH.NEW", "AMSTERDAM-001", 30, 50);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createWarehouseUseCase.create(warehouse));

        assertTrue(ex.getMessage().contains("exceeds warehouse capacity"));
        verify(warehouseStore, never()).create(any());
    }

    // --- Helper ---

    private Warehouse buildWarehouse(String code, String location, int capacity, int stock) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = code;
        w.location = location;
        w.capacity = capacity;
        w.stock = stock;
        return w;
    }
}
