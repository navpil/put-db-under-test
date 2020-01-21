package io.github.navpil.dbtests.access;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Properties;

public class HibernateNativeDao {

    private final SessionFactory sessionFactory;

    public HibernateNativeDao(String url, String username, String password) {

        final Configuration configuration = new Configuration();
        Properties properties = new Properties();
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", username);
        properties.put("hibernate.connection.password", password);
        properties.put("show_sql", "true");
//        properties.put("hbm2ddl.auto", "update");
        configuration.setProperties(properties);
        configuration.addAnnotatedClass(CarEntity.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    public List<CarEntity> getCars() {
        try (final Session session = sessionFactory.openSession()) {
            final List<CarEntity> cars = session.createQuery("from CarEntity", CarEntity.class).list();
            return cars;
        }
    }

}
