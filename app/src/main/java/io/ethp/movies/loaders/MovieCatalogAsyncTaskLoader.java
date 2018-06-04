package io.ethp.movies.loaders;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.ethp.movies.R;
import io.ethp.movies.model.Movie;

public class MovieCatalogAsyncTaskLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MovieCatalogAsyncTaskLoader.class.getSimpleName();

    private static final String REQUEST_METHOD = "GET";

    private static final String PARAM_MDB_API_KEY = "api_key";
    private static final String PARAM_SORT_BY = "sort_by";
    private static final String PARAM_PAGE = "page";

    private final String MDB_API_KEY;

    private int mPage;

    private String mSortBy;

    private List<Movie> mMovies;

    public MovieCatalogAsyncTaskLoader(Context context) {
        this(context, 1);
    }

    public MovieCatalogAsyncTaskLoader(Context context, int page) {
        super(context);
        MDB_API_KEY = getContext().getString(R.string.movie_db_org_api_key);
        mPage = page;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = prefs.getString(context.getString(R.string.pref_catalog_sorting_key), context.getString(R.string.pref_catalog_sorting_default));

        // TODO call fragment/activity startLoading() ==> show loading widget

        if (mMovies == null || mSortBy != sortBy) {
            // Force load if sortBy preference has changed
            mSortBy = sortBy;
            forceLoad();
        } else {
            deliverResult(mMovies);
        }
    }

    @Override
    public void deliverResult(@Nullable List<Movie> data) {
        // Cache the result before delivering
        if (mMovies != data) {
            mMovies = data;
        }

        super.deliverResult(data);
    }

    @Nullable
    @Override
    public List<Movie> loadInBackground() {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("api.themoviedb.org");
        builder.appendPath("3");
        builder.appendPath("discover");
        builder.appendPath("movie");
        builder.appendQueryParameter(PARAM_MDB_API_KEY, MDB_API_KEY);
        builder.appendQueryParameter(PARAM_SORT_BY, mSortBy);
        builder.appendQueryParameter(PARAM_PAGE, Integer.toString(mPage));

        Uri mDbApiUri = builder.build();

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String responseStr = null;
        try {
            URL mDbApiUrl = new URL(builder.build().toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) mDbApiUrl.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            responseStr = buffer.toString();

        } catch(Exception e) {
            Log.e(LOG_TAG, "Failed to fetch movies: " + mDbApiUri, e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Movie movies[] = processApiResponse(responseStr);

        if(movies != null) {
            // List<Movie> moviesList = Arrays.asList(movies) adds NULL items in the array to the list, to prevent that I had to create the list myself
            // NULL items may exist if the Movie object wasn't created correctly due to issues in the parsing and/or date formatting
            List<Movie> moviesList = new ArrayList<Movie>();
            for(Movie movie : movies) {
                if(movie != null) {
                    moviesList.add(movie);
                }
            }

            return moviesList;

        } else {
            // TODO Log / Toast
        }

        return null;
    }

    private Movie[] processApiResponse(String responseStr) {
        Movie movies[] = null;

        final String DISCOVER_RESULTS = "results";

        try {
            JSONObject responseJson = new JSONObject(responseStr);
            JSONArray responseResults = responseJson.getJSONArray(DISCOVER_RESULTS);

            movies = new Movie[responseResults.length()];

            for(int i = 0; i< responseResults.length(); i++) {
                JSONObject movieJSON = responseResults.getJSONObject(i);
                try {
                    movies[i] = new Movie(movieJSON);
                } catch(JSONException e) {
                    Log.e(LOG_TAG, "Failed parsing response json" + movieJSON, e);
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Failed parsing response json date" + movieJSON, e);
                }
            }
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Failed parsing response json" + responseStr, e);
        }

        return movies;
    }

}
