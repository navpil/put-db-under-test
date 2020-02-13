package io.github.navpil.dbtests.hibernatemigrations;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class CarEntity {

    @Id
    private String id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id")
//    @javax.persistence.OrderColumn(name = "wheel_order")
    private List<WheelEntity> wheels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WheelEntity> getWheels() {
        return wheels;
    }

    public void setWheels(List<WheelEntity> wheels) {
        this.wheels = wheels;
    }

    @Override
    public String toString() {
        return "CarEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", wheels=" + wheels +
                '}';
    }
}
