package io.ethp.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.ethp.movies.data.MovieDatabaseContract.MovieEntry;

/**
 * Movie SQL
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ethp-movie.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID                          + " INTEGER PRIMARY KEY, "  +
                    MovieEntry.COLUMN_TITLE                 + " TEXT NOT NULL, "        +
                    MovieEntry.COLUMN_OVERVIEW              + " TEXT NOT NULL,"         +
                    MovieEntry.COLUMN_RELEASE               + " INTEGER NOT NULL, "     +
                    MovieEntry.COLUMN_POSTER_IMAGE_PATH     + " TEXT NOT NULL, "        +
                    MovieEntry.COLUMN_USER_RATING           + " REAL NOT NULL "        +
            ");";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}