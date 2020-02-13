package io.github.navpil.dbtests.hibernatemigrations;

import io.github.navpil.dbtests.Credentials;
import io.github.navpil.dbtests.recreate.SqlServerRecreate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class GenerateDataWithUnorderedWheels {

    public static void main(String[] args) throws SQLException {
        final String dbname = "carrental4";

        new SqlServerRecreate().recreateDb(
                new Credentials(
                        "jdbc:sqlserver://localhost;;integratedSecurity=true;",
                        "",
                        ""
                ),
                dbname);


        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.put("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        properties.put("hibernate.connection.url", "jdbc:sqlserver://localhost;databaseName=" + dbname+ ";integratedSecurity=true;");
        properties.put("hibernate.connection.username", "");
        properties.put("hibernate.connection.password", "");
        properties.put("show_sql", "true");

        properties.put("hibernate.hbm2ddl.auto", "update");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Cars", properties);


        final CarEntity carEntity = new CarEntity();
        carEntity.setId("2");
        carEntity.setName("Ford");
        carEntity.setWheels(new ArrayList<>(Arrays.asList(
                wheel("4", true, true),
                wheel("5", true, false),
                wheel("6", false, true),
                wheel("7", false, false)
        )));
        final EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(carEntity);
        em.getTransaction().commit();

        final CarEntity carEntity1 = emf.createEntityManager().find(CarEntity.class, "2");
        System.out.println(carEntity1);

        emf.close();
    }

    private static WheelEntity wheel(String id, boolean front, boolean left) {
        final WheelEntity w = new WheelEntity();
        w.setId(id);
        w.setFront(front);
        w.setLeft(left);
        return w;
    }

}
