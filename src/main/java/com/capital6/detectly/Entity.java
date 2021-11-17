package com.capital6.detectly;

public class
Entity extends EmployeeDetail{

    String address;

    public Entity(String content, String address) {
        super(content);
        this.address = address;
    }

    public Entity(String content, String address, boolean isValid, String msg) {
        super(content, isValid, msg);
        this.address = address;
    }

    public Entity() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
