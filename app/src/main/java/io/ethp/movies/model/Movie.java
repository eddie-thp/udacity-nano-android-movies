package io.ethp.movies.model;

/**
 * Class that represents the Movie information retrieved from "themoviedb.org"
 */
public class Movie {

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
