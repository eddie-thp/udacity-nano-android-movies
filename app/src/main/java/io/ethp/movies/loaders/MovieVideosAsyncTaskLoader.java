package io.ethp.movies.loaders;

import android.content.Context;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.List;

import io.ethp.movies.R;
import io.ethp.movies.model.Video;

public class MovieVideosAsyncTaskLoader extends AsyncTaskLoader<List<Video>> {

    private static final String LOG_TAG = MovieVideosAsyncTaskLoader.class.getSimpleName();

    private static final String REQUEST_METHOD = "GET";

    private static final String PARAM_MDB_API_KEY = "api_key";
    private static final String PARAM_PAGE = "page";

    private final String MDB_API_KEY;

    private int mPage;

    private long mMovieId;

    private List<Video> mVideos;

    public MovieVideosAsyncTaskLoader(Context context, long movieId) {
        this(context, movieId, 1);
    }

    public MovieVideosAsyncTaskLoader(Context context, long movieId, int page) {
        super(context);
        MDB_API_KEY = getContext().getString(R.string.movie_db_org_api_key);
        mMovieId = movieId;
        mPage = page;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        // TODO call fragment/activity startLoading() ==> show loading widget

        if (mVideos == null) {
            forceLoad();
        } else {
            deliverResult(mVideos);
        }
    }

    @Override
    public void deliverResult(@Nullable List<Video> data) {
        // Cache the result before delivering
        if (mVideos != data) {
            mVideos = data;
        }

        super.deliverResult(data);
    }

    @Nullable
    @Override
    public List<Video> loadInBackground() {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("api.themoviedb.org");
        builder.appendPath("3");
        builder.appendPath("movie");
        builder.appendPath(Long.toString(mMovieId));
        builder.appendPath("videos");
        builder.appendQueryParameter(PARAM_MDB_API_KEY, MDB_API_KEY);
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

        Video videos[] = processApiResponse(responseStr);

        if(videos != null) {
            // List<Review> reviewsList = Arrays.asList(reviews) adds NULL items in the array to the list, to prevent that I had to create the list myself
            // NULL items may exist if the Review object wasn't created correctly due to issues in the parsing and/or date formatting
            List<Video> videosList = new ArrayList<>();
            for(Video video : videos) {
                // Note, we are including trailer
                if(video != null && video.getType() == Video.VideoType.Trailer) {
                    videosList.add(video);
                }
            }

            return videosList;

        } else {
            // TODO Log / Toast
        }

        return null;
    }

    private Video[] processApiResponse(String responseStr) {
        Video videos[] = null;

        final String DISCOVER_RESULTS = "results";

        try {
            JSONObject responseJson = new JSONObject(responseStr);
            JSONArray responseResults = responseJson.getJSONArray(DISCOVER_RESULTS);

            videos = new Video[responseResults.length()];

            for(int i = 0; i < responseResults.length(); i++) {
                JSONObject reviewJSON = responseResults.getJSONObject(i);
                try {
                    videos[i] = new Video(reviewJSON);
                } catch(JSONException e) {
                    Log.e(LOG_TAG, "Failed parsing response json" + reviewJSON, e);
                }
            }
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Failed parsing response json" + responseStr, e);
        }

        return videos;
    }
}
