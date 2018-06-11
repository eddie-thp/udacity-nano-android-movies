package io.ethp.movies.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class Review {
    private static final String JSON_ID = "id";
    private static final String JSON_AUTHOR = "author";
    private static final String JSON_CONTENT = "content";
    private static final String JSON_URL = "url";

    private String id;
    private String author;
    private String content;
    private String url;

    public Review(JSONObject reviewJSON) throws JSONException {
        this.id = reviewJSON.getString(JSON_ID);
        this.author = reviewJSON.getString(JSON_AUTHOR);
        this.content = reviewJSON.getString(JSON_CONTENT);
        this.url = reviewJSON.getString(JSON_URL);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
