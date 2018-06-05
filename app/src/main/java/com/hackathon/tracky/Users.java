package com.hackathon.tracky;

public class Users {
    String fullnames, email;

    public Users(String fullnames, String email) {
        this.fullnames = fullnames;
        this.email = email;
    }

    public Users() {
        this.fullnames = "";
        this.email = "";
    }

    public String getFullnames() {
        return fullnames;
    }

    public void setFullnames(String fullnames) {
        this.fullnames = fullnames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}