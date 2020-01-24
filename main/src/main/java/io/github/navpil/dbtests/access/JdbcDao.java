package io.github.navpil.dbtests.access;

import io.github.navpil.dbtests.services.IJdbcDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcDao implements IJdbcDao {

    private final String url;
    private final String username;
    private final String password;

    public JdbcDao(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public List<Car> list() throws SQLException {
        final ArrayList<Car> cars = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, brand, max_speed FROM car");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                final Car car = new Car();
                car.setId(resultSet.getString("id"));
                car.setBrand(resultSet.getString("brand"));
                car.setMaxSpeed(resultSet.getInt("max_speed"));
                cars.add(car);
            }
        }
        return cars;
    }

    @Override
    public void save(Car car) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO car(id, brand, max_speed) VALUES (?, ?, ?)");
             ) {
            preparedStatement.setString(1, car.getId());
            preparedStatement.setString(2, car.getBrand());
            preparedStatement.setInt(3, car.getMaxSpeed());
            preparedStatement.execute();
        }

    }

}
