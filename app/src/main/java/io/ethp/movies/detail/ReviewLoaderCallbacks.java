package io.ethp.movies.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import io.ethp.movies.adapters.ReviewAdapter;
import io.ethp.movies.loaders.MovieReviewsAsyncTaskLoader;
import io.ethp.movies.model.Review;

public class ReviewLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Review>> {

    private Context mContext;

    private ReviewAdapter mReviewsAdapter;

    public ReviewLoaderCallbacks(Context context, ReviewAdapter reviewsAdapter) {
        this.mContext = context;
        this.mReviewsAdapter = reviewsAdapter;
    }

    @NonNull
    @Override
    public Loader<List<Review>> onCreateLoader(int id, @Nullable Bundle args) {
        final long movieId = args.getLong("MOVIE_ID");

        switch (id) {
            case MovieDetailsActivity.LOADER_REVIEW_ID:
                return new MovieReviewsAsyncTaskLoader(mContext, movieId);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Review>> loader, List<Review> data) {
        if(data != null) {
            // Cleaning up the adapter contents
            // TODO - I'm currently not handling data paging, I guess this will have to change
            mReviewsAdapter.clear();
            mReviewsAdapter.addAll(data);
        } else {
            // TODO Log / Toast
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Review>> loader) {

    }
}
