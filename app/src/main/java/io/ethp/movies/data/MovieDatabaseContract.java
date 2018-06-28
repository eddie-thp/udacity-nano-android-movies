package io.ethp.movies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieDatabaseContract {

    public static final String AUTHORITY = "io.ethp.movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE = "release_date";
        public static final String COLUMN_POSTER_IMAGE_PATH = "poster_image_path";
        public static final String COLUMN_USER_RATING = "user_rating";

    }

}
