package com.example.universities.autorization;

import java.util.List;

/**
 * User information
 */
public class User {
    public String userName;
    public String uriImage;
    public List<String> favouritesUniversities;

    public User() {

    }

    public User(String userName, List<String> favouritesUniversities) {
        this.userName = userName;
        this.favouritesUniversities = favouritesUniversities;
    }

    public User(String userName, List<String> favouritesUniversities, String uriImage) {
        this.userName = userName;
        this.favouritesUniversities = favouritesUniversities;
        this.uriImage = uriImage;
    }

}
