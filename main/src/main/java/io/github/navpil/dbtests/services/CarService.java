package io.github.navpil.dbtests.services;

import io.github.navpil.dbtests.access.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CarService {

    private static final Logger LOG = LoggerFactory.getLogger(CarService.class);
    private IJdbcDao dao;

    public CarService(IJdbcDao dao) {
        this.dao = dao;
    }

    public List<Car> list() {
        try {
            return dao.list();
        } catch (SQLException e) {
            LOG.warn("Error while fetching cars", e);
            return Collections.emptyList();
        }
    }

    public boolean save(Car car) {
        try {
            dao.save(car);
            return true;
        } catch (SQLException e) {
            LOG.warn("Error while saving a car", e);
            return false;
        }
    }
}
