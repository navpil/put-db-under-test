package io.github.navpil.dbtests.access;

public class Car {

    private String id;
    private String brand;
    private int maxSpeed;

    public Car() {
    }

    public Car(String id, String brand, int maxSpeed) {
        this.id = id;
        this.brand = brand;
        this.maxSpeed = maxSpeed;
    }

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
        return "Car{" +
                "id='" + id + '\'' +
                ", brand='" + brand + '\'' +
                ", maxSpeed=" + maxSpeed +
                '}';
    }
}
