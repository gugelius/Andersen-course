package org.example;

public class CoworkingSpace {
    private int id;
    private String type;
    private float price;
    private boolean status;

    public CoworkingSpace(int id, String type, float price, boolean status) {
        this.id = id;
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
    public String toString(){
        return "ID: " + id + ", Type: " + type + ", Price: " + price + ", Available: " + status;
    }
}
