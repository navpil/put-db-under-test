package io.github.navpil.dbtests.access;

import com.querydsl.jpa.impl.JPAQuery;
import io.github.navpil.dbtests.SQLConfig;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Properties;

public class QueryDSLDao {

    private final EntityManagerFactory emf;
    private SQLConfig config = SQLConfig.getDefault();

    public QueryDSLDao(String url, String username, String password) {

        Properties properties = new Properties();
        properties.put("hibernate.dialect", config.dialect);
        properties.put("hibernate.connection.driver_class", config.driver);
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", username);
        properties.put("hibernate.connection.password", password);
        properties.put("show_sql", "true");
//        properties.put("hbm2ddl.auto", "update");
        emf = Persistence.createEntityManagerFactory("Cars", properties);


    }

    public List<CarEntity> getCars() {
        QCarEntity car = QCarEntity.carEntity;
        JPAQuery<?> query = new JPAQuery<Void>(emf.createEntityManager());

        return query.select(car)
                .from(car)
                .where(car.maxSpeed.goe(200))
                .fetch();

    }

}
