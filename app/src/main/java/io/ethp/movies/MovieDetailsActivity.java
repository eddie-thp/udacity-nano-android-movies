package io.ethp.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import io.ethp.movies.adapters.ReviewAdapter;
import io.ethp.movies.loaders.MovieReviewsAsyncTaskLoader;
import io.ethp.movies.model.Movie;
import io.ethp.movies.model.Review;

public class MovieDetailsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Review>> {

        private static final SimpleDateFormat sdf = new SimpleDateFormat("mm/yyyy");

        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();

        private ReviewAdapter mReviewsAdapter;

        private RecyclerView mReviewsRecyclerView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

            Intent intent = getActivity().getIntent();
            if(intent!=null && intent.getSerializableExtra(Intent.EXTRA_TEXT) != null) {
                Movie movie  = (Movie) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.textViewTitle)).setText(movie.getTitle());
                movie.loadImage((ImageView) rootView.findViewById(R.id.imageViewPoster));
                ((TextView) rootView.findViewById(R.id.textViewReleaseDate)).setText(sdf.format(movie.getRelease()));
                ((RatingBar) rootView.findViewById(R.id.ratingBarUserRating)).setRating((float) movie.getUserRating() / 2);
                ((TextView) rootView.findViewById(R.id.textViewRating)).setText("(" + String.valueOf(movie.getUserRating()) + ")");
                ((TextView) rootView.findViewById(R.id.textViewOverview)).setText(movie.getOverview());


                // Setup Recycler View:
                // 1 - Set the layout manager
                // 2 - Set that items will have a fixed size
                // 3 - Set the adapter
                mReviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviewsRecyclerView);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                mReviewsRecyclerView.setLayoutManager(linearLayoutManager);

                mReviewsRecyclerView.setHasFixedSize(true);

                mReviewsAdapter = new ReviewAdapter();
                mReviewsRecyclerView.setAdapter(mReviewsAdapter);

                // See: https://stackoverflow.com/questions/31088404/difference-between-getloadermanger-and-getactivity-getsupportloadermanager
                // Explains that getLoaderManager() from v4.app.Fragment should be the same as calling getSupportLoaderManager() from v4.app.FragmentAcivity
                LoaderManager loaderManager = getLoaderManager();

                Loader<List<Review>> reviewsLoader = loaderManager.getLoader(LOADER_REVIEW_ID);

                // TODO ASK: Why would I use the bundle, if the loader callbacks are implemented in this class ???
                Bundle loadReviewsCatalogBundle = new Bundle();
                loadReviewsCatalogBundle.putLong("MOVIE_ID", movie.getId());
                if (reviewsLoader == null) {
                    loaderManager.initLoader(LOADER_REVIEW_ID, loadReviewsCatalogBundle, this);
                }



            }

            return rootView;
        }

        /// REVIEW LOADER CALLBACK

        private static final int LOADER_REVIEW_ID = 101;


        @NonNull
        @Override
        public Loader<List<Review>> onCreateLoader(int id, @Nullable Bundle args) {
            switch (id) {

                case LOADER_REVIEW_ID:
                    final long movieId = args.getLong("MOVIE_ID");
                    return new MovieReviewsAsyncTaskLoader(getContext(), movieId);
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
            }        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Review>> loader) {

        }

    }


}