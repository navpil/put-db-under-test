package io.github.navpil.dbtests.access;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTemplateDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateDao(String url, String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
//        dataSource.setDriverClassName("");

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Car> getCars() {
        return jdbcTemplate.query("SELECT id, brand, max_speed FROM car", new RowMapper<Car>() {
            @Override
            public Car mapRow(ResultSet resultSet, int i) throws SQLException {
                final Car car = new Car();
                car.setId(resultSet.getString("id"));
                car.setBrand(resultSet.getString("brand"));
                car.setMaxSpeed(resultSet.getInt("max_speed"));
                return car;
            }
        });
    }

}
