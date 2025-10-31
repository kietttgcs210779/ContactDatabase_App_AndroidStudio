package com.example.contactdatabaseappjava;

public class Contact {
    private long id;
    private String name;
    private String phone;
    private String email;

    public Contact(long id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
