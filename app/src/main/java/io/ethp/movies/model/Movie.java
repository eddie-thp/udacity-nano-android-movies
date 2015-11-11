package io.ethp.movies.model;

import java.io.Serializable;

/**
 * Class that represents the Movie information retrieved from "themoviedb.org"
 */
public class Movie implements Serializable {

    private String title;

    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
