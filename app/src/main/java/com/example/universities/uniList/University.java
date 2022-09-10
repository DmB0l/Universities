package com.example.universities.uniList;

/**
 * University info
 */
public class University {
    String image;
    String name;
    String des;

    public University(String image, String name, String des) {
        this.image = image;
        this.name = name;
        this.des = des;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
