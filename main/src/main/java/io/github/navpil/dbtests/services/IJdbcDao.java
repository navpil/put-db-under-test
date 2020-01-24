package io.github.navpil.dbtests.services;

import io.github.navpil.dbtests.access.Car;

import java.sql.SQLException;
import java.util.List;

public interface IJdbcDao {

    List<Car> list() throws SQLException;

    void save(Car car) throws SQLException;

}
