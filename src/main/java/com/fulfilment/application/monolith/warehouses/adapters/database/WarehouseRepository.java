package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  private static final Logger LOGGER = Logger.getLogger(WarehouseRepository.class);

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void create(Warehouse warehouse) {
    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;
    
    this.persist(dbWarehouse);
    LOGGER.infof("Warehouse persisted: businessUnitCode=%s, location=%s", warehouse.businessUnitCode, warehouse.location);
  }

  @Override
  @Transactional
  public void update(Warehouse warehouse) {
    DbWarehouse dbWarehouse = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (dbWarehouse != null) {
      dbWarehouse.location = warehouse.location;
      dbWarehouse.capacity = warehouse.capacity;
      dbWarehouse.stock = warehouse.stock;
      dbWarehouse.archivedAt = warehouse.archivedAt;
    } else {
      LOGGER.warnf("Update skipped: warehouse with businessUnitCode '%s' not found", warehouse.businessUnitCode);
    }
  }

  @Override
  @Transactional
  public void remove(Warehouse warehouse) {
    DbWarehouse dbWarehouse = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (dbWarehouse != null) {
      this.delete(dbWarehouse);
    } else {
      LOGGER.warnf("Remove skipped: warehouse with businessUnitCode '%s' not found", warehouse.businessUnitCode);
    }
  }

  @Override
  @Transactional
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse dbWarehouse = find("businessUnitCode", buCode).firstResult();
    if (dbWarehouse == null) {
      LOGGER.debugf("Warehouse lookup returned no result for businessUnitCode=%s", buCode);
    }
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }
}
