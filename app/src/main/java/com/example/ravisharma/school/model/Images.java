package com.example.ravisharma.school.model;

/**
 * Created by Ravi Sharma on 10-Mar-18.
 */

public class Images {
    private String id;
    private String imageLink;

    public Images() {
    }

    public Images(String id, String imageLink) {
        this.id = id;
        this.imageLink = imageLink;
    }

    public String getImageLink() {
        return imageLink;
    }
}
