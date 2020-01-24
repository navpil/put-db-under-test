package io.github.navpil.dbtests.access;

import io.github.navpil.dbtests.SQLConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class HibernateJpaDao {

    private final EntityManagerFactory emf;
    private SQLConfig config = SQLConfig.getDefault();

    public HibernateJpaDao(String url, String username, String password) {
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

    public List<CarEntity> getNativeQueryCars() {
        return withEm(em -> (List<CarEntity>)em.createNativeQuery("select * from car", CarEntity.class).getResultList());
    }

    public List<CarEntity> getJpqlCars() {
        return withEm(em -> em.createNamedQuery(CarEntity.SELECT_ALL, CarEntity.class).getResultList());
    }

    public List<CarEntity> getCriteriaCars() {
        return withEm(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<CarEntity> q = cb.createQuery(CarEntity.class);
            Root<CarEntity> c = q.from(CarEntity.class);
            q.select(c).where(cb.gt(c.get("maxSpeed"), 200));

            final TypedQuery<CarEntity> query = em.createQuery(q);
            return query.getResultList();
        });
    }

    public <T> T withEm(Function<EntityManager, T> f) {
        final EntityManager em = emf.createEntityManager();
        try {
            return f.apply(em);
        } finally {
            em.close();
        }
    }

}
