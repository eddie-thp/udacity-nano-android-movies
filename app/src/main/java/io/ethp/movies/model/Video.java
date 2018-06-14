package io.ethp.movies.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Video {

    public enum VideoType {
        Trailer, Teaser, Clip, Featurette;
    }

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_KEY = "key";
    private static final String JSON_SITE = "site";
    private static final String JSON_TYPE = "type";

    private String id;
    private String name;
    private String key;
    private String site;
    private VideoType type;

    public Video(JSONObject videoJSON) throws JSONException {
        id = videoJSON.getString(JSON_ID);
        name = videoJSON.getString(JSON_NAME);
        key = videoJSON.getString(JSON_KEY);
        site = videoJSON.getString(JSON_SITE);
        type = VideoType.valueOf(videoJSON.getString(JSON_TYPE));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public VideoType getType() {
        return type;
    }

    public void setType(VideoType type) {
        this.type = type;
    }
}
