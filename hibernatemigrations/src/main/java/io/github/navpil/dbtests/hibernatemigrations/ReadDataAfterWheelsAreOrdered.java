package io.github.navpil.dbtests.hibernatemigrations;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.util.Properties;

public class ReadDataAfterWheelsAreOrdered {

    public static void main(String[] args) {
        final String dbname = "carrental4";

        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.put("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        properties.put("hibernate.connection.url", "jdbc:sqlserver://localhost;databaseName=" + dbname+ ";integratedSecurity=true;");
        properties.put("hibernate.connection.username", "");
        properties.put("hibernate.connection.password", "");
        properties.put("show_sql", "true");

        properties.put("hibernate.hbm2ddl.auto", "update");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Cars", properties);

        final CarEntity carEntity1 = emf.createEntityManager().find(CarEntity.class, "2");
        System.out.println(carEntity1);

        emf.close();
    }

}
