package org.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "spaces")
public class CoworkingSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_id")
    private int id;

    @Column(name = "space_type")
    private String type;

    @Column(name = "space_price")
    private float price;

    @Column(name = "space_availability")
    private boolean status;

    public CoworkingSpace() {
    }

    public CoworkingSpace(String type, float price, boolean status) {
        this.type = type;
        this.price = price;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CoworkingSpace{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
