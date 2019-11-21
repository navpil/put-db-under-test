package io.github.navpil.dbtests.access;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQuery(name = CarEntity.SELECT_ALL, query = "select c from io.github.navpil.dbtests.access.CarEntity c")
@Entity
@Table(name = "car")
public class CarEntity {

    public static final String SELECT_ALL = "CarEntity.SELECT_ALL";

    @Id
    private String id;

    private String brand;

    @Column(name = "max_speed")
    private int maxSpeed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public String toString() {
        return "CarEntity{" +
                "id='" + id + '\'' +
                ", brand='" + brand + '\'' +
                ", maxSpeed=" + maxSpeed +
                '}';
    }
}
