package io.ethp.movies;

import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.Arrays;

import io.ethp.movies.model.Movie;
import io.ethp.movies.widget.MovieImageArrayAdapter;

/**
 * Task responsible for fetching a list of movies from "www.themoviedb.org"
 *
 * API documentation can be found in: https://www.themoviedb.org/documentation/api/discover
 *
 * // TODO receive api key externally, or getString property here ? problem = lack of context
 *
 * Parameters:
 * [0] = apiKey
 * [1] = sorting
 */
public class FetchMoviesTask  extends AsyncTask<String, Void, Movie[]> {

    private static final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private MovieImageArrayAdapter mAdapter;

    public FetchMoviesTask(MovieImageArrayAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        // TODO Use parameter if passed or use default for sorting
        // String sorting = (params.length > 0 ? param[0] : getString(R.string.pref_catalog_sorting_default));
        String apiKey = params[0];
        String sorting = params[1];

        // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=daf267f1cbc202e37a035fc65cef8814
/*
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_catalog_sorting_key), getString(R.string.pref_catalog_sorting_default));
*/

        final String MDB_API_KEY_PARAM = "api_key";
        final String SORTING_PARAM = "sort_by";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("api.themoviedb.org");
        builder.appendPath("3");
        builder.appendPath("discover");
        builder.appendPath("movie");
        builder.appendQueryParameter(MDB_API_KEY_PARAM, apiKey);
        builder.appendQueryParameter(SORTING_PARAM, sorting);
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
            urlConnection.setRequestMethod("GET");
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

        return movies;
    }

    private Movie[] processApiResponse(String responseStr) {
        Movie movies[] = null;

        final String DISCOVER_RESULTS = "results";
        final String MOVIE_TITLE = "title";
        final String POSTER_PATH = "poster_path";

        try {
            JSONObject responseJson = new JSONObject(responseStr);
            JSONArray responseResults = responseJson.getJSONArray(DISCOVER_RESULTS);

            movies = new Movie[responseResults.length()];

            for(int i = 0; i< responseResults.length(); i++) {
                JSONObject movieJSON = responseResults.getJSONObject(i);
                movies[i] = new Movie();
                movies[i].setTitle(movieJSON.getString(MOVIE_TITLE));
                movies[i].setImage(movieJSON.getString(POSTER_PATH));

            }
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Failed parsing response json" + responseStr, e);
        }

        return movies;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);

        if(movies != null) {
            mAdapter.addAll(Arrays.asList(movies));
        } else {
            // TODO Log / Toast
        }
    }
}
