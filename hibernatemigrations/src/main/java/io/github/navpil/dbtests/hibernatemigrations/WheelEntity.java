package io.github.navpil.dbtests.hibernatemigrations;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "my_wheel")
public class WheelEntity {

    @Id
    private String id;
    @Column(name = "is_front")
    private boolean front;
    @Column(name = "is_left")
    private boolean left;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFront() {
        return front;
    }

    public void setFront(boolean front) {
        this.front = front;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    @Override
    public String toString() {
        return "WheelEntity{" +
                "id='" + id + '\'' +
                ", front=" + front +
                ", left=" + left +
                '}';
    }
}
