package io.github.navpil.dbtests.access;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Properties;

public class HibernateJpaDao {

    private final EntityManagerFactory emf;

    public HibernateJpaDao(String url, String username, String password) {
        Properties properties = new Properties();
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", username);
        properties.put("hibernate.connection.password", password);
        properties.put("show_sql", "true");
//        properties.put("hbm2ddl.auto", "update");
        emf = Persistence.createEntityManagerFactory("Cars", properties);
    }

    public List<CarEntity> getNativeQueryCars() {
        final EntityManager em = emf.createEntityManager();
        final List<CarEntity> cars = em.createNativeQuery("select * from car", CarEntity.class).getResultList();
        return cars;
    }

    public List<CarEntity> getJpqlCars() {
        final EntityManager em = emf.createEntityManager();
        final List<CarEntity> cars = em.createNamedQuery(CarEntity.SELECT_ALL, CarEntity.class).getResultList();
        return cars;
    }

    public List<CarEntity> getCriteriaCars() {
        final EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<CarEntity> q = cb.createQuery(CarEntity.class);
        Root<CarEntity> c = q.from(CarEntity.class);
        q.select(c).where(cb.gt(c.get("maxSpeed"), 200));

        final TypedQuery<CarEntity> query = em.createQuery(q);
        return query.getResultList();
        //Some EntityManager handling
    }

}
