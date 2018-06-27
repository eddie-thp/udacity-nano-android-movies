package io.ethp.movies.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.ethp.movies.data.MovieDatabaseContract.MovieEntry;


/**
 * Class that represents the Movie information retrieved from "themoviedb.org"
 */
public class Movie implements Serializable {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_POSTER_IMAGE_PATH = "poster_path";
    private static final String JSON_USER_RATING = "vote_average";
    private static final String JSON_RELEASE_DATE = "release_date";

    private long id;
    private String title;
    private String posterImagePath;
    private String overview;
    private double userRating;
    private Date release;

    public Movie(JSONObject movieJson) throws JSONException, ParseException {
        this.id = movieJson.getLong(JSON_ID);
        this.title = movieJson.getString(JSON_TITLE);
        this.overview = movieJson.getString(JSON_OVERVIEW);
        this.release = sdf.parse(movieJson.getString(JSON_RELEASE_DATE));
        this.posterImagePath = movieJson.getString(JSON_POSTER_IMAGE_PATH);
        this.userRating = movieJson.getDouble(JSON_USER_RATING);
    }

    public Movie(Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(MovieEntry._ID));
        this.title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
        this.overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
        this.release = new Date(cursor.getLong(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE)));
        this.posterImagePath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_IMAGE_PATH));
        this.userRating = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_USER_RATING));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterImagePath() {
        return posterImagePath;
    }

    public void setPosterImagePath(String posterImagePath) {
        this.posterImagePath = posterImagePath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    public void loadImage(ImageView imageView) {
        Context context = imageView.getContext();

        // E.g. http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("image.tmdb.org");
        builder.appendPath("t");
        builder.appendPath("p");
        builder.appendPath("w185");
        builder.appendEncodedPath(this.getPosterImagePath());

        Picasso.with(context).load(builder.build()).into(imageView);
    }

    public boolean isFavorite(SQLiteDatabase movieDatabase) {
        String whereArgs[] = { Long.toString(getId()) };
        Cursor cursor = movieDatabase.query(MovieEntry.TABLE_NAME,  null,MovieEntry._ID + " = ?", whereArgs, null, null, null);
        boolean favorite = (cursor.getCount() == 1);
        cursor.close();
        return favorite;
    }

    public void setFavorite(boolean favorite, SQLiteDatabase movieDatabase) {
        if (favorite) {
            ContentValues cv = new ContentValues();
            cv.put(MovieEntry._ID, getId());
            cv.put(MovieEntry.COLUMN_TITLE, getTitle());
            cv.put(MovieEntry.COLUMN_OVERVIEW, getOverview());
            cv.put(MovieEntry.COLUMN_RELEASE, getRelease().getTime());
            cv.put(MovieEntry.COLUMN_POSTER_IMAGE_PATH, getPosterImagePath());
            cv.put(MovieEntry.COLUMN_USER_RATING, getUserRating());

            movieDatabase.insert(MovieEntry.TABLE_NAME, null, cv);
        } else {
            String[] deleteArgs = { Long.toString(getId()) };
            movieDatabase.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + " = ?", deleteArgs);
        }
    }
}
