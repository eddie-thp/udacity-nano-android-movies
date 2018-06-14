package io.ethp.movies.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import io.ethp.movies.adapters.VideoAdapter;
import io.ethp.movies.loaders.MovieVideosAsyncTaskLoader;
import io.ethp.movies.model.Video;

public class VideoLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Video>> {

    private Context mContext;

    private VideoAdapter mVideosAdapter;

    public VideoLoaderCallbacks(Context context, VideoAdapter videosAdapter) {
        this.mContext = context;
        this.mVideosAdapter = videosAdapter;
    }

    @NonNull
    @Override
    public Loader<List<Video>> onCreateLoader(int id, @Nullable Bundle args) {
        final long movieId = args.getLong("MOVIE_ID");

        switch (id) {
            case MovieDetailsActivity.LOADER_VIDEO_ID:
                return new MovieVideosAsyncTaskLoader(mContext, movieId);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Video>> loader, List<Video> data) {
        if(data != null) {
            // Cleaning up the adapter contents
            // TODO - I'm currently not handling data paging, I guess this will have to change
            mVideosAdapter.clear();
            mVideosAdapter.addAll(data);
        } else {
            // TODO Log / Toast
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Video>> loader) {

    }
}
