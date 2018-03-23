package com.example.ravisharma.school.model;

/**
 * Created by Ravi Sharma on 05-Mar-18.
 */

public class Info {

    private String id, first_name, last_name, email, gender;

    public Info() {

    }

    public Info(String id, String first_name, String last_name, String email, String gender) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public void setId(String id) {
        this.id = id;
    }
}
